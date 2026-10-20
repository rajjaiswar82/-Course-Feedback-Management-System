# Create user, group and directories
# @api private
class feedback_app::user {
  # Create system group
  group { $feedback_app::app_group:
    ensure => present,
    system => true,
  }

  # Create system user
  user { $feedback_app::app_user:
    ensure     => present,
    system     => true,
    gid        => $feedback_app::app_group,
    home       => $feedback_app::app_dir,
    managehome => false,
    shell      => '/usr/sbin/nologin',
    comment    => 'Course Feedback Application User',
    groups     => ['docker'],
    require    => [Group[$feedback_app::app_group], Package['docker.io']],
  }

  # Create application directories
  file { $feedback_app::app_dir:
    ensure  => directory,
    owner   => $feedback_app::app_user,
    group   => $feedback_app::app_group,
    mode    => '0755',
    require => User[$feedback_app::app_user],
  }

  file { "${feedback_app::app_dir}/config":
    ensure  => directory,
    owner   => $feedback_app::app_user,
    group   => $feedback_app::app_group,
    mode    => '0750',
    require => File[$feedback_app::app_dir],
  }

  file { "${feedback_app::app_dir}/releases":
    ensure  => directory,
    owner   => $feedback_app::app_user,
    group   => $feedback_app::app_group,
    mode    => '0755',
    require => File[$feedback_app::app_dir],
  }

  file { $feedback_app::log_dir:
    ensure  => directory,
    owner   => $feedback_app::app_user,
    group   => $feedback_app::app_group,
    mode    => '0755',
    require => User[$feedback_app::app_user],
  }
}
