pipeline{
    agent any
    tools {
        
    }

    stages {
        stage ('Build') {
            steps {
                if (isUnix()) {
                    sh './gradlew clean build'
                } else {
                    bat 'gradlew.bat clean build'
                }
            }
        }

        stage ('Test') {
            steps {
                echo 'testing skips..'
            }
        }

        stage ('Deploy') {
            steps {
                echo 'ddd...'
            }
        }
    }
}