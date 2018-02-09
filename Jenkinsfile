#!groovy

node('maven') {
    def artServer = Artifactory.server('artifactory');
    artServer.credentialsId='artifactory-admin-credential';
    def rtMaven = Artifactory.newMavenBuild();
    //def buildInfo = Artifactory.newBuildInfo();
    //buildInfo.env.capture = true;
    rtMaven.resolver server: artServer, releaseRepo: 'maven-release', snapshotRepo: 'maven-release';
    rtMaven.deployer server: artServer, releaseRepo: 'app-stages-local', snapshotRepo: 'app-dev-local';
    rtMaven.tool = 'maven';
    env.JAVA_HOME = '/usr/lib/jvm/java-1.8.0';
    stage('pre-build'){
        git credentialsId: 'git-biancl', url: 'http://200.31.147.77/devops/ansible-maven-sample.git'
    }
    
   // stage('test') {
    //    sh 'mvn -X sonar:sonar -Dsonar.host.url=http://200.31.147.144:19000/'
 //   }

    stage('build'){
        def buildInfo = rtMaven.run pom: 'pom.xml', goals: '-X clean install ';
        artServer.publishBuildInfo buildInfo;
    }
    
    stage('publish'){
        echo 'published...';
    } 
    
}
