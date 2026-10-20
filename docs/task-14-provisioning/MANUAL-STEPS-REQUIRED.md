# Task 14: Manual Steps Required

## CRITICAL: Commands You Must Run

I cannot execute commands in your WSL2 Ubuntu environment directly. Please run these commands and share the output so I can proceed with Task 14.

---

## STEP 1: Check Available Docker Image Versions

Run these commands in **WSL2 Ubuntu**:

```bash
cd /mnt/c/Users/RAJ\ JAISWAR/OneDrive/Documents/Devops/course-feedback-git

# Make script executable
chmod +x scripts/check-docker-tags.sh

# Run the script
bash scripts/check-docker-tags.sh
```

**What I need from you:**
- List of available tags (e.g., 1.0.30, 1.0.31, latest)
- Which tag is currently running
- Confirm we have at least 2 versions for rollback testing

**If fewer than 2 versions exist**, you'll need to:
1. Make a trivial change (e.g., update README)
2. Commit and push
3. Trigger Jenkins build to create another version
4. Share the new build number/tag

---

## STEP 2: Decide on Clean Node Strategy

### Option A: Fresh WSL2 Distro (RECOMMENDED)
```powershell
# In Windows PowerShell
wsl --list
wsl --export Ubuntu C:\temp\ubuntu-backup.tar
wsl --import Ubuntu-Task14 C:\WSL\Ubuntu-Task14 C:\temp\ubuntu-backup.tar
wsl -d Ubuntu-Task14
```

### Option B: Clean Existing WSL2 Node
```bash
# In WSL2, remove ALL Task 13 artifacts
sudo systemctl stop feedback-app
sudo docker rm -f feedback-app
sudo userdel -r feedbackapp
sudo rm -rf /opt/course-feedback
sudo rm -rf /var/log/course-feedback
sudo rm /etc/systemd/system/feedback-app.service
sudo systemctl daemon-reload
sudo apt remove --purge docker.io
sudo apt autoremove

# Verify clean state
id feedbackapp          # Should fail
ls /opt/course-feedback # Should not exist
docker --version        # Should fail
ss -tulpn | grep 8082   # Should be empty
```

**Which option do you prefer?** Let me know and I'll tailor the next steps accordingly.

---

## STEP 3: Capture "Before" Evidence

After choosing and preparing a clean node, run:

```bash
# These should all fail or show nothing
id feedbackapp
ls /opt/course-feedback
docker --version
ss -tulpn | grep 8082

# Save output
{
  echo "=== Clean Node Evidence ==="
  echo "Date: $(date)"
  echo ""
  echo "Checking feedbackapp user:"
  id feedbackapp 2>&1
  echo ""
  echo "Checking /opt/course-feedback:"
  ls -la /opt/course-feedback 2>&1
  echo ""
  echo "Checking Docker:"
  docker --version 2>&1
  echo ""
  echo "Checking port 8082:"
  ss -tulpn | grep 8082
} > /tmp/clean-node-evidence.txt

cat /tmp/clean-node-evidence.txt
```

**Share this output with me.**

---

## Current Status

- ✅ Branch created: `feature/task-14-provisioning-validation`
- ✅ Puppet module inspected
- ✅ Hiera configuration reviewed
- ✅ Dockerfile and Jenkinsfile analyzed
- ⏳ **WAITING**: Docker image versions list
- ⏳ **WAITING**: Clean node strategy decision

---

## What Happens Next

Once you provide:
1. Available Docker image tags
2. Clean node strategy choice
3. Clean node evidence

I will:
1. Update Hiera with specific version tags
2. Create provisioning scripts
3. Create health-check and rollback scripts
4. Provide complete STEP 3-7 commands
5. Generate all documentation

---

## Quick Decision Tree

**Do you have 2+ versions in Docker Hub?**
- ✅ **YES** → Proceed to clean node setup
- ❌ **NO** → Trigger Jenkins build first

**Which clean node approach?**
- 🚀 **Fresh WSL2** → Best for true "clean" provisioning
- 🧹 **Clean existing** → Faster, but must verify complete cleanup
- 📦 **Vagrant VM** → Most isolated, but slower setup

**Let me know your answers and I'll continue!**
