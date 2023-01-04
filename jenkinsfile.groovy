node('built-in') {
    properties([[$class: 'ParametersDefinitionProperty', parameterDefinitions: [
            [$class: 'ChoiceParameterDefinition', description: 'Maven Active Profile', choices: ['dev', 'prd'], name: 'activeProfile'],
            [$class: 'StringParameterDefinition', defaultValue: 'phis.harbor.io', description: 'Registry Url. ex) phis.harbor.io  registry.hub.docker.com', name: "dockerRegistry"],
            [$class: 'StringParameterDefinition', defaultValue: 'cred-harbor-admin', description: 'Registry Credential', name: "registryCredential"],
            [$class: 'StringParameterDefinition', defaultValue: 'pqmtest/pqm-api', description: 'Docker Image Name', name: "dockerImageName"]
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
        docker.withRegistry("http://${dockerRegistry}", registryCredential) {
            def app = docker.build(dockerImageName)
            app.push("${env.BUILD_NUMBER}")
            //app.push("latest");

            //sh "buildah bud -t ${dockerRegistry}/${dockerImageName}:${env.BUILD_NUMBER} ."
        }
    }

    stage('Kubernetes Deploy') {
        withKubeConfig([credentialsId: 'cred-k8s-admin']) {
            sh "sed -i \"s,__IMAGE_NAME__,${dockerRegistry}/${dockerImageName}:${env.BUILD_NUMBER},\" k8s_deployment.yaml"
            sh "/usr/bin/kubectl apply -f k8s_deployment.yaml"
        }
    }
}