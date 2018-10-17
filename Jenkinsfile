#!groovy

node('maven') {
    

    def artServer = Artifactory.server('artifactory');
    artServer.credentialsId='GLOBAL-ARTIFACTORY';
    def rtMaven = Artifactory.newMavenBuild();
    def buildInfo = Artifactory.newBuildInfo();
    
    buildInfo.env.capture = true;
    rtMaven.tool = 'maven';
    rtMaven.resolver server: artServer, releaseRepo: 'maven-release', snapshotRepo: 'maven-release';
    rtMaven.deployer server: artServer, releaseRepo: 'app-dev-local', snapshotRepo: 'app-dev-local';
    rtMaven.deployer.deployArtifacts = false;
    
    def pom;
    def version;
    def artifactId;
    def groupId;

    properties([
            gitLabConnection('gitlab-cfets'),
            pipelineTriggers([
                    [
                            $class: 'GitLabPushTrigger',
                            branchFilterType: 'All',
                            triggerOnPush: true,
                            triggerOnMergeRequest: true,
                            triggerOpenMergeRequestOnPush: "never",
                            triggerOnNoteRequest: true,
                            noteRegex: "Jenkins please retry a build",
                            skipWorkInProgressMergeRequest: true,
                            ciSkip: false,
                            setBuildDescription: true,
                            addNoteOnMergeRequest: true,
                            addCiMessage: true,
                            addVoteOnMergeRequest: true,
                            acceptMergeRequestOnSuccess: false,
                    ]
            ])
    ])

    def needPublishArtifacts = false
    if ("$BRANCH_NAME" == "master" || "$BRANCH_NAME" == "develop" || "$BRANCH_NAME".startsWith("release-")) {
        needPublishArtifacts = true
    } else {
        needPublishArtifacts = false
    }



    stage('Check out'){
        git credentialsId: 'git-biancl', url: 'http://200.31.147.77/devops/ansible-maven-sample.git'
        pom = readMavenPom file: 'pom.xml'
        version = pom.version;
        artifactId = pom.artifactId;
        groupId = pom.groupId;
        
        echo "verison=${version}"
        echo "artifactId=$artifactId"
        echo "groupId=$groupId"
    }
    
    
    // stage('Unit Test') {
        
    //     rtMaven.run pom: 'pom.xml', goals: 'clean test ', buildInfo: buildInfo;
        
    // }
    
    stage('SonarQube Scan') {
        rtMaven.run pom:'pom.xml', goals: '-Dsonar.host.url=$SONAR_HOST_URL package',buildInfo: buildInfo;
        sh 'cd ./ansible-maven-sample/target && md5sum *.tar.gz *.war > ansible-maven-sample.md5'
    }

    stage ('Publish artifacts') {
                gitlabCommitStatus("Publish artifacts") {
                    buildInfo.env.capture = true
                    buildInfo.env.filter.addExclude("*PASS*")
                    buildInfo.env.filter.addExclude("*pass*")
                    buildInfo.env.collect()
                    rtMaven.deployer.artifactDeploymentPatterns.addInclude("*.war").addInclude("*.tar.gz").addInclude("*md5*");
                    rtMaven.deployer.deployArtifacts buildInfo
                    artServer.publishBuildInfo buildInfo
                }
            }
 

}
