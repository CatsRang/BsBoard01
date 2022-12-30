node {
    properties(
            [
                    [$class: 'ParametersDefinitionProperty', parameterDefinitions:
                            [
                                    [$class: 'ChoiceParameterDefinition', description: 'Maven Active Profile', choices: ['dev', 'prd'], name: 'activeProfile'],
                                    [$class: 'StringParameterDefinition', defaultValue: 'https://registry.hub.docker.com', description: 'Registry Url. ex) http://phis.harbor.io  https://registry.hub.docker.com', name: "dockerRegistry"],
                                    [$class: 'StringParameterDefinition', defaultValue: 'dockerhub-bless2k', description: 'Registry Credential', name: "registryCredential"],
                                    [$class: 'StringParameterDefinition', defaultValue: 'bless2k/pqm-api', description: 'Docker Image Name', name: "dockerImageName"]
                            ]
                    ]])

    def app

    stage('Preparation') { // for display purposes
        echo "Current workspace : ${workspace}"
    }

    stage('Checkout') {
        checkout scm
    }

    stage('Build Package') {
        withMaven(
                maven: 'MavenM3'
        ) {
            sh "mvn -P ${activeProfile} -Dmaven.test.skip=true clean package"
        }
    }

    stage('Archive') {
        archiveArtifacts artifacts: '**/target/*.jar'
    }

    stage('Build Docker Image') {
        app = docker.build(dockerImageName)
    }

    stage('Push Docker Image') {
        docker.withRegistry(dockerRegistry, registryCredential) {
            //app.push("${env.BUILD_NUMBER}")
            app.push("latest");
        }
    }

    stage('Kubernetes Deploy') {
        withKubeConfig([credentialsId: 'kube-secret']) {
            sh "./kubectl apply -f k8s_deployment.yaml"
        }
    }
}