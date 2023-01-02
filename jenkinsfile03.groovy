properties([parameters([
        choice(choices: ['dev', 'prod'], name: 'activeProfile', description: 'Maven Active Profile'),
        choice(choices: ['registry.hub.docker.com', 'phis.harbor.io'], name: 'dockerRegistry', description: 'dockerRegistry'),
        choice(choices: ['dockerhub-bless2k'], name: 'registryCredential', description: 'registryCredential'),
        choice(choices: ['bless2k/pqm-api'], name: 'dockerImageName', description: 'dockerImageName')
])])

pipeline {
    agent any

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
                stash includes: 'k8s_deployment.yaml', name: 'K8S_DEPL'
            }
        }

        stage('Build Package') {
            steps {
                withMaven(maven: 'MavenM3') {
                    sh "mvn -P ${activeProfile} -Dmaven.test.skip=true clean package"
                    stash includes: 'target/BsBoard-*.jar', name: 'APP_JAR'
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
                        unstash 'APP_JAR'
                        sh "/kaniko/executor -f `pwd`/Dockerfile -c `pwd`  --insecure --skip-tls-verify --cache=true --destination=${dockerRegistry}/${dockerImageName}:${env.BUILD_NUMBER}"
                    }
                }
            }
        }

        stage('Kubernetes Deploy') {
            steps {
                withKubeConfig([credentialsId: 'kube-secret']) {
                    unstash 'K8S_DEPL'
                    echo "NODE_NAME = ${env.NODE_NAME}"
                    sh "sed -i \"s,__IMAGE_NAME__,${dockerImageName}:${env.BUILD_NUMBER},\" k8s_deployment.yaml"
                    sh "/usr/bin/kubectl apply -f k8s_deployment.yaml"
                }
            }
        }
    }
}