properties([parameters([choice(choices: 'dev\nprod', name: 'activeProfile', description: 'Maven Active Profile')])])

pipeline {
    agent {
        node {
            label "pod-template-test01"
        }
    }

    stages {
        stage('Preparation') { // for display purposes
            steps {
                echo "Current workspace : ${workspace}"
            }
        }

        stage('Checkout') {
            steps {
                checkout scm
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