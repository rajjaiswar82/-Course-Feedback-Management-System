# Manage application service
# @api private
class feedback_app::service {
  # Ensure Docker service is running
  service { 'docker':
    ensure => running,
    enable => true,
  }
  
  # Manage application service
  service { 'feedback-app':
    ensure    => running,
    enable    => true,
    provider  => 'systemd',
    require   => [
      Service['docker'],
      File['/etc/systemd/system/feedback-app.service'],
      Exec['systemd-reload'],
    ],
    subscribe => File['/etc/systemd/system/feedback-app.service'],
  }
  
  # Health check (verify container is running)
  exec { 'health-check':
    command => "/usr/bin/curl -f -s -o /dev/null -w '%{http_code}' http://localhost:${feedback_app::container_port}/ | /bin/grep -q '200\\|302'",
    tries   => 10,
    try_sleep => 5,
    require => Service['feedback-app'],
    unless  => "/usr/bin/docker ps --filter name=${feedback_app::container_name} --filter status=running --format '{{.Names}}' | /bin/grep -q ${feedback_app::container_name}",
  }
}
