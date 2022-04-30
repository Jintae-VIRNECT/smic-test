pipeline {
    agent any

    triggers {
        pollSCM('H/5 * * * *')
    }

    tools {
        gradle 'gradle'
    }

    stages {
        stage ('Build') {
            steps {
//                 if (isUnix()) {
//                     sh './gradlew clean build'
//                 } else {
//                     bat 'gradlew.bat clean build'
//                 }
                echo 'fff'
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