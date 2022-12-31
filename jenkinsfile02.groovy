properties([parameters([
        choice(choices: ['dev', 'prod'], name: 'activeProfile', description: 'Maven Active Profile'),
        choice(choices: ['registry.hub.docker.com', 'phis.harbor.io'], name: 'dockerRegistry', description: 'dockerRegistry'),
        choice(choices: ['dockerhub-bless2k'], name: 'registryCredential', description: 'registryCredential'),
        choice(choices: ['bless2k/pqm-ap'], name: 'dockerImageName', description: 'dockerImageName')
])])

pipeline {
    agent {
        node {
            label "pod-kaniko"
        }
    }

    stages {
        stage('Preparation') { // for display purposes
            steps {
                echo "> Current workspace : ${workspace}"
                echo "> activeProfile : ${activeProfile}"
                echo "> dockerRegistry : ${dockerRegistry}"
                echo "> registryCredential : ${registryCredential}"
                echo "> dockerImageName : ${dockerImageName}"
            }
        }

        /*
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
         */

        stage('Build Package') {
            steps {
                container("container-maven") {
                    checkout scm
                    sh "mvn -P ${activeProfile} -Dmaven.test.skip=true clean package"
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                container("container-kaniko") {
                    sh 'mkdir -p /kaniko/.docker'
                    sh '/kaniko/executor -f `pwd`/Dockerfile --insecure --skip-tls-verify --cache=true --destination=${dockerRegistry}/${dockerImageName}:${env.BUILD_NUMBER}'
                    // sh '/kaniko/executor --context=git://github.com/repository/project.git  --destination=docker.io/repository/image:tag --insecure --skip-tls-verify  -v=debug'
                }
            }
        }
    }
}