properties([parameters([
        choice(choices: ['dev', 'prod'], name: 'activeProfile', description: 'Maven Active Profile'),
        choice(choices: ['registry.hub.docker.com', 'phis.harbor.io'], name: 'dockerRegistry', description: 'dockerRegistry'),
        choice(choices: ['dockerhub-bless2k'], name: 'registryCredential', description: 'registryCredential'),
        choice(choices: ['bless2k/pqm-api'], name: 'dockerImageName', description: 'dockerImageName')
])])

pipeline {
    agent {
        node {
            label "pod-kaniko"
        }
    }

    stages {
        stage('Checkout') {
            steps {
                echo "> Current workspace : ${workspace}"
                echo "> activeProfile : ${activeProfile}"
                echo "> dockerRegistry : ${dockerRegistry}"
                echo "> registryCredential : ${registryCredential}"
                echo "> dockerImageName : ${dockerImageName}"

                container("container-maven") {
                    checkout scm
                }
            }
        }

        stage('Build Package') {
            steps {
                container(name: "container-maven") {
                    sh "mvn -P ${activeProfile} -Dmaven.test.skip=true clean package"
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                container(name: "container-kaniko", shell: "/busybox/sh") {
                    withCredentials([file(credentialsId: 'secret-kaniko', variable: 'CONF_KANIKO')]) {
                        sh "mkdir -p /kaniko/.docker"
                        sh "cp $CONF_KANIKO /kaniko/.docker/config.json"
//                        sh "cp `pwd`/.docker/config.json /kaniko/.docker"
                        sh "/kaniko/executor -f `pwd`/Dockerfile --context=`pwd` --insecure --skip-tls-verify --cache=true --destination=${dockerRegistry}/${dockerImageName}:${env.BUILD_NUMBER}"
                    }
                }
            }
        }
    }
}