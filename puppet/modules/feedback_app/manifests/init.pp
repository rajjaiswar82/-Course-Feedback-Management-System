# Main class for Course Feedback application
# Manages Docker-based deployment of the application
#
# @summary Configures and deploys Course Feedback Management System
#
# @param image_name Docker image repository name
# @param image_tag Docker image tag/version
# @param registry Docker registry URL
# @param container_port Host port to expose the application
# @param app_env Application environment (dev/test/prod)
# @param app_user System user for application management
# @param app_group System group for application management
# @param app_dir Application root directory
# @param log_dir Log directory
# @param container_name Docker container name
# @param java_opts JVM options for the application
#
# @example
#   include feedback_app
#
class feedback_app (
  String $image_name     = 'rajjaiswar/course-feedback',
  String $image_tag      = 'latest',
  String $registry       = 'docker.io',
  Integer $container_port = 8082,
  Enum['dev', 'test', 'prod'] $app_env = 'dev',
  String $app_user       = 'feedbackapp',
  String $app_group      = 'feedbackapp',
  Stdlib::Absolutepath $app_dir = '/opt/course-feedback',
  Stdlib::Absolutepath $log_dir = '/var/log/course-feedback',
  String $container_name = 'feedback-app',
  String $java_opts      = '-Xms256m -Xmx512m',
) {
  
  # Validate parameters
  assert_type(String[1], $image_name)
  assert_type(String[1], $image_tag)
  
  # Include subclasses in correct order
  contain feedback_app::install
  contain feedback_app::user
  contain feedback_app::config
  contain feedback_app::service
  
  # Define resource ordering
  Class['feedback_app::install']
  -> Class['feedback_app::user']
  -> Class['feedback_app::config']
  -> Class['feedback_app::service']
  
  # Success notification
  notify { 'Feedback App Configuration Complete':
    message => "Course Feedback app configured: ${image_name}:${image_tag} on port ${container_port}",
    require => Class['feedback_app::service'],
  }
}
