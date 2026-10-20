#!/bin/bash
# Check available Docker image tags for rollback testing

REPO="rajjaiswar/course-feedback"

echo "========================================="
echo "Checking Docker Hub for available tags"
echo "Repository: $REPO"
echo "========================================="
echo ""

# Method 1: Query Docker Hub API
echo "Method 1: Docker Hub API"
curl -s "https://hub.docker.com/v2/repositories/$REPO/tags/?page_size=100" | \
  jq -r '.results[] | "\(.name)\t\(.last_updated)"' 2>/dev/null || \
  echo "jq not installed, showing raw JSON:"

echo ""
echo "Method 2: Docker inspect local images"
docker images "$REPO" --format "table {{.Repository}}:{{.Tag}}\t{{.ID}}\t{{.CreatedAt}}"

echo ""
echo "Method 3: List all rajjaiswar images"
docker images rajjaiswar/* --format "table {{.Repository}}:{{.Tag}}\t{{.ID}}\t{{.CreatedAt}}"

echo ""
echo "========================================="
echo "Recommendations for Task 14:"
echo "- Need at least 2 versions for rollback testing"
echo "- Use semantic versions (1.0.x) not 'latest'"
echo "- Identify current stable release (v1)"
echo "- Identify newer release (v2) for upgrade test"
echo "========================================="
