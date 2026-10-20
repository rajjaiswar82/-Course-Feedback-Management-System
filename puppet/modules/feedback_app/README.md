# feedback_app Puppet Module

## Description

This Puppet module manages the deployment and configuration of the Course Feedback Management System as a Docker container.

## Requirements

- **Puppet**: 8.x or later
- **OS**: Ubuntu 22.04 (tested), Debian-based systems
- **Docker**: Will be installed by the module
- **Internet access**: Required to pull Docker images from Docker Hub

## Usage

### Basic Usage (Masterless)

```bash
sudo puppet apply --modulepath=puppet/modules \
  --hiera_config=puppet/hiera.yaml \
  puppet/manifests/site.pp
```

### With Custom Parameters

Edit `puppet/data/common.yaml`:

```yaml
feedback_app::image_tag: '1.0.30'
feedback_app::app_env: 'prod'
feedback_app::container_port: 9000
```

Then apply:

```bash
sudo puppet apply --modulepath=puppet/modules \
  --hiera_config=puppet/hiera.yaml \
  puppet/manifests/site.pp
```

## Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `image_name` | String | `rajjaiswar/course-feedback` | Docker image repository |
| `image_tag` | String | `latest` | Docker image version tag |
| `registry` | String | `docker.io` | Docker registry URL |
| `container_port` | Integer | `8082` | Host port for the application |
| `app_env` | Enum[dev, test, prod] | `dev` | Application environment |
| `app_user` | String | `feedbackapp` | System user for app management |
| `app_group` | String | `feedbackapp` | System group |
| `app_dir` | Stdlib::Absolutepath | `/opt/course-feedback` | Application root directory |
| `log_dir` | Stdlib::Absolutepath | `/var/log/course-feedback` | Log directory |
| `container_name` | String | `feedback-app` | Docker container name |
| `java_opts` | String | `-Xms256m -Xmx512m` | JVM options |

## Module Structure

```
feedback_app/
├── manifests/
│   ├── init.pp       # Main class with parameters
│   ├── install.pp    # Package installation
│   ├── user.pp       # User, group, directories
│   ├── config.pp     # Configuration files
│   └── service.pp    # Docker and app services
├── templates/
│   ├── app.env.epp              # Environment variables
│   └── feedback-app.service.epp # Systemd unit
└── README.md
```

## What This Module Does

1. **Installs packages**: docker.io, curl, git, ca-certificates
2. **Creates user**: `feedbackapp` system user with docker group membership
3. **Creates directories**: 
   - `/opt/course-feedback/`
   - `/opt/course-feedback/config/`
   - `/opt/course-feedback/releases/`
   - `/var/log/course-feedback/`
4. **Configures files**:
   - `/opt/course-feedback/config/app.env`
   - `/etc/systemd/system/feedback-app.service`
5. **Manages services**:
   - Docker daemon (enabled, running)
   - feedback-app systemd unit (enabled, running)
6. **Deploys container**:
   - Pulls image from Docker Hub
   - Stops old container (if exists)
   - Starts new container with proper configuration

## Idempotency

This module is fully idempotent:
- First run: Makes changes (exit code 2)
- Subsequent runs: No changes if configuration matches (exit code 0)
- Config changes: Only modified resources updated

## Rollback Support (Task 14)

To rollback to a previous version:

1. Update `puppet/data/common.yaml`:
   ```yaml
   feedback_app::image_tag: '1.0.29'  # Previous version
   ```

2. Re-apply:
   ```bash
   sudo puppet apply --modulepath=puppet/modules \
     --hiera_config=puppet/hiera.yaml \
     puppet/manifests/site.pp
   ```

The module will automatically pull the old image and recreate the container.

## Validation

```bash
# Syntax validation
puppet parser validate puppet/manifests/site.pp
puppet parser validate puppet/modules/feedback_app/manifests/*.pp

# Lint check
puppet-lint puppet/modules/feedback_app

# Template validation
puppet epp validate puppet/modules/feedback_app/templates/*.epp

# Dry-run (noop)
sudo puppet apply --noop --modulepath=puppet/modules \
  --hiera_config=puppet/hiera.yaml puppet/manifests/site.pp
```

## Verification

After applying, verify the installation:

```bash
# Check user
id feedbackapp

# Check directories
ls -la /opt/course-feedback

# Check services
systemctl status docker
systemctl status feedback-app

# Check container
docker ps --filter name=feedback-app

# Test application
curl -I http://localhost:8082/
```

## Troubleshooting

### Container fails to start

Check logs:
```bash
journalctl -u feedback-app -n 50
docker logs feedback-app
```

### Docker pull fails

Ensure internet connectivity:
```bash
docker pull rajjaiswar/course-feedback:latest
```

### Port already in use

Check what's using the port:
```bash
sudo ss -tulpn | grep 8082
```

## Author

DevOps Course Project - Task 13

## License

Educational use only
