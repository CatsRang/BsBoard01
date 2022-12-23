
node {
    // Jenkins 파일에서 취급하는 파라미터들을 미리 정의한다.
    // 아래와 같이 미리 정의하면 Jenkins Job 이 Parametrized Job 이 되며 기본 변수들이 들어가게 된다
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
        docker.withRegistry('http://host.docker.internal:5000') {
            app.push("${env.BUILD_NUMBER}")
            app.push("latest")
        }
    }

    stage('Deploy') {
        echo "Deploy is not yet implemented"
    }
}