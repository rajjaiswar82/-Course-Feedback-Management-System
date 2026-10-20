# Install required packages
# @api private
class feedback_app::install {
  # Ensure package list is up to date
  exec { 'apt-update':
    command => '/usr/bin/apt-get update',
    unless  => '/usr/bin/test -f /var/lib/apt/periodic/update-success-stamp && /usr/bin/find /var/lib/apt/periodic/update-success-stamp -mmin -60 | /bin/grep -q update-success-stamp',
    timeout => 300,
  }
  
  # Install required packages
  package { 'docker.io':
    ensure  => installed,
    require => Exec['apt-update'],
  }
  
  package { 'curl':
    ensure  => installed,
    require => Exec['apt-update'],
  }
  
  package { 'git':
    ensure  => installed,
    require => Exec['apt-update'],
  }
  
  package { 'ca-certificates':
    ensure  => installed,
    require => Exec['apt-update'],
  }
  
  # Ensure Docker service is enabled and running
  service { 'docker':
    ensure  => running,
    enable  => true,
    require => Package['docker.io'],
  }
}
