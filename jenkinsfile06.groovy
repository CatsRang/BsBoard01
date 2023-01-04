properties([parameters([
        choice(choices: ['dev', 'prod'], name: 'activeProfile', description: 'Maven Active Profile'),
        choice(choices: ['phis.harbor.io', 'registry.hub.docker.com'], name: 'dockerRegistry', description: 'dockerRegistry'),
        choice(choices: ['cred-harbor-admin', 'dockerhub-bless2k'], name: 'registryCredential', description: 'registryCredential'),
        choice(choices: ['pqmtest/pqm-api', 'bless2k/pqm-api'], name: 'dockerImageName', description: 'dockerImageName')
])])

pipeline {
    agent { label "built-in" }

    options {
        skipDefaultCheckout(true)
    }

    stages {
        stage('Checkout') {
            steps {
                echo "> activeProfile : ${activeProfile}"
                echo "> dockerRegistry : ${dockerRegistry}"
                echo "> registryCredential : ${registryCredential}"
                echo "> dockerImageName : ${dockerImageName}"

                checkout scm
            }
        }

        stage('Build Package') {
            steps {
                withMaven(maven: 'tool-maven') {
                    sh "mvn -P ${activeProfile} -Dmaven.test.skip=true clean package"
                }
            }
        }

        stage('Docker Build') {
            steps {
                withRegistry("http://${dockerRegistry}", registryCredential) {
                    def app = docker.build(dockerImageName)
                    app.push("${env.BUILD_NUMBER}")
                    //app.push("latest");
                }
            }
        }

        stage('Kubernetes Deploy') {
            steps {
                withKubeConfig([credentialsId: 'cred-k8s-admin']) {
                    echo "NODE_NAME = ${env.NODE_NAME}"
                    sh "sed -i \"s,__IMAGE_NAME__,${dockerImageName}:${env.BUILD_NUMBER},\" k8s_deployment.yaml"
                    sh "/usr/bin/kubectl apply -f k8s_deployment.yaml"
                }
            }
        }
    }
}