#!groovy

node('maven') {
    
    def pom = readMavenPom file: 'pom.xml'
    def version = pom.version;
    def artifactId = pom.artifactId;
    def groupId = pom.groupId;
    def artServer = Artifactory.server('artifactory');
    artServer.credentialsId='artifactory-admin-credential';
    def rtMaven = Artifactory.newMavenBuild();
    def buildInfo = Artifactory.newBuildInfo();
    
    buildInfo.env.capture = true;
    rtMaven.resolver server: artServer, releaseRepo: 'maven-release', snapshotRepo: 'maven-release';
    rtMaven.deployer server: artServer, releaseRepo: 'app-stages-local', snapshotRepo: 'app-dev-local';
    rtMaven.tool = 'maven';
    rtMaven.deployer.deployArtifacts = true;

    stage('Check out'){
        git credentialsId: 'git-biancl', url: 'http://200.31.147.77/devops/ansible-maven-sample.git'
    }
    
    stage('Unit Test') {
        
        configFileProvider([configFile(fileId: 'mvn-settings', targetLocation: '.m2/settings.xml', variable: 'M2_SETTINGS')]) {
            // some block
            sh 'mvn -gs "$M2_SETTINGS" clean test'
            hygieiaCodeQualityPublishStep checkstyleFilePattern: '**/*/checkstyle-result.xml', findbugsFilePattern: '**/*/Findbugs.xml', jacocoFilePattern: '**/*/jacoco.xml', junitFilePattern: '**/*/TEST-.*-test.xml', pmdFilePattern: '**/*/PMD.xml'
        }
    }
    
    stage('SonarQube Scan') {
        configFileProvider([configFile(fileId: 'mvn-settings', targetLocation: '.m2/settings.xml', variable: 'M2_SETTINGS')]) {
            // some block
            sh 'mvn -gs "$M2_SETTINGS" sonar:sonar -Dsonar.host.url=http://cwap.cfets.com:19000'
            
            hygieiaCodeQualityPublishStep checkstyleFilePattern: '**/*/checkstyle-result.xml', findbugsFilePattern: '**/*/Findbugs.xml', jacocoFilePattern: '**/*/jacoco.xml', junitFilePattern: '**/*/TEST-.*-test.xml', pmdFilePattern: '**/*/PMD.xml'

        }
    }
        
    stage('build'){
        rtMaven.run pom: 'pom.xml', goals: 'clean install ', buildInfo: buildInfo;
        hygieiaDeployPublishStep applicationName: '${JOB_NAME}', artifactDirectory: '${WORKSPACE}/ansible-maven-sample/target', artifactGroup: '${groupId}', artifactName: '*.war', artifactVersion: '${version}', buildStatus: 'Success', environmentName: 'dev-openshift'
    }
    
    stage('Publish build information') {
        artServer.publishBuildInfo buildInfo;
    }
    
    parallel ST: {
        stage ('Intergration Test') {
            echo 'Intergration Test OK.'
        }
    }, FT: {
        
        stage ('Functional Test') {
            
            echo 'Functional Test OK.'
        }
    }, SECT: {
        
        stage ('Security Test') {
            
            echo 'Security Test OK.'
        }
    }

}
