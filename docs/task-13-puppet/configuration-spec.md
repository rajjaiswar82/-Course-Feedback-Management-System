# Task 13: Server Configuration Specification

**Target Node**: Ubuntu 22.04 (WSL2)  
**Puppet Version**: 8.10.0  
**Docker Version**: 29.1.3  
**Deployment Method**: Docker container from Docker Hub registry

---

## 📦 1. Packages

| Package | Version | Purpose | Puppet Resource |
|---------|---------|---------|-----------------|
| `docker.io` | Latest (29.x) | Container runtime for running the application | `package { 'docker.io': }` |
| `curl` | Latest | HTTP client for health checks and downloads | `package { 'curl': }` |
| `git` | Latest | Version control (optional, for git-based deployments) | `package { 'git': }` |
| `ca-certificates` | Latest | SSL/TLS certificate validation | `package { 'ca-certificates': }` |

**Note**: No host Java installation needed - Java 21 is bundled inside the Docker image.

---

## 👤 2. Users and Groups

| User/Group | Type | UID/GID | Purpose | Puppet Resource |
|------------|------|---------|---------|-----------------|
| `feedbackapp` | System User | Auto | Dedicated non-privileged user for app management | `user { 'feedbackapp': system => true }` |
| `feedbackapp` | Group | Auto | Primary group for feedbackapp user | `group { 'feedbackapp': system => true }` |
| Docker group membership | Secondary | - | Allow feedbackapp user to manage Docker containers | `User['feedbackapp'] { groups +> 'docker' }` |

**Security**: The `feedbackapp` user will NOT run the application directly (container runs as `appuser` internally), but will own config files and manage the container lifecycle.

---

## 📁 3. Directories

| Path | Owner | Group | Mode | Purpose | Puppet Resource |
|------|-------|-------|------|---------|-----------------|
| `/opt/course-feedback` | `feedbackapp` | `feedbackapp` | `0755` | Application root directory | `file { '/opt/course-feedback': }` |
| `/opt/course-feedback/config` | `feedbackapp` | `feedbackapp` | `0750` | Configuration files (env vars, secrets) | `file { '/opt/course-feedback/config': }` |
| `/opt/course-feedback/releases` | `feedbackapp` | `feedbackapp` | `0755` | Version tracking for rollback (Task 14) | `file { '/opt/course-feedback/releases': }` |
| `/var/log/course-feedback` | `feedbackapp` | `feedbackapp` | `0755` | Application logs (if needed) | `file { '/var/log/course-feedback': }` |

---

## 📄 4. Files

| Path | Source/Template | Owner | Group | Mode | Purpose | Puppet Resource |
|------|----------------|-------|-------|------|---------|-----------------|
| `/opt/course-feedback/config/app.env` | `templates/app.env.epp` | `feedbackapp` | `feedbackapp` | `0640` | Environment variables for container | `file { '...': content => epp('...') }` |
| `/etc/systemd/system/feedback-app.service` | `templates/feedback-app.service.epp` | `root` | `root` | `0644` | Systemd unit for container management | `file { '...': content => epp('...') }` |
| `/opt/course-feedback/releases/current-version.txt` | Content string | `feedbackapp` | `feedbackapp` | `0644` | Current deployed version marker | `file { '...': content => "${image_tag}" }` |

---

## 🔌 5. Ports

| Port | Protocol | Service | Firewall Rule | Puppet Resource |
|------|----------|---------|---------------|-----------------|
| `8082` | TCP | Application HTTP | Allow incoming (ufw) | `exec { 'ufw-allow-8082': }` |
| `22` | TCP | SSH (optional) | Allow incoming | `exec { 'ufw-allow-ssh': }` |

**Firewall Configuration**: Use UFW (Uncomplicated Firewall) if enabled. Check with `ufw status`.

---

## 🔧 6. Services

| Service | State | Enabled | Purpose | Puppet Resource |
|---------|-------|---------|---------|-----------------|
| `docker` | `running` | `true` | Docker daemon must be active | `service { 'docker': ensure => running, enable => true }` |
| `feedback-app` | `running` | `true` | Application systemd unit | `service { 'feedback-app': ensure => running, enable => true }` |

**Service Management**: The `feedback-app` service will:
- Pull the Docker image from Docker Hub
- Stop and remove old container (if exists)
- Start new container with correct configuration
- Manage container lifecycle through systemd

---

## 🌍 7. Environment Settings

| Variable | Value | Source | Purpose |
|----------|-------|--------|---------|
| `APP_ENV` | `dev` (default, parameterized) | Hiera/class parameter | Application environment (dev/test/prod) |
| `IMAGE_NAME` | `rajjaiswar/course-feedback` | Hiera/class parameter | Docker image repository |
| `IMAGE_TAG` | `latest` (default, parameterized) | Hiera/class parameter | Docker image version tag |
| `REGISTRY` | `docker.io` | Hiera/class parameter | Docker registry URL |
| `CONTAINER_PORT` | `8082` | Hiera/class parameter | Host port for the application |
| `CONTAINER_NAME` | `feedback-app` | Fixed | Docker container name |
| `JAVA_OPTS` | `-Xms256m -Xmx512m` | app.env | JVM memory settings (inside container) |
| `SERVER_PORT` | `8080` | Container default | Application port inside container |

---

## 🔄 8. Resource Dependencies & Ordering

**Correct execution order** (Puppet will enforce):

```
1. Packages (docker.io, curl, git, ca-certificates)
   ↓
2. Groups (feedbackapp)
   ↓
3. Users (feedbackapp, added to docker group)
   ↓
4. Directories (/opt/course-feedback/*, /var/log/course-feedback)
   ↓
5. Docker Service (ensure running)
   ↓
6. Configuration Files (app.env, systemd unit)
   ↓
7. Systemd Reload (daemon-reload after unit file creation)
   ↓
8. Application Service (feedback-app.service)
   ↓
9. Health Check (verify container is running)
```

**Puppet Implementation**:
- Use `require` to declare dependencies
- Use `->` (chaining arrow) for explicit ordering
- Use `notify` to trigger service restarts when config changes
- Use `unless`/`onlyif`/`creates` on `exec` resources for idempotency

---

## 🔐 9. Secrets Management

**Docker Hub Credentials** (if needed for private registry):
- **NOT stored in manifests or Hiera**
- **NOT committed to Git**
- **Option 1**: Pre-configure `docker login` on the node
- **Option 2**: Use Docker credential helpers
- **Option 3**: Pass via environment variable to Puppet

**Documented Approach**:
```bash
# Manual pre-setup (run once on target node)
docker login -u rajjaiswar
# Enter Docker Hub token when prompted

# Or use environment variable
export DOCKER_TOKEN="your-token-here"
```

**For public images** (`rajjaiswar/course-feedback` is public): No credentials needed.

---

## ✅ 10. Idempotency Guarantees

Every resource is designed to be idempotent (safe to run multiple times):

| Resource Type | Idempotency Mechanism |
|---------------|----------------------|
| `package` | Only installs if not present; upgrade if specified |
| `user` | Only creates if missing; modifies attributes if changed |
| `group` | Only creates if missing |
| `file` | Only writes if content/mode/owner differs |
| `service` | Only starts if stopped; restarts only if notified |
| `exec` | Uses `unless`, `onlyif`, or `creates` to prevent re-execution |

**Testing**: First run will make changes (exit code 2), second run will report no changes (exit code 0).

---

## 📊 11. Puppet Manifest Structure

```
puppet/
├── manifests/
│   └── site.pp                          # Node definition, includes feedback_app
├── modules/
│   └── feedback_app/
│       ├── manifests/
│       │   ├── init.pp                  # Main class with parameters
│       │   ├── install.pp               # Packages
│       │   ├── user.pp                  # User, group, directories
│       │   ├── config.pp                # Config files from templates
│       │   └── service.pp               # Docker & app services
│       ├── templates/
│       │   ├── app.env.epp              # Environment variables template
│       │   └── feedback-app.service.epp # Systemd unit template
│       ├── files/
│       └── README.md
├── hiera.yaml                           # Hiera configuration
└── data/
    └── common.yaml                      # Default parameter values
```

---

## 🎯 12. Parameterization for Task 14 (Rollback Support)

**Key Parameters** (can be changed without editing code):
- `$image_tag` - Version to deploy (e.g., `1.0.30`, `1.0.29` for rollback)
- `$app_env` - Environment (dev/test/prod)
- `$container_port` - Host port
- `$image_name` - Image repository

**Rollback Workflow** (Task 14):
1. Read previous version from `/opt/course-feedback/releases/current-version.txt`
2. Update Hiera: `image_tag: '1.0.29'` (previous version)
3. Re-run: `puppet apply ...`
4. Puppet will pull old image and recreate container

---

## 📋 13. Verification Commands

After Puppet run, verify with:

```bash
# User and groups
id feedbackapp
getent group feedbackapp

# Directories
ls -la /opt/course-feedback
ls -la /opt/course-feedback/config
ls -la /var/log/course-feedback

# Files
cat /opt/course-feedback/config/app.env
cat /opt/course-feedback/releases/current-version.txt
systemctl cat feedback-app.service

# Services
systemctl status docker --no-pager
systemctl status feedback-app --no-pager

# Container
docker ps --filter name=feedback-app
docker inspect feedback-app | grep -E "Image|Status"

# Network
ss -tulpn | grep 8082

# Health check
curl -I http://localhost:8082/
```

---

## 🚀 14. Execution Commands

### Validation (before applying):
```bash
# Syntax check
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

### First Application:
```bash
# Apply configuration
sudo puppet apply --modulepath=puppet/modules \
  --hiera_config=puppet/hiera.yaml puppet/manifests/site.pp \
  --verbose --detailed-exitcodes | tee puppet-first-run.log

# Exit codes:
# 0 = No changes (already configured)
# 2 = Changes applied successfully
# 4 or 6 = Errors occurred
```

---

**Specification Complete** ✅  
**Next**: Create Puppet module structure (STEP 3)
