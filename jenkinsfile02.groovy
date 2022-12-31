properties([parameters([
        choice(choices: ['dev', 'prod'], name: 'activeProfile', description: 'Maven Active Profile'),
        choice(choices: ['https://registry.hub.docker.comhttps://registry.hub.docker.com', 'http://phis.harbor.io'], name: 'dockerRegistry', description: 'dockerRegistry'),
        choice(choices: ['dockerhub-bless2k'], name: 'registryCredential', description: 'registryCredential'),
        choice(choices: ['bless2k/pqm-ap'], name: 'dockerImageName', description: 'dockerImageName')
])])

pipeline {
    agent {
        node {
            label "pod-template-test01"
        }
    }

    stages {
        stage('Preparation') { // for display purposes
            steps {
                echo "> Current workspace : ${workspace}"
                echo "> activeProfile : ${activeProfile}"
            }
        }

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Package') {
            steps {
                withMaven(
                        maven: 'MavenM3'
                ) {
                    sh "mvn -P ${activeProfile} -Dmaven.test.skip=true clean package"
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                container("container-test01") {
                    sh "echo Hello from $POD_CONTAINER"
                }
            }
        }
    }
}