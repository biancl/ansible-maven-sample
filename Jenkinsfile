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

    // input message: '请输入/选择构建参数', parameters: [
    //     credentials(credentialType: 'com.cloudbees.plugins.credentials.common.StandardCredentials', defaultValue: 'git', description: '源码仓库认证', name: 'REPOSITORY_CREDENTIAL_ID', required: true), 
    //     string(defaultValue: 'http://200.31.147.77/devops/ansible-maven-sample.git', description: '代码仓库地址', name: 'REPOSITORY_URL'), 
    //     string(defaultValue: 'cfets-gitlab', description: 'gitlab连接，需在系统设置中配置', name: 'GITLAB_CONNECTION'), 
    //     string(defaultValue: 'cfets-sonar', description: 'sonar服务器连接，需在系统设置中配置', name: 'SONAR_SERVER')];
properties([gitLabConnection('gitlab-cfets'),
 [$class: 'GitlabLogoProperty', repositoryName: ''],
  [$class: 'RebuildSettings', autoRebuild: false, rebuildDisabled: false], 
  pipelineTriggers([<object of type com.dabsquared.gitlabjenkins.GitLabPushTrigger>])])
    properties([
        gitLabConnection('$GITLAB_CONNECTION'), 
        [$class: 'GitlabLogoProperty', repositoryName: ''], 
        [$class: 'RebuildSettings', autoRebuild: false, rebuildDisabled: false], 
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
              withSonarQubeEnv('$SONAR_SERVER') {
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
