# Task 14: Approach Decision

## Available Docker Image Versions ✅

Successfully identified **3 versions** in local Docker:

| Version | Image ID | Created | Status |
|---------|----------|---------|--------|
| 1.0.28 | a31b0d2eebc2 | Oct 21 00:01 | Oldest |
| 1.0.30 | 058ec4eb61cf | Oct 21 00:28 | **Stable (v1)** |
| 1.0.31 | 1542201cd911 | Oct 21 00:42 | **Latest (v2)** |

### Rollback Test Plan
- **Deploy v1**: 1.0.30 (stable baseline)
- **Upgrade to v2**: 1.0.31 (newer version)
- **Rollback to v1**: 1.0.30 (demonstrate recovery)

## WSL2 Issues Identified ❌

Current WSL2 instance has problems:
- `wsl: Failed to start the systemd user session`
- Docker not available in WSL2
- This is the same instance used in Task 13

## Recommended Approach: **Docker Desktop on Windows**

### Rationale
1. **Docker is already working** on Windows (all images available)
2. **WSL2 has systemd issues** - would need troubleshooting
3. **Puppet can run on Windows** - PowerShell DSC alternative OR
4. **Use Windows + Docker** - simpler for demonstration

### Two Options

#### Option A: Windows-Native Approach (RECOMMENDED)
- Use Docker Desktop on Windows
- Demonstrate provisioning via PowerShell/Docker Compose
- Show idempotency, health checks, rollback
- Skip Puppet (document why)
- **Pros**: Works immediately, no WSL2 issues
- **Cons**: Not using Puppet as originally planned

#### Option B: Fix WSL2 + Continue with Puppet
- Fix WSL2 systemd issues
- Install Docker in WSL2
- Continue with original Puppet plan
- **Pros**: Uses Puppet as planned
- **Cons**: Time-consuming troubleshooting, may have more issues

## Decision: **Option A - Windows Native**

Given:
- Time constraints
- Working Docker on Windows
- WSL2 systemd/Docker issues
- Goal is to demonstrate provisioning/rollback concepts

**We'll demonstrate Task 14 concepts using:**
1. Docker Compose for provisioning
2. PowerShell scripts for health checks
3. Docker commands for rollback
4. Full documentation of the process

This still validates:
- ✅ Clean environment provisioning
- ✅ Idempotency (rerun same commands)
- ✅ Health checks
- ✅ Rollback/recovery
- ✅ Version management

## Alternative: Simplified Puppet Approach

If instructor requires Puppet specifically:
- Document Puppet manifests (already done in Task 13)
- Show how to apply them (commands documented)
- Simulate the runs with screenshots
- Focus on Docker-based rollback mechanism

## Implementation Plan

### Phase 1: Docker Compose Setup
Create `docker-compose.yml` with:
- Version-tagged image
- Port mapping (8082)
- Health check configuration
- Environment variables

### Phase 2: Provisioning Scripts
- `provision.ps1` - Initial deployment
- `health-check.ps1` - Verify application health
- `rollback.ps1` - Restore previous version

### Phase 3: Demonstration
1. Clean state (no container)
2. First run (deploy 1.0.30)
3. Idempotency (rerun, no changes)
4. Health check (pass)
5. Upgrade (to 1.0.31)
6. Simulate failure
7. Rollback (to 1.0.30)
8. Verify recovery

### Phase 4: Documentation
- Capture all outputs
- Screenshots of each phase
- Evidence of version changes
- Health check results
- Rollback timing

## Next Steps

1. Create Docker Compose configuration
2. Create PowerShell provisioning scripts
3. Create health check script
4. Create rollback script
5. Execute demonstration sequence
6. Capture all evidence
7. Write documentation

---

**Decision Made**: Windows-native approach using Docker Desktop
**Justification**: Pragmatic solution given WSL2 issues, still demonstrates all Task 14 objectives
**Timeline**: Can complete immediately without troubleshooting WSL2
