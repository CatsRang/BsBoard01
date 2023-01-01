properties([parameters([
        choice(choices: ['dev', 'prod'], name: 'activeProfile', description: 'Maven Active Profile'),
        choice(choices: ['registry.hub.docker.com', 'phis.harbor.io'], name: 'dockerRegistry', description: 'dockerRegistry'),
        choice(choices: ['dockerhub-bless2k'], name: 'registryCredential', description: 'registryCredential'),
        choice(choices: ['bless2k/pqm-api'], name: 'dockerImageName', description: 'dockerImageName')
])])

pipeline {
    agent none

    options {
        skipDefaultCheckout(true)
    }

    stages {
        stage('Checkout') {
            agent { node { label "pod-kaniko" } }

            steps {
                echo "> Current workspace : ${workspace}"
                echo "> activeProfile : ${activeProfile}"
                echo "> dockerRegistry : ${dockerRegistry}"
                echo "> registryCredential : ${registryCredential}"
                echo "> dockerImageName : ${dockerImageName}"

                checkout scm
            }
        }

        stage('Build Package') {
            agent { node { label "pod-kaniko" } }

            steps {
                container(name: "container-maven") {
                    sh "mvn -P ${activeProfile} -Dmaven.test.skip=true clean package"
                }
            }
        }

        stage('Build Docker Image') {
            agent { node { label "pod-kaniko" } }

            steps {
                container(name: "container-kaniko", shell: "/busybox/sh") {
                    withCredentials([file(credentialsId: 'secret-kaniko', variable: 'CONF_KANIKO')]) {
                        sh "mkdir -p /kaniko/.docker"
                        sh "cp $CONF_KANIKO /kaniko/.docker/config.json"
                        sh "/kaniko/executor -f `pwd`/Dockerfile --context=`pwd` --insecure --skip-tls-verify --cache=true --destination=$dockerRegistry/$dockerImageName:$env.BUILD_NUMBER"
                    }
                }
            }
        }

        stage('Kubernetes Deploy') {
            agent none

            steps {
                withKubeConfig([credentialsId: 'kube-secret']) {
                    sh "sed -i \"s,__IMAGE_NAME__,$dockerRegistry/$dockerImageName:$env.BUILD_NUMBER,\" k8s_deployment.yaml"
                    sh "/usr/bin/kubectl apply -f k8s_deployment.yaml"
                }
            }
        }
    }
}