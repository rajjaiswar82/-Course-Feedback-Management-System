# Puppet Configuration Management - Task 13

This directory contains Puppet manifests for automated configuration of the Course Feedback application deployment infrastructure.

## Quick Start

Run the validation and execution script in **WSL2 Ubuntu**:

```bash
cd /mnt/c/Users/RAJ\ JAISWAR/OneDrive/Documents/Devops/course-feedback-git
chmod +x puppet/run-puppet-validation.sh
./puppet/run-puppet-validation.sh
```

The script will:
1. ✓ Check Puppet installation
2. ✓ Validate syntax
3. ✓ Validate EPP templates
4. ✓ Run puppet-lint
5. ✓ Perform dry run (--noop)
6. ⏸ Prompt for confirmation
7. ▶ Execute actual provisioning
8. ✓ Verify deployment

## Manual Execution

If you prefer to run commands manually:

```bash
# Find puppet path
which puppet

# Syntax validation
puppet parser validate puppet/manifests/site.pp puppet/modules/feedback_app/manifests/*.pp

# Template validation
puppet epp validate puppet/modules/feedback_app/templates/*.epp

# Lint check
puppet-lint puppet/modules/feedback_app

# Dry run
sudo /opt/puppetlabs/bin/puppet apply --noop \
  --modulepath=puppet/modules \
  --hiera_config=puppet/hiera.yaml \
  puppet/manifests/site.pp

# Actual execution
sudo /opt/puppetlabs/bin/puppet apply \
  --modulepath=puppet/modules \
  --hiera_config=puppet/hiera.yaml \
  puppet/manifests/site.pp \
  --verbose --detailed-exitcodes | tee puppet-first-run.log
```

## Verification Commands

After execution, verify the deployment:

```bash
# Check user
id feedbackapp

# Check directories
ls -la /opt/course-feedback
ls -la /var/log/course-feedback

# Check services
systemctl status docker
systemctl status feedback-app

# Check container
docker ps --filter name=feedback-app

# Test application
curl -I http://localhost:8082/
```

## Directory Structure

```
puppet/
├── manifests/
│   └── site.pp                 # Main entry point
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
│       └── README.md           # Module documentation
├── data/
│   └── common.yaml             # Hiera configuration values
├── hiera.yaml                  # Hiera configuration
├── run-puppet-validation.sh    # Automated validation & execution
└── README.md                   # This file
```

## Configuration

Default values are set in `puppet/data/common.yaml`:

```yaml
feedback_app::image_name: 'rajjaiswar/course-feedback'
feedback_app::image_tag: 'latest'
feedback_app::container_port: 8082
feedback_app::app_env: 'dev'
```

To change values, edit `puppet/data/common.yaml` or override in `site.pp`.

## What Gets Configured

### Packages Installed
- docker.io
- curl
- git
- ca-certificates

### User & Group Created
- User: `feedbackapp` (system user, member of docker group)
- Group: `feedbackapp`

### Directories Created
- `/opt/course-feedback` - Application root
- `/opt/course-feedback/config` - Configuration files
- `/opt/course-feedback/releases` - Release tracking
- `/var/log/course-feedback` - Application logs

### Services Configured
- Docker service (enabled & running)
- feedback-app systemd service (manages container lifecycle)

### Container Deployed
- Image: `rajjaiswar/course-feedback:latest`
- Container name: `feedback-app`
- Port mapping: 8082:8080
- Auto-restart on failure

## Troubleshooting

### Puppet command not found with sudo
```bash
# Use full path
sudo /opt/puppetlabs/bin/puppet apply ...
```

### puppet-lint not installed
```bash
sudo /opt/puppetlabs/puppet/bin/gem install puppet-lint
```

### Docker permission denied
```bash
# User needs to be in docker group (Puppet handles this)
# If running manually, logout and login again after group change
sudo usermod -aG docker $USER
```

### Container not starting
```bash
# Check service logs
systemctl status feedback-app
journalctl -u feedback-app -f

# Check container logs
docker logs feedback-app
```

### Application not responding
```bash
# Give it time to start (Java apps take ~30 seconds)
# Check if container is running
docker ps -a

# Check application logs
docker logs feedback-app
```

## Exit Codes

Puppet uses specific exit codes:
- `0` - Success, no changes needed
- `2` - Success, changes were applied
- `4` - Failures occurred during execution
- `6` - Changes applied AND failures occurred

## Next Steps (Task 14)

After successful Task 13 execution:
1. Test idempotency (run Puppet again, should show exit code 0)
2. Test configuration changes
3. Test rollback procedures
4. Document all findings

## Documentation

- Configuration Spec: `docs/task-13-puppet/configuration-spec.md`
- Module README: `puppet/modules/feedback_app/README.md`
- Execution Log: `puppet-first-run.log` (after running)
