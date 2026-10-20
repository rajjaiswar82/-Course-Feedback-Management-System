#!/bin/bash
# Puppet Validation and Execution Script for Task 13
# Run this in WSL2 Ubuntu

set -e  # Exit on error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}Puppet Validation and Execution - Task 13${NC}"
echo -e "${BLUE}========================================${NC}\n"

# Navigate to project directory
PROJECT_DIR="/mnt/c/Users/RAJ JAISWAR/OneDrive/Documents/Devops/course-feedback-git"
cd "$PROJECT_DIR"

echo -e "${YELLOW}Current directory: $(pwd)${NC}\n"

# Step 1: Check Puppet installation
echo -e "${BLUE}[STEP 1] Checking Puppet installation...${NC}"
if command -v puppet &> /dev/null; then
    PUPPET_CMD=$(which puppet)
    echo -e "${GREEN}✓ Puppet found at: $PUPPET_CMD${NC}"
    puppet --version
else
    echo -e "${RED}✗ Puppet not found in PATH${NC}"
    echo -e "${YELLOW}Trying /opt/puppetlabs/bin/puppet...${NC}"
    if [ -f /opt/puppetlabs/bin/puppet ]; then
        PUPPET_CMD="/opt/puppetlabs/bin/puppet"
        echo -e "${GREEN}✓ Puppet found at: $PUPPET_CMD${NC}"
        $PUPPET_CMD --version
    else
        echo -e "${RED}✗ Puppet not installed. Please install Puppet 8.x first.${NC}"
        exit 1
    fi
fi
echo ""

# Step 2: Syntax validation
echo -e "${BLUE}[STEP 2] Validating Puppet syntax...${NC}"
$PUPPET_CMD parser validate puppet/manifests/site.pp puppet/modules/feedback_app/manifests/*.pp
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Syntax validation passed${NC}\n"
else
    echo -e "${RED}✗ Syntax validation failed${NC}\n"
    exit 1
fi

# Step 3: EPP template validation
echo -e "${BLUE}[STEP 3] Validating EPP templates...${NC}"
for template in puppet/modules/feedback_app/templates/*.epp; do
    echo -e "  Validating: $template"
    $PUPPET_CMD epp validate "$template"
done
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Template validation passed${NC}\n"
else
    echo -e "${RED}✗ Template validation failed${NC}\n"
    exit 1
fi

# Step 4: Puppet-lint check
echo -e "${BLUE}[STEP 4] Running puppet-lint...${NC}"
if command -v puppet-lint &> /dev/null; then
    puppet-lint puppet/modules/feedback_app
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Puppet-lint checks passed${NC}\n"
    else
        echo -e "${YELLOW}⚠ Puppet-lint found issues (warnings only)${NC}\n"
    fi
else
    echo -e "${YELLOW}⚠ puppet-lint not installed, skipping${NC}"
    echo -e "${YELLOW}  Install with: sudo /opt/puppetlabs/puppet/bin/gem install puppet-lint${NC}\n"
fi

# Step 5: Dry run (noop)
echo -e "${BLUE}[STEP 5] Running Puppet dry run (--noop)...${NC}"
echo -e "${YELLOW}This will show what changes would be made without applying them${NC}\n"
sudo $PUPPET_CMD apply --noop --modulepath=puppet/modules --hiera_config=puppet/hiera.yaml puppet/manifests/site.pp
NOOP_EXIT=$?
echo ""

if [ $NOOP_EXIT -eq 0 ] || [ $NOOP_EXIT -eq 2 ]; then
    echo -e "${GREEN}✓ Dry run completed successfully${NC}\n"
else
    echo -e "${RED}✗ Dry run failed with exit code: $NOOP_EXIT${NC}\n"
    exit 1
fi

# Step 6: Prompt for actual execution
echo -e "${YELLOW}========================================${NC}"
echo -e "${YELLOW}Validation Complete!${NC}"
echo -e "${YELLOW}========================================${NC}\n"
echo -e "${BLUE}Ready to run actual Puppet provisioning.${NC}"
echo -e "${YELLOW}This will make real changes to your system:${NC}"
echo "  - Install packages (docker.io, curl, git, ca-certificates)"
echo "  - Create user 'feedbackapp' and group"
echo "  - Create directories in /opt and /var/log"
echo "  - Deploy systemd service for feedback-app"
echo "  - Start the feedback-app container"
echo ""
read -p "$(echo -e ${GREEN}Do you want to proceed with actual execution? [y/N]: ${NC})" -n 1 -r
echo ""

if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo -e "${YELLOW}Execution cancelled by user${NC}"
    exit 0
fi

# Step 7: Actual execution
echo -e "\n${BLUE}[STEP 6] Running Puppet apply (actual execution)...${NC}\n"
sudo $PUPPET_CMD apply --modulepath=puppet/modules --hiera_config=puppet/hiera.yaml puppet/manifests/site.pp --verbose --detailed-exitcodes | tee puppet-first-run.log
APPLY_EXIT=$?

echo ""
echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}Execution Complete${NC}"
echo -e "${BLUE}========================================${NC}\n"

# Interpret exit codes
# 0 = no changes, 2 = changes applied, 4 or 6 = failures
if [ $APPLY_EXIT -eq 0 ]; then
    echo -e "${GREEN}✓ Puppet run completed: No changes needed (already in desired state)${NC}"
elif [ $APPLY_EXIT -eq 2 ]; then
    echo -e "${GREEN}✓ Puppet run completed: Changes were successfully applied${NC}"
elif [ $APPLY_EXIT -eq 4 ]; then
    echo -e "${RED}✗ Puppet run failed: Errors occurred${NC}"
    exit 1
elif [ $APPLY_EXIT -eq 6 ]; then
    echo -e "${YELLOW}⚠ Puppet run completed with changes AND errors${NC}"
    exit 1
else
    echo -e "${YELLOW}⚠ Puppet run completed with unexpected exit code: $APPLY_EXIT${NC}"
fi

# Step 8: Verification
echo -e "\n${BLUE}[STEP 7] Verifying deployment...${NC}\n"

echo "1. Checking user 'feedbackapp':"
id feedbackapp 2>/dev/null && echo -e "${GREEN}✓ User exists${NC}" || echo -e "${RED}✗ User not found${NC}"
echo ""

echo "2. Checking directories:"
ls -ld /opt/course-feedback 2>/dev/null && echo -e "${GREEN}✓ /opt/course-feedback exists${NC}" || echo -e "${RED}✗ Directory not found${NC}"
ls -ld /var/log/course-feedback 2>/dev/null && echo -e "${GREEN}✓ /var/log/course-feedback exists${NC}" || echo -e "${RED}✗ Directory not found${NC}"
echo ""

echo "3. Checking Docker service:"
systemctl is-active --quiet docker && echo -e "${GREEN}✓ Docker is running${NC}" || echo -e "${RED}✗ Docker not running${NC}"
echo ""

echo "4. Checking feedback-app service:"
systemctl is-active --quiet feedback-app && echo -e "${GREEN}✓ feedback-app service is running${NC}" || echo -e "${RED}✗ Service not running${NC}"
systemctl is-enabled --quiet feedback-app && echo -e "${GREEN}✓ feedback-app service is enabled${NC}" || echo -e "${RED}✗ Service not enabled${NC}"
echo ""

echo "5. Checking Docker container:"
docker ps --filter name=feedback-app --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
CONTAINER_RUNNING=$(docker ps --filter name=feedback-app --format "{{.Names}}" | grep -c feedback-app || echo "0")
if [ "$CONTAINER_RUNNING" -gt 0 ]; then
    echo -e "${GREEN}✓ Container is running${NC}"
else
    echo -e "${RED}✗ Container not running${NC}"
fi
echo ""

echo "6. Testing application endpoint:"
sleep 5  # Give app time to start
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8082/ || echo "000")
if [ "$HTTP_CODE" = "200" ] || [ "$HTTP_CODE" = "302" ]; then
    echo -e "${GREEN}✓ Application is responding (HTTP $HTTP_CODE)${NC}"
    echo -e "${GREEN}✓ Access the app at: http://localhost:8082/${NC}"
else
    echo -e "${YELLOW}⚠ Application not responding yet (HTTP $HTTP_CODE)${NC}"
    echo -e "${YELLOW}  Check logs: docker logs feedback-app${NC}"
    echo -e "${YELLOW}  Check service: systemctl status feedback-app${NC}"
fi
echo ""

echo -e "${BLUE}========================================${NC}"
echo -e "${GREEN}Task 13 Puppet Execution Complete!${NC}"
echo -e "${BLUE}========================================${NC}\n"
echo "Log file saved to: puppet-first-run.log"
echo ""
echo "Next steps:"
echo "  1. Review the log file: puppet-first-run.log"
echo "  2. Test the application: http://localhost:8082/"
echo "  3. Check service status: systemctl status feedback-app"
echo "  4. View container logs: docker logs feedback-app"
echo ""
