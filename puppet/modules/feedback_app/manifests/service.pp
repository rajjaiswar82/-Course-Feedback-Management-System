# Manage application service
# @api private
class feedback_app::service {
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
  $health_url = "http://localhost:${feedback_app::container_port}/"
  $health_cmd = "/usr/bin/curl -f -s -o /dev/null -w '%{http_code}' ${health_url} | /bin/grep -q '200\\|302'"
  $container_check = "/usr/bin/docker ps --filter name=${feedback_app::container_name} --filter status=running"
  $container_running = "${container_check} --format '{{.Names}}' | /bin/grep -q ${feedback_app::container_name}"

  exec { 'health-check':
    command   => $health_cmd,
    tries     => 10,
    try_sleep => 5,
    require   => Service['feedback-app'],
    unless    => $container_running,
  }
}
