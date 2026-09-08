pipeline {
    agent any

    tools {
        maven 'Maven-3.9.6'
        jdk 'JDK-21'
    }

    stages {
        stage('Build & Test') {
            steps {
                bat 'mvn clean test -Dtest=TestRunner'
            }
        }
    }

    post {
        always {
            // Cucumber Rapor Yolu
            cucumber buildStatus: "UNSTABLE",
                     fileIncludePattern: "**/cucumber.json",
                     jsonReportDirectory: "target/cucumber-reports"

            // Aluure Rapor Yolu
            allure includeProperties: false,
                   results: [[path: 'target/allure-results']]

            // cucumber json formatinda arsivleme
            archiveArtifacts artifacts: 'target/cucumber-reports/cucumber.json', allowEmptyArchive: true

            echo 'Build ve raporlama tamamlandi!'
        }
    }
}
