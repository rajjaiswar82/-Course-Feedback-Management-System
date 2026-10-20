# Puppet Configuration Design - Task 13

## Overview

This document describes the design and implementation of the Puppet configuration management solution for the Course Feedback Management System.

## Architecture

### Deployment Model
- **Mode**: Masterless (puppet apply)
- **Reason**: Single-node deployment, simpler for development/testing, no server infrastructure needed
- **Target OS**: Ubuntu 22.04 (WSL2)

### Module Structure

```
puppet/
├── manifests/
│   └── site.pp                 # Main entry point, node classification
├── modules/
│   └── feedback_app/
│       ├── manifests/
│       │   ├── init.pp         # Main class with parameters
│       │   ├── install.pp      # Package installation
│       │   ├── user.pp         # User/group/directory creation
│       │   ├── config.pp       # Configuration files
│       │   └── service.pp      # Service management
│       ├── templates/
│       │   ├── app.env.epp     # Environment variables
│       │   └── feedback-app.service.epp  # Systemd unit
│       └── README.md
├── data/
│   └── common.yaml             # Hiera data
├── hiera.yaml                  # Hiera configuration
└── README.md
```

## Design Decisions

### 1. Docker-Based Deployment

**Decision**: Deploy application as a Docker container managed by systemd

**Rationale**:
- Consistent with existing CI/CD pipeline (Task 12)
- Isolation and portability
- Easy rollback and version management
- Simpler than managing Tomcat/Java directly

**Alternatives Considered**:
- Direct Tomcat deployment (rejected: more complex, less portable)
- Docker Compose (rejected: overkill for single container)

### 2. Systemd Service Management

**Decision**: Use systemd Type=oneshot service to manage container lifecycle

**Rationale**:
- Native Linux service integration
- Auto-restart on failure
- Service dependency management (requires Docker)
- Easy monitoring with systemctl

**Implementation**:
```
ExecStartPre: Pull latest image
ExecStartPre: Stop old container
ExecStartPre: Remove old container  
ExecStart: Run new container
ExecStop: Stop container
```

### 3. Resource Ordering

**Decision**: Explicit class ordering with contains and chaining arrows

```puppet
Class['feedback_app::install']
-> Class['feedback_app::user']
-> Class['feedback_app::config']
-> Class['feedback_app::service']
```

**Rationale**:
- Ensures packages installed before users created
- Users exist before directories created
- Configuration in place before service started
- Predictable, repeatable execution

### 4. Idempotency Strategy

All resources are naturally idempotent:

| Resource Type | Idempotency Mechanism |
|---------------|----------------------|
| Package | `ensure => installed` (installs only if absent) |
| User/Group | `ensure => present` (creates only if absent) |
| File | Content comparison (updates only if different) |
| Service | State check (starts only if not running) |
| Exec | `unless` parameter (runs only if condition false) |

### 5. Hiera for Configuration

**Decision**: Use Hiera for all configurable parameters

**Rationale**:
- Separation of code and data
- Easy to override per environment
- Single source of truth for configuration values

**Configuration Hierarchy**:
```yaml
hierarchy:
  - name: "Common defaults"
    path: "common.yaml"
```

Extensible to add environment-specific layers:
```yaml
hierarchy:
  - name: "Per-environment"
    path: "environments/%{::environment}.yaml"
  - name: "Common defaults"
    path: "common.yaml"
```

## Class Responsibilities

### feedback_app::install
- Updates apt package cache
- Installs required packages: docker.io, curl, git, ca-certificates
- Ensures Docker service is running
- **Dependencies**: None
- **Required by**: user (needs docker group)

### feedback_app::user
- Creates feedbackapp system user and group
- Adds user to docker group
- Creates directory structure:
  - /opt/course-feedback
  - /opt/course-feedback/config
  - /opt/course-feedback/releases
  - /var/log/course-feedback
- **Dependencies**: install (docker group must exist)
- **Required by**: config (directories must exist)

### feedback_app::config
- Generates app.env from EPP template
- Generates systemd unit file from EPP template
- Creates version tracking file
- Triggers systemd reload when unit file changes
- **Dependencies**: user (directories and ownership)
- **Required by**: service (systemd unit must exist)

### feedback_app::service
- Manages feedback-app systemd service
- Ensures service is running and enabled
- Performs health check after startup
- **Dependencies**: config (systemd unit must exist)

## Templates

### app.env.epp
Environment variables file for documentation and future use.

**Parameters**:
- app_env: Application environment (dev/test/prod)
- java_opts: JVM tuning parameters
- container_port: Host port mapping

### feedback-app.service.epp
Systemd unit file for container lifecycle management.

**Parameters**:
- app_user: Run service as this user
- app_dir: Working directory
- image_name: Docker image repository
- image_tag: Docker image version
- container_name: Container name
- container_port: Host port mapping
- app_env: Application environment
- java_opts: JVM options

**Service Type**: oneshot with RemainAfterExit=yes
- Allows ExecStart to exit after container starts
- Service remains "active" while container runs
- Restart policy triggers on container failure

## Configuration Parameters

| Parameter | Default | Description |
|-----------|---------|-------------|
| image_name | rajjaiswar/course-feedback | Docker image repository |
| image_tag | latest | Docker image tag |
| registry | docker.io | Docker registry URL |
| container_port | 8083 | Host port mapping |
| app_env | dev | Application environment |
| app_user | feedbackapp | System user |
| app_group | feedbackapp | System group |
| app_dir | /opt/course-feedback | Application root directory |
| log_dir | /var/log/course-feedback | Log directory |
| container_name | feedback-app | Docker container name |
| java_opts | -Xms256m -Xmx512m | JVM memory settings |

## Security Considerations

1. **System User**: feedbackapp runs as non-root system user
2. **Docker Group**: User added to docker group for container management
3. **File Permissions**:
   - Config files: 0640 (owner read/write, group read)
   - Directories: 0755 (standard directory permissions)
   - Systemd unit: 0644 (root-owned, world-readable)
4. **Container Restart Policy**: unless-stopped (prevents runaway restarts)

## Port Configuration

**Original Plan**: Port 8082
**Actual Implementation**: Port 8083

**Reason for Change**: Port 8082 had a persistent conflict in the WSL2 environment. Changed to 8083 as a workaround for Task 13 completion. This demonstrates Puppet's flexibility - changing a single value in Hiera updated the entire configuration.

## Validation Strategy

1. **Syntax Validation**: `puppet parser validate`
2. **Template Validation**: `puppet epp validate`
3. **Linting**: `puppet-lint` for style compliance
4. **Dry Run**: `puppet apply --noop` before actual execution
5. **Verification**: Automated checks after execution

## Rollback Strategy (Task 14)

Future rollback implementation will:
1. Update image_tag in Hiera to previous version
2. Run puppet apply
3. Systemd service will:
   - Pull old image
   - Stop current container
   - Start container with old image

## Testing Approach

### Unit Tests (Future Enhancement)
- rspec-puppet for manifest testing
- Test parameter validation
- Test resource relationships

### Integration Tests (Task 14)
- Idempotency: Run twice, second run should show exit code 0
- Configuration changes: Modify Hiera, verify changes applied
- Service restart: Stop service, verify it restarts
- Container failure: Kill container, verify service recreates it

## Lessons Learned

1. **Module Dependencies**: Removing puppetlabs-stdlib simplified deployment but required manual path validation
2. **WSL2 Considerations**: Port conflicts common in WSL2, need flexible configuration
3. **Docker Permissions**: User must be in docker group before service starts
4. **Systemd Type**: oneshot type appropriate for containerized applications
5. **Template Simplicity**: Avoided complex template logic (DateTime, etc.) for better portability

## Future Enhancements

1. **Multi-Environment Support**: Add dev/test/prod Hiera layers
2. **Monitoring Integration**: Add Prometheus metrics endpoint
3. **Log Aggregation**: Configure container logging to host filesystem
4. **Health Checks**: Enhanced application health verification
5. **Secrets Management**: Integrate with Vault or similar for sensitive data
6. **Master/Agent Mode**: If scaling to multiple nodes
7. **Testing Framework**: rspec-puppet and serverspec integration

## References

- Puppet Documentation: https://www.puppet.com/docs/puppet/8/puppet_index.html
- Module Best Practices: https://www.puppet.com/docs/puppet/8/modules_fundamentals.html
- Systemd Integration: https://www.freedesktop.org/software/systemd/man/systemd.service.html
- Docker Documentation: https://docs.docker.com/
