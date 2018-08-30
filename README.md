# Jenkins Job Creator

This web application listens to the Gerrit event stream. When a project or branch is created in or pushed into Gerrit,
the corresponding Jenkins job is created using the `config.xml`.

### How to build

Use Maven or `docker image build .`.

### Run the application

- Using a custom `application.yml`:
~~~~
docker run -v /path/to/application.yml:/app/config/application.yml <docker image>`
~~~~

- Using environment variables:
~~~~
docker run \
    -e 'GERRIT_HOSTNAME=gerrit.your-domain.com' \
    -e 'GERRIT_SSH_PORT=29418' \
    -e 'GERRIT_SSH_KEY_FILE=/config/id_rsa' \
    -e 'GERRIT_SSH_KEY_PASS=secret' \
    -e 'GERRIT_BASE_URL=https://gerrit.your-domain.com' \
    -e 'GERRIT_USER=user-name' \
    -e 'GERRIT_PASSWORD=secret' \
    -e 'JENKINS_BASE_URL=https://jenkins.your-domain.com' \
    -e 'JENKINS_USER=jenkins-owner' \
    -e 'JENKINS_PASSWORD=secret' \
    -e 'JENKINS_GERRIT_USER=jenkins' \
    <docker image>`
~~~~
