# jenkins-job-creator

This application listens to the Gerrit event stream. If a project or branch is created in Gerrit, the corresponding Jenkins job is created using the deposited `config.xml`.

### Build the docker image
To build the docker image run `build.sh`.

### Run the application
`docker run -v <path to application.yml>:/app/config/application.yml job-creator`
