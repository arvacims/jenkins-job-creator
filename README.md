# Jenkins Job Creator

This web application listens to the Gerrit event stream. When a project or branch is created in or pushed into Gerrit,
the corresponding Jenkins job is created using the `config.xml`.

### How to build

Use Maven or `docker image build .`.

### Run the application

- Using a custom `application.yml`:
~~~~
docker run -v </path/to/application.yml>:/app/config/application.yml <docker image>`
~~~~

- Using environment variables:
~~~~
docker run \
    -e 'GERRIT_HOST_NAME=<Gerrit hostname>' \
    -e 'GERRIT_SSH_PORT=<Gerrit SSH port>' \
    -e 'GERRIT_USER=<Gerrit user>' \
    -e 'GERRIT_PRIVATE_SSH_KEYFILE=<private SSH key file of Gerrit user>' \
    -e 'JENKINS_BASE_URL=<Jenkins base URL>' \
    -e 'JENKINS_USER=<Jenkins user>' \
    -e 'JENKINS_PASSWORD=<Jenkins password>' \
    -e 'JENKINS_GERRIT_USER=<non-interactive Gerrit user>' \
    <docker image>`
~~~~
