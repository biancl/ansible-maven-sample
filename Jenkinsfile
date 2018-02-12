#!groovy

node('maven') {
    def artServer = Artifactory.server('artifactory');
    artServer.credentialsId='artifactory-admin-credential';
    def rtMaven = Artifactory.newMavenBuild();
    def buildInfo = Artifactory.newBuildInfo();
//    try{
    buildInfo.env.capture = true;
    rtMaven.resolver server: artServer, releaseRepo: 'maven-release', snapshotRepo: 'maven-release';
    rtMaven.deployer server: artServer, releaseRepo: 'app-stages-local', snapshotRepo: 'app-dev-local';
    rtMaven.tool = 'maven';
    env.JAVA_HOME = '/usr/lib/jvm/java-1.8.0';

    stage('pre-build'){
        git credentialsId: 'git-biancl', url: 'http://200.31.147.77/devops/ansible-maven-sample.git'
    }
    
   stage('sonar') {
       
//      withSonarQubeEnv('sonar'){
            configFileProvider([configFile(fileId: 'mvn-settings', targetLocation: '.m2/settings.xml', variable: 'M2_SETTINGS')]) {
                sh 'echo `which java`'
                sh 'mvn -X -gs ${M2_SETTINGS}  sonar:sonar -Dsonar.host.url=http://cwap.cfets.com:19000'
                echo 'sonar...';
            }
            
//        }
    }
 
 

    stage('build'){
        rtMaven.deployer.deployArtifacts = true;
        rtMaven.run pom: 'pom.xml', goals: 'clean install ', buildInfo: buildInfo;
        hygieiaDeployPublishStep applicationName: 'openshift-ansible-maven-sample', artifactDirectory: '/tmp/workspace/devops-ansible-maven-sample/ansible-maven-sample/target', artifactGroup: 'com.cfets.devops', artifactName: '*.war', artifactVersion: '0.0.7-SNAPSHOT', buildStatus: 'Success', environmentName: 'dev-openshift'
        buildInfo.env.capture = true;
        buildInfo.env.collect();
        artServer.publishBuildInfo buildInfo;
    }
    
    // }catch(e){
    //     echo '执行错误 Error:'+e.toString();
    //     throw e;
    // }finally{
    //     echo 'published...';
    //     artServer.publishBuildInfo buildInfo;
    // }

    
}
