#!groovy

node {
    
    def artServer = Artifactory.server('artifactory');
    artServer.credentialsId='artifactory-admin-credential';
    def rtMaven = Artifactory.newMavenBuild();
    def buildInfo = Artifactory.newBuildInfo();
    try{
    buildInfo.env.capture = true;
    rtMaven.resolver server: artServer, releaseRepo: 'maven-release', snapshotRepo: 'maven-release';
    rtMaven.deployer server: artServer, releaseRepo: 'app-stages-local', snapshotRepo: 'app-dev-local';
    rtMaven.tool = 'maven';
    env.JAVA_HOME = '/usr/lib/jvm/java-1.8.0';
    stage('pre-build'){
        git credentialsId: 'git-biancl', url: 'http://200.31.147.77/devops/ansible-maven-sample.git'
    }
    
   //stage('sonar') {
       
       // withSonarQubeEnv('sonar'){
       //     configFileProvider([configFile(fileId: 'mvn-settings', targetLocation: '.m2/settings.xml', variable: 'M2_SETTINGS')]) {
                // some block
       //         sh 'mvn -gs ${M2_SETTINGS}  sonar:sonar -Dsonar.host.url=http://cwap.cfets.com:19000'
       //         echo 'sonar...';
      //      }

            //rtMaven.deployer.deployArtifacts = false;
            //rtMaven.run pom: 'pom.xml', goals: 'sonar:sonar';
           // sh 'mvn sonar:sonar -Dsonar.host.url=http://cwap.cfets.com:19000'
       // }
   // }
    stage('build'){
        rtMaven.deployer.deployArtifacts = true;
        rtMaven.run pom: 'pom.xml', goals: 'clean package install sonar:sonar -Dsonar.host.url=http://cwap.cfets.com:19000', buildInfo: buildInfo;
        buildInfo.env.capture = true;
        buildInfo.env.collect();
        hygieiaDeployPublishStep applicationName: 'ansible-maven-sample', artifactDirectory: '/target', artifactGroup: 'com.cfets.devops', artifactName: '*.jar', artifactVersion: '0.0.7-SNAPSHOT', buildStatus: 'Success', environmentName: 'dev'
    }
    }catch(e){
        echo '执行错误 Error:'+e.toString();
        throw e;
    }finally{
        echo 'published...';
        artServer.publishBuildInfo buildInfo;
    }
    
    
}
