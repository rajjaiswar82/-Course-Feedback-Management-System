# Site manifest for Course Feedback Management System
# Puppet masterless configuration
#
# Usage:
#   sudo puppet apply --modulepath=puppet/modules \
#     --hiera_config=puppet/hiera.yaml puppet/manifests/site.pp

# Default node definition (applies to all nodes)
node default {
  # Include the feedback_app module
  # Parameters will be read from Hiera (data/common.yaml)
  include feedback_app
  
  # Notify admin
  notify { 'Puppet run starting':
    message => 'Configuring Course Feedback application...',
  }
}
