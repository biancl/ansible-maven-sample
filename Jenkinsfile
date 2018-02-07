#!groovy

node ('master') {

    def artServer = Artifactory.server('old');
     artServer.credentialsId='chenyuqiang-artifactory-old';
    def buildInfo = Artifactory.newBuildInfo();
    buildInfo.env.capture = true
    stage 'Build'
    def rtMaven = Artifactory.newMavenBuild()
    rtMaven.resolver server: artServer, releaseRepo: 'libs-releases', snapshotRepo: 'libs-snapshot'
    rtMaven.deployer server: artServer, releaseRepo: 'libs-releases-local', snapshotRepo: 'libs-releases-local'
    rtMaven.tool = 'maven'
    rtMaven.run pom: 'pom.xml', goals: 'clean install', buildInfo: buildInfo


    node ('master') {
        properties([
            parameters([
                stringParam(
                    description: 'Select a environment to deploy',
                    name: 'deploy_env'
                ),
                stringParam(
                    description: 'Select a version to deploy',
                    name: 'deploy_version'
                )
            ])
        ])

        environment {
            database_password = credentialsId('devops-database-password-jeffery-' + ${params.deploy_env});
        }

        stage('fetch artifacts from artifactory')
        artServer.download(null, buildInfo)
        sh "tar xzvf ansible-hello-world-0.0.2-deploy.tar.gz"

        stage('deploy')
        dir('ansible-hello-world-0.0.2-deploy/deploy') {
            echo 'Deploy to ' + ${params.deploy_env}
            // some block
            ansiblePlaybook extras: 'database_password=' + ${env.database_password}, installation: 'ansible', inventory: 'inventories/stage/hosts', playbook: 'proxy_server.yml'


        }

    }
}
