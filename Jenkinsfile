#!groovy

node('maven') {

    def REPOSITORY_URL='http://200.31.147.77/devops/ansible-maven-sample.git';
    def REPOSITORY_CREDENTIAL_ID='git';
    def GITLAB_CONNECTION='cfets-gitlab';
    def SONAR_SERVER='cfets-sonar';

    def artServer = Artifactory.server('artifactory');
    artServer.credentialsId='GLOBAL-ARTIFACTORY';
    def rtMaven = Artifactory.newMavenBuild();
    rtMaven.tool = 'maven';
    rtMaven.resolver server: artServer, releaseRepo: 'maven-release', snapshotRepo: 'maven-release';
    rtMaven.deployer.deployArtifacts = false;

    properties([
        gitLabConnection("$GITLAB_CONNECTION"), 
        [$class: 'GitlabLogoProperty', repositoryName: ''], 
        [$class: 'RebuildSettings', autoRebuild: false, rebuildDisabled: false], 
        pipelineTriggers([[
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
        ]])
    ])


    gitlabBuilds(builds: ['Checkout', 'Scan', 'Test', 'Quality Gate']){

        stage('Check out'){
            gitlabCommitStatus("Checkout") {
                echo "branchName=$BRANCH_NAME"
                echo "REPOSITORY_CREDENTIAL_ID=$REPOSITORY_CREDENTIAL_ID"
                git branch: "$BRANCH_NAME", credentialsId: "$REPOSITORY_CREDENTIAL_ID", url: "$REPOSITORY_URL"
            }
        }
        
        stage("SonarQube scan") {
            gitlabCommitStatus("Scan") {
                withSonarQubeEnv("$SONAR_SERVER") {
                    rtMaven.run pom:'pom.xml', goals: '-Dmaven.test.skip=true  compile  sonar:sonar'
                }
            }
        }

        stage('Unit Test') {
            gitlabCommitStatus("Test") {
                echo "=============================unit test start================================================="
                    rtMaven.run pom:'pom.xml', goals: 'org.jacoco:jacoco-maven-plugin:prepare-agent  test'
                    // jacoco()     //需安装jacoco插件
                    echo "=============================unit test end================================================="
                }
        }
    }
}

stage("Quality Gate"){
    gitlabCommitStatus("Quality Gate") {
        def qg = waitForQualityGate()
        if (qg.status != 'OK') {
            error "Pipeline aborted due to quality gate failure: ${qg.status}"
        }
    }     
} 
