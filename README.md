# jenkins-job-creator

This application listens to the Gerrit event stream. If a project or branch is created in Gerrit, the corresponding Jenkins job is created using the deposited `config.xml`.

### Build the docker image
To build the docker image run `build.sh`.

### Run the application
  - using a custom application.yml:
  `docker run -v <path to application.yml>:/app/config/application.yml job-creator`

  - using environment variables:
  `docker run -e "GERRIT_HOST_NAME=<Gerrit host name>" -e "GERRIT_SSH_PORT=<Gerrit ssh port>" -e "GERRIT_USER=<Gerrit user>" -e "GERRIT_PRIVATE_SSH_KEYFILE=<private ssh key file of Gerrit user>" -e "JENKINS_BASE_URL=<Jenkins base url>" -e "JENKINS_USER=<Jenkins user>" -e "JENKINS_PASSWORD=<Jenkins password>" -e "JENKINS_GERRIT_USER=<non-interactive gerrit user>" job-creator`
