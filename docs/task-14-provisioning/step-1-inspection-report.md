# Task 14 - STEP 1: Current State Inspection Report

## Target Node Information

### Node Type
**WSL2 Ubuntu 22.04** (Windows Subsystem for Linux 2)

**Rationale**: 
- Existing node used for Task 13
- Quick provisioning capability
- Native Linux environment for Puppet
- No additional VM overhead

### Puppet Version
```bash
$ puppet --version
8.10.0
```

**Installation Location**: `/opt/puppetlabs/bin/puppet`

## Puppet Module Review

### Module Structure
```
puppet/modules/feedback_app/
├── manifests/
│   ├── init.pp      # Main class with parameters
│   ├── install.pp   # Package installation (docker.io, curl, git, ca-certificates)
│   ├── user.pp      # User/group/directory creation
│   ├── config.pp    # Configuration files (app.env, systemd unit)
│   └── service.pp   # Service management (docker, feedback-app)
└── templates/
    ├── app.env.epp              # Environment variables
    └── feedback-app.service.epp # Systemd unit file
```

### Key Parameters (from init.pp)
- `image_name`: Docker image repository (default: 'rajjaiswar/course-feedback')
- `image_tag`: Docker image version (default: 'latest')
- `container_port`: Host port mapping (default: 8082, currently 8083 in Hiera)
- `app_env`: Application environment (default: 'dev')
- `app_user`: System user (default: 'feedbackapp')
- `app_dir`: Application directory (default: '/opt/course-feedback')
- `log_dir`: Log directory (default: '/var/log/course-feedback')
- `container_name`: Docker container name (default: 'feedback-app')
- `java_opts`: JVM options (default: '-Xms256m -Xmx512m')

### Current Hiera Configuration
**File**: `puppet/data/common.yaml`

```yaml
feedback_app::image_name: 'rajjaiswar/course-feedback'
feedback_app::image_tag: 'latest'
feedback_app::registry: 'docker.io'
feedback_app::container_port: 8083
feedback_app::app_env: 'dev'
```

**Note**: Port 8083 is currently set due to Task 13 port conflict resolution.

## Docker Image Versions

### Registry Information
- **Registry**: Docker Hub (docker.io)
- **Repository**: rajjaiswar/course-feedback
- **Public Access**: Yes

### Available Image Tags

To verify available tags, run:
```bash
# Method 1: Using Docker Hub API
curl -s "https://hub.docker.com/v2/repositories/rajjaiswar/course-feedback/tags/" | jq '.results[].name'

# Method 2: Using skopeo (if installed)
skopeo list-tags docker://rajjaiswar/course-feedback

# Method 3: Check Jenkins build history for pushed tags
```

### Expected Tags (Based on Jenkins Pipeline)
From the Jenkinsfile, images are tagged with:
- `latest` - Most recent build
- `1.0.<build_number>` - Semantic version based on Jenkins BUILD_NUMBER
- Potentially: `dev`, `test`, `prod` environment tags

### Current Stable Release
**To be determined**: Need to query Docker Hub or check Jenkins build history.

**Placeholder values for Task 14**:
- **Version 1 (Stable)**: `1.0.30` (or last known good build)
- **Version 2 (New)**: `1.0.31` (or next build)

## Dockerfile Review

### Multi-Stage Build
- **Stage 1 (BUILD)**: Maven 3.9 + Eclipse Temurin JDK 21
- **Stage 2 (RUNTIME)**: Eclipse Temurin JRE 21 Alpine

### Key Features
- **Layer Caching**: Separate `pom.xml` copy for dependency caching
- **Security**: Non-root user (`appuser:appgroup`)
- **Health Check**: Built-in Docker healthcheck on port 8080
- **Configurable**: Environment variables for APP_ENV, JAVA_OPTS, SERVER_PORT
- **Port**: Exposes 8080 (internal container port)

### Health Check Configuration
```dockerfile
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/ || exit 1
```

## Jenkinsfile Review

### Pipeline Stages
1. **Checkout** - Code retrieval
2. **Build** - Maven build (`mvn clean verify`)
3. **Test** - Unit tests included in verify
4. **Selenium UI Tests** - Optional (RUN_UI_TESTS parameter)
5. **Build Docker Image** - Creates tagged image
6. **Push to Registry** - Pushes to Docker Hub
7. **Deploy Container** - Deploys to specified environment

### Relevant Parameters for Task 14
- `DEPLOY_ENV`: Target environment (dev/test/prod)
- `CONTAINER_PORT`: Host port (default: 8082)
- `REGISTRY`: Docker registry (docker.io or localhost:5000)
- `DEPLOY_CONTAINER`: Boolean to enable deployment

### Image Tagging Strategy
```groovy
DOCKER_TAG = "${env.IMAGE_NAME}:${env.IMAGE_VERSION}"
// IMAGE_VERSION is typically: 1.0.${BUILD_NUMBER}
```

## Task 13 Execution Summary

### What Was Deployed
- **Execution Date**: October 20-21, 2026
- **Image Used**: `rajjaiswar/course-feedback:latest`
- **Port**: 8083 (changed from 8082 due to conflict)
- **Application Status**: Running and healthy
- **Service Status**: Active and enabled

### Resources Configured
- ✅ 4 packages installed
- ✅ 1 system user created (feedbackapp)
- ✅ 1 system group created
- ✅ 4 directories created
- ✅ 2 configuration files
- ✅ 2 services managed
- ✅ 1 Docker container deployed

### Known Issues from Task 13
1. **Port Conflict**: Port 8082 was in use, changed to 8083
2. **Module Dependencies**: Removed puppetlabs-stdlib for simplicity
3. **WSL2 Specific**: Some Docker networking quirks

## Action Items for Task 14

### 1. Determine Image Versions
```bash
# Check what's currently deployed
docker images | grep rajjaiswar/course-feedback

# Check Docker Hub for available tags
curl -s "https://hub.docker.com/v2/repositories/rajjaiswar/course-feedback/tags/?page_size=100" | jq -r '.results[] | "\(.name) \(.last_updated)"'

# Or check Jenkins build history
```

### 2. Create Second Image Version (if needed)
If fewer than 2 versions exist:
1. Make a trivial change (e.g., update version in pom.xml)
2. Commit: `git commit -m "chore: bump version for rollback testing"`
3. Trigger Jenkins build
4. Verify new tag appears in Docker Hub

### 3. Provision Clean Node
Options:
- **Option A**: Fresh WSL2 distro (`wsl --import Ubuntu-Task14 ...`)
- **Option B**: Clean existing WSL2 node (remove all Task 13 artifacts)
- **Option C**: Create Vagrant/VirtualBox VM (more isolated but slower)

### 4. Update Hiera for Proper Versioning
Change from `latest` to specific version:
```yaml
feedback_app::image_tag: '1.0.30'  # Stable version
feedback_app::container_port: 8082  # Reset to standard port
```

## Recommendations

1. **Use Specific Tags**: Change from `latest` to semantic versions (1.0.x)
2. **Port Standardization**: Reset to 8082 if possible (investigate port conflict)
3. **Clean Node**: Prefer fresh WSL2 instance for true clean provisioning
4. **Version Tracking**: Implement version marker in /opt/course-feedback/releases/
5. **Health Check Script**: Add reusable health-check.sh to Puppet module

## Next Steps

1. Query Docker Hub API for available tags
2. If < 2 versions, trigger Jenkins build to create another
3. Decide on clean node provisioning method
4. Create provisioning documentation
5. Begin STEP 2 with clean environment

---

**Report Date**: October 21, 2026  
**Status**: Inspection complete, ready to proceed with STEP 2
