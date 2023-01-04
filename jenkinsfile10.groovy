node('builder-node') {
    properties([[$class: 'ParametersDefinitionProperty', parameterDefinitions: [
            [$class: 'ChoiceParameterDefinition', description: 'Maven Active Profile', choices: ['dev', 'prd'], name: 'activeProfile'],
            [$class: 'StringParameterDefinition', defaultValue: 'registry.hub.docker.com', description: 'Registry Url. ex) phis.harbor.io  registry.hub.docker.com', name: "dockerRegistry"],
            [$class: 'StringParameterDefinition', defaultValue: 'cred-dockerhub-bless2k', description: 'Registry Credential', name: "registryCredential"],
            [$class: 'StringParameterDefinition', defaultValue: 'bless2k/pqm-apii', description: 'Docker Image Name', name: "dockerImageName"]
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
        docker.withRegistry("https://${dockerRegistry}", registryCredential) {
            def app = docker.build(dockerImageName)
            app.push("${env.BUILD_NUMBER}")
            //app.push("latest");
        }
    }

    stage('Kubernetes Deploy') {
        withKubeConfig([credentialsId: 'cred-k8s-admin']) {
            // TODO kubectl delete (deployment)
            sh "sed -i \"s,__IMAGE_NAME__,${dockerRegistry}/${dockerImageName}:${env.BUILD_NUMBER},\" k8s_deployment.yaml"
            sh "/usr/bin/kubectl apply -f k8s_deployment.yaml"
        }
    }
}