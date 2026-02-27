pipeline {
  agent any

  tools {
    maven 'maven'
    jdk 'jdk17'
  }

  stages {
    
    // Jenkins faz automaticamente checkout SCM no inicio de qualquer pipeline

    // stage('Checkout') {
    //   steps {
    //     checkout scm
    //   }
    // }

    stage('SonarQube Analysis') {
      steps {
        withSonarQubeEnv('sonarqube') {
          sh 'mvn clean verify sonar:sonar'
        }
      }
    }


    stage('Quality Gate') {
      steps {
        timeout(time: 2, unit: 'MINUTES') {
          waitForQualityGate abortPipeline: true
        }
      }
    }
  }
}
