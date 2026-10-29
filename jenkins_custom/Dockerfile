FROM jenkins/jenkins:lts

# Switch to root to install packages
USER root

# Install Maven, Docker CLI, Chromium + matching driver, and utilities
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        maven \
        chromium \
        chromium-driver \
        ca-certificates \
        curl \
        gnupg \
        lsb-release && \
    rm -rf /var/lib/apt/lists/*

# Install Docker CLI (official Docker repository)
RUN install -m 0755 -d /etc/apt/keyrings && \
    curl -fsSL https://download.docker.com/linux/debian/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg && \
    chmod a+r /etc/apt/keyrings/docker.gpg && \
    echo "deb [arch=amd64 signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/debian \
    $(. /etc/os-release && echo "$VERSION_CODENAME") stable" > /etc/apt/sources.list.d/docker.list && \
    apt-get update && \
    apt-get install -y --no-install-recommends docker-ce-cli && \
    rm -rf /var/lib/apt/lists/*

# Switch back to Jenkins user
USER jenkins

EXPOSE 8080 50000
