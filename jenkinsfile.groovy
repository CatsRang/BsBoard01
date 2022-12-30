node {
    properties(
            [
                    [$class: 'ParametersDefinitionProperty', parameterDefinitions:
                            [
                                    [$class: 'ChoiceParameterDefinition', description:'Maven Active Profile', choices: ['dev', 'prd'], name: 'activeProfile'],
                                    [$class: 'StringParameterDefinition', defaultValue: 'http://phis.harbor.io', description: 'Registry Url. ex) https://registry.hub.docker.com', name:"dockerRegistry"],
                                    [$class: 'StringParameterDefinition', defaultValue: 'harbor-phis', description: 'Registry Credential', name:"registryCredential"],
                                    [$class: 'StringParameterDefinition', defaultValue: 'pqmtest/pqm-api', description: 'Docker Image Name', name:"dockerImageName"]
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
            app.push("${env.BUILD_NUMBER}")
        }
    }

    /*
    stage('Kubernetes Deploy') {
        withKubeConfig([credentialsId: 'kube_config']) {
            sh 'curl -LO "https://storage.googleapis.com/kubernetes-release/release/$(curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt)/bin/linux/amd64/kubectl"'
            sh 'chmod u+x ./kubectl'
//            sh "./kubectl delete deployment bsboard-b01 -n pqmtest"
            sh "./kubectl apply -f k8s_deployment.yaml"
            //kubernetesDeploy(configs: "k8s_deployment.yml", kubeconfigId: "kubernetes")
        }
    }
     */
}