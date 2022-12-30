node {
    properties(
            [
                    [$class: 'ParametersDefinitionProperty', parameterDefinitions:
                            [
                                    [$class: 'BooleanParameterDefinition', defaultValue: true, description: '테스트를 Skip 할 수 있습니다. 선택 시 테스트를 건너뛰고 체크아웃 - 빌드 - 아카이빙만 진행합니다', name: 'skipTests']
                                    , [$class: 'StringParameterDefinition', defaultValue: 'development', description: 'Maven에서 Active 할 Profile 을 입력하세요. 예) production', name: 'activeProfile']
                            ]
                    ]])

    def mvnHome
    def app

    stage('Preparation') { // for display purposes
        echo "Current workspace : ${workspace}"
        mvnHome = tool 'MavenM3'
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
        app = docker.build("pqmtest/pqm-api")
    }

    stage('Push Docker Image') {
        docker.withRegistry('http://phis.harbor.io', 'harbor-phis') {
            app.push("${env.BUILD_NUMBER}")
        }
    }

    /*
    stage('Build Docker Image') {
        //app = docker.build("phis/pqm-api")
        sh 'podman login  http://phis.harbor.io -u admin -p Harbor12345'
        sh "podman build --events-backend=file -t pqmtest/pqm-api ."
        sh "podman tag --events-backend=file localhost/pqmtest/pqm-api:latest phis.harbor.io/pqmtest/pqm-api:latest"
        sh "podman push --events-backend=file phis.harbor.io/pqmtest/pqm-api:latest"
        sh "podman logout  http://phis.harbor.io"
    }
     */

    /*
    stage('Push Docker Image') {
        docker.withRegistry('http://docker-registry:5000') {
            app.push("${env.BUILD_NUMBER}")
            app.push("latest")
        }
    }
     */

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