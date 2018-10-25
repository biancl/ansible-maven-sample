#!groovy

node('maven') {
    

    def artServer = Artifactory.server('artifactory');
    artServer.credentialsId='GLOBAL-ARTIFACTORY';
    def rtMaven = Artifactory.newMavenBuild();
    rtMaven.tool = 'maven';
    rtMaven.resolver server: artServer, releaseRepo: 'maven-release', snapshotRepo: 'maven-release';
    rtMaven.deployer.deployArtifacts = false;


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

    // stage('pre build'){
    //     deleteDir();
    // }

    gitlabBuilds(builds: ['Checkout', 'Scan', 'Test', 'Quality Gate']){

    stage('Check out'){
        gitlabCommitStatus("Checkout") {
        echo "branchName=$BRANCH_NAME"
        git branch: "$BRANCH_NAME", credentialsId: 'git-biancl', url: 'http://200.31.147.77/devops/ansible-maven-sample.git'
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
          timeout(time: 1, unit: 'HOURS') {
              def qg = waitForQualityGate()
              if (qg.status != 'OK') {
                  error "Pipeline aborted due to quality gate failure: ${qg.status}"
              }
          }
      }     
    } 
