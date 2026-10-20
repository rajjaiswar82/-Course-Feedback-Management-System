# Install required packages
# @api private
class feedback_app::install {
  # Ensure package list is up to date
  $apt_stamp = '/var/lib/apt/periodic/update-success-stamp'
  $apt_check = "/usr/bin/test -f ${apt_stamp} && /usr/bin/find ${apt_stamp} -mmin -60 | /bin/grep -q update-success-stamp"

  exec { 'apt-update':
    command => '/usr/bin/apt-get update',
    unless  => $apt_check,
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
