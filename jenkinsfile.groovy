
node {
    properties(
            [
                    [$class: 'ParametersDefinitionProperty', parameterDefinitions:
                            [
                                    [$class: 'BooleanParameterDefinition', defaultValue: true, description: 'Skip Test', name: 'skipTests']
                                    , [$class: 'StringParameterDefinition', defaultValue: 'development', description: 'ex) production', name: 'activeProfile']
                            ]
                    ]])

    def mvnHome
    def app

    stage('Preparation') { // for display purposes
        echo "Current workspace : ${workspace}"
        // Get the Maven tool.
        // ** NOTE: This 'M3' Maven tool must be configured
        // **       in the global configuration.
        mvnHome = tool 'MavenM3'
    }

    stage('Checkout') {
        // Get some code from a Git repository
        checkout scm
    }

//    stage('Test') {
//        sh "'${mvnHome}/bin/mvn' -P ${activeProfile} -Dmaven.test.failure.ignore -B verify"
//    }
//
//    stage('Store Test Results') {
//        junit(
//                allowEmptyResults: true,
//                testResults: '** /target/surefire-reports/TEST-*.xml'
//        )
//    }

    stage('Build Package') {
        sh "'${mvnHome}/bin/mvn' -P ${activeProfile} -Dmaven.test.skip=true clean install"
    }

    stage('Archive') {
        archive '**/target/*.jar'
    }

    stage('Build Docker Image') {
        app = docker.build("phis/pqm-api")
    }

    stage('Push Docker Image') {
        docker.withRegistry('http://docker-registry:5000') {
            app.push("${env.BUILD_NUMBER}")
            app.push("latest")
        }
    }

    stage('Kubernetes Deploy') {
        withKubeConfig([credentialsId: 'minikube_config']) {
            sh 'curl -LO "https://storage.googleapis.com/kubernetes-release/release/$(curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt)/bin/linux/amd64/kubectl"'
            sh 'chmod u+x ./kubectl'
            sh "./kubectl delete deployment bsboard-b01"
            sh "./kubectl apply -f k8s_deployment.yaml"
            //kubernetesDeploy(configs: "k8s_deployment.yml", kubeconfigId: "kubernetes")
        }
    }
}