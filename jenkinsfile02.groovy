properties([parameters([choice(choices: 'dev\nprod', name: 'activeProfile', description: 'Maven Active Profile')])])

pipeline {
    agent {
        node {
            label "pod-template-test01"
        }
    }

    stages {
        stage('Preparation') { // for display purposes
            echo "Current workspace : ${workspace}"
        }

        stage('Checkout') {
            checkout scm
        }

        /*
        stage('Build Package') {
            withMaven(
                    maven: 'MavenM3'
            ) {
                sh "mvn -P ${activeProfile} -Dmaven.test.skip=true clean package"
            }
        }
         */

        /*
        stage('Archive') {
            archiveArtifacts artifacts: '**/target/*.jar'
        }
        */

        stage('Build Docker Image') {
            container("container-test01") {
                sh "echo Hello from $POD_CONTAINER"
            }
        }
    }
}