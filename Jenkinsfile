#!groovy

node('maven') {
    def artServer = Artifactory.server('artifactory');
    artServer.credentialsId='artifactory-admin-credential';
    def rtMaven = Artifactory.newMavenBuild();
    def buildInfo = Artifactory.newBuildInfo();
    rtMaven.resolver server: artServer, releaseRepo: 'maven-release', snapshotRepo: 'maven-release';
    rtMaven.deployer server: artServer, releaseRepo: 'app-stages-local', snapshotRepo: 'app-dev-local';
    //rtMaven.tool = 'maven';
    
    stage('pre-build'){
        git credentialsId: 'git-biancl', url: 'http://200.31.147.77/devops/ansible-maven-sample.git'
    }
    
    stage('test') {
        rtMaven.run pom: 'pom.xml', goals: 'sonar:sonar -Dsonar.host.url=http://200.31.147.144:19000/', buildInfo: buildInfo;
    }

    stage('build'){
        rtMaven.run pom: 'pom.xml', goals: '-X clean install', buildInfo: buildInfo;
    }
    
    stage('publish'){
        echo 'published...'
    } 
    
}
