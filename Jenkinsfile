// Build properties
properties([
  buildDiscarder(
    logRotator(
      artifactDaysToKeepStr: '',
      artifactNumToKeepStr: '',
      daysToKeepStr: '',
      numToKeepStr: '10'
    )
  ),
  disableConcurrentBuilds(),
  disableResume(),
  pipelineTriggers([
    cron('H H * * *')
  ])
])

// Repository name use, must end with / or be '' for none.
// Setting this to '' will also disable any pushing
repository= 'area51/'

// image prefix
imagePrefix = 'opendatabridge'

// The architectures to build. This is an array of [node,arch]
architectures = [
 ['AMD64', 'amd64']
]

// The modules to build.
// Most projects will have a dummy entry ['Build'] some like nre-feeds have multiple
// ones.
// Note 'Build' is a magic keyword here for a single module project
modules = [ 'Build' ]

// ======================================================================
// Do not modify anything below this point
// ======================================================================

// The image tag (i.e. repository/image but no version)
imageTag=repository + imagePrefix

// The image version based on the branch name - master branch is latest in docker
version=BRANCH_NAME
if( version == 'master' ) {
  version = 'latest'
}

// Build each architecture on each node in parallel
modules.each {
  module -> stage( module ) {
    def builders = [:]
    for( architecture in architectures ) {
      // Need to bind these before the closure, cannot access these as architecture[x]
      def nodeId = architecture[0]
      def arch = architecture[1]
      builders[arch] = {
        node( nodeId ) {
          withCredentials([
            usernameColonPassword(credentialsId: 'artifact-publisher', variable: 'UPLOAD_CRED')]
          ) {
            stage( arch ) {
              checkout scm

              sh 'docker build -t ' + imageTag + ':' + version + ' .'

              if( repository != '' ) {
                sh 'docker push ' + imageTag + ':' + version
              }
            }
          }
        }
      }
    }
    parallel builders
  }
}
