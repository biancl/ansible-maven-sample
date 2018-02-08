#!groovy

node {
    def artServer = Artifactory.server('artifactory');
    artServer.credentialsId='artifactory-admin-credential';
    

    stage('build'){
    def rtMaven = Artifactory.newMavenBuild();
    def buildInfo = Artifactory.newBuildInfo();
    rtMaven.resolver server: artServer, releaseRepo: 'maven-release', snapshotRepo: 'maven-release';
    rtMaven.deployer server: artServer, releaseRepo: 'app-stages-local', snapshotRepo: 'app-dev-local';
    rtMaven.tool = 'maven';
    rtMaven.run pom: 'pom.xml', goals: 'clean install', buildInfo: buildInfo;
    }
    
    stage('publish'){
        
    } 
    
}
