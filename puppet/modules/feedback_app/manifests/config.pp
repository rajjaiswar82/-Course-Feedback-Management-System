# Configure application files
# @api private
class feedback_app::config {
  # Application environment file
  file { "${feedback_app::app_dir}/config/app.env":
    ensure  => file,
    content => epp('feedback_app/app.env.epp', {
      'app_env'        => $feedback_app::app_env,
      'java_opts'      => $feedback_app::java_opts,
      'container_port' => $feedback_app::container_port,
    }),
    owner   => $feedback_app::app_user,
    group   => $feedback_app::app_group,
    mode    => '0640',
    require => File["${feedback_app::app_dir}/config"],
    notify  => Service['feedback-app'],
  }

  # Systemd unit file
  file { '/etc/systemd/system/feedback-app.service':
    ensure  => file,
    content => epp('feedback_app/feedback-app.service.epp', {
      'app_user'       => $feedback_app::app_user,
      'app_dir'        => $feedback_app::app_dir,
      'image_name'     => $feedback_app::image_name,
      'image_tag'      => $feedback_app::image_tag,
      'container_name' => $feedback_app::container_name,
      'container_port' => $feedback_app::container_port,
      'app_env'        => $feedback_app::app_env,
      'java_opts'      => $feedback_app::java_opts,
    }),
    owner   => 'root',
    group   => 'root',
    mode    => '0644',
    require => File["${feedback_app::app_dir}/config/app.env"],
    notify  => Exec['systemd-reload'],
  }

  # Reload systemd when unit file changes
  exec { 'systemd-reload':
    command     => '/bin/systemctl daemon-reload',
    refreshonly => true,
  }

  # Current version marker (for rollback tracking)
  file { "${feedback_app::app_dir}/releases/current-version.txt":
    ensure  => file,
    content => "${feedback_app::image_tag}\n",
    owner   => $feedback_app::app_user,
    group   => $feedback_app::app_group,
    mode    => '0644',
    require => File["${feedback_app::app_dir}/releases"],
  }
}
