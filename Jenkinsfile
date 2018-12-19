#!groovy

node('maven') {
    

    def artServer = Artifactory.server('artifactory');
    artServer.credentialsId='GLOBAL-ARTIFACTORY';
    def rtMaven = Artifactory.newMavenBuild();
    rtMaven.tool = 'maven';
    rtMaven.resolver server: artServer, releaseRepo: 'maven-release', snapshotRepo: 'maven-release';
    rtMaven.deployer.deployArtifacts = false;


    // properties([
    //         gitLabConnection('gitlab-cfets'),
    //         pipelineTriggers([
    //                 [
    //                         $class: 'GitLabPushTrigger',
    //                         branchFilterType: 'All',
    //                         triggerOnPush: true,
    //                         triggerOnMergeRequest: true,
    //                         triggerOpenMergeRequestOnPush: "never",
    //                         triggerOnNoteRequest: true,
    //                         noteRegex: "Jenkins please retry a build",
    //                         skipWorkInProgressMergeRequest: true,
    //                         ciSkip: false,
    //                         setBuildDescription: true,
    //                         addNoteOnMergeRequest: true,
    //                         addCiMessage: true,
    //                         addVoteOnMergeRequest: true,
    //                         acceptMergeRequestOnSuccess: false,
    //                 ]
    //         ])
    // ])

    properties([
        gitLabConnection('gitlab-cfets'), 
        [$class: 'GitlabLogoProperty', repositoryName: ''], 
        [$class: 'RebuildSettings', autoRebuild: false, rebuildDisabled: false], 
        parameters([
            string(defaultValue: '', description: '代码仓库地址', name: 'REPOSITORY_URL', defaultValue: 'http://200.31.147.77/devops/ansible-maven-sample.git'), 
            credentials(credentialType: 'com.cloudbees.plugins.credentials.common.StandardCredentials', defaultValue: '', description: '源码仓库认证', name: 'REPOSITORY_CREDENTIAL_ID', required: false)]), 
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
            ])])

    // stage('pre build'){
    //     deleteDir();
    // }

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
              withSonarQubeEnv('cfets-sonar') {
                 rtMaven.run pom:'pom.xml', goals: 'clean org.jacoco:jacoco-maven-plugin:prepare-agent  compile  sonar:sonar'
      }
        }
    }

    stage('Unit Test') {
        gitlabCommitStatus("Test") {
        rtMaven.run pom: 'pom.xml', goals: 'clean test '
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
