node {
    properties([[$class: 'ParametersDefinitionProperty', parameterDefinitions: [
            [$class: 'ChoiceParameterDefinition', description: 'Maven Active Profile', choices: ['dev', 'prd'], name: 'activeProfile'],
            [$class: 'StringParameterDefinition', defaultValue: 'http://phis.harbor.io', description: 'Registry Url. ex) phis.harbor.io  registry.hub.docker.com', name: "dockerRegistry"],
            [$class: 'StringParameterDefinition', defaultValue: 'cred-harbor-admin', description: 'Registry Credential', name: "registryCredential"],
            [$class: 'StringParameterDefinition', defaultValue: 'phis.harbor.io/pqmtest/pqm-api', description: 'Docker Image Name', name: "dockerImageName"]
    ]]])

    stage('Checkout') {
        echo "******************* Start Building, Current workspace : ${workspace}"
        checkout scm
    }

    stage('Maven Build') {
        withMaven(maven: 'tool-maven') {
            sh "mvn -P ${activeProfile} -Dmaven.test.skip=true clean package"
        }
    }

    stage('Archive') {
        archiveArtifacts artifacts: '**/target/*.jar'
    }

    stage('Docker Build') {
        def app = docker.build(dockerImageName)
        docker.withRegistry(dockerRegistry, registryCredential) {
            app.push("${env.BUILD_NUMBER}")
            //app.push("latest");
        }
    }

    stage('Kubernetes Deploy') {
        withKubeConfig([credentialsId: 'cred-k8s-admin']) {
            sh "sed -i \"s,__IMAGE_NAME__,${dockerImageName}:${env.BUILD_NUMBER},\" k8s_deployment.yaml"
            sh "/usr/bin/kubectl apply -f k8s_deployment.yaml"
        }
    }
}