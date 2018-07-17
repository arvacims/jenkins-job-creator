#!/usr/bin/env bash

function errorExit {
    echo -e "\n(X) $1\n" >&2
    exit 1
}

function errorExitDocker {
    echo -e '\n(X) Cleaning up ("docker system prune") ...\n'
    docker system prune --force
    errorExit "$1"
}

echo -e '\n(1) Running Maven ...\n'
docker run -it --rm \
    -v "$PWD":/workspace \
    -v "$HOME/.m2":/root/.m2 \
    -w /workspace maven mvn clean package \
    || errorExitDocker 'Failed to build the Maven project.'

echo -e '\n(4) Building and tagging the Docker image ("docker build") ...\n'
declare -r workspaceName=${PWD##*/}
docker build \
    --tag "${workspaceName}:latest" \
    . \
    || errorExitDocker 'Failed to build the Docker image.'

echo -e '\n(6) Cleaning up ("docker system prune") ...\n'
docker system prune --force

exit 0