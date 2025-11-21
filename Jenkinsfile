def notifyLINE(status) {
    def jobName = env.JOB_NAME +' '+env.BRANCH_NAME
    def buildNo = env.BUILD_NUMBER
    def message = "${jobName} Build #${buildNo} ${status} \r\n tag : ${TAG} \r\n commit id : ${COMMIT_ID}"
    sh "curl ${URL_NOTIFY_LINE} -H 'Authorization: Bearer ${TOKEN_NOTIFY_LINE}' -F 'message=${message}'"
}
pipeline {
     agent any
     environment {
            DOCKER_REPOSITORY = 'ascendcorp/ptvn-sourcing-req-service'
            REPOSITORY = 'ptvn-sourcing-req-service'
            REGISTRY_URL = '192.168.19.15:8082'
            IMAGE_NAME = "${REGISTRY_URL}/${DOCKER_REPOSITORY}"
            DEV_HOSTS = '192.168.19.136,192.168.19.120,192.168.19.131'
            QA_HOSTS = '192.168.19.137,192.168.19.121,192.168.19.132'
            UAT_HOSTS = '192.168.19.165,192.168.19.166,192.168.19.167'
            TOKEN_NOTIFY_LINE = "ru6s9YKPOGV9onNc2VCqds7XPfSo89ztDzh8Qz4GV5W"
            URL_NOTIFY_LINE = 'https://notify-api.line.me/api/notify'
            TAG = sh(returnStdout: true, script: "git tag --contains | tail -1").trim()
            COMMIT_ID = sh(returnStdout: true, script: "git rev-parse HEAD").trim()
            }

    stages {
        stage("setting text"){
            steps{
                script{
                    tag = TAG.split('-')
                    profile = tag[0]
                    version = tag[1]            
                    composefile ="./dockerScript/${profile}/docker-compose.yml"   
                    IMAGE_TAG = "${profile}-${version}"
                    env.BRANCH_NAME = "${profile}"
                }
            }
        }


        stage('Clean and package Project') {
            steps {
                sh 'java -version'
                sh 'mvn clean'
                sh 'mvn versions:set -DnewVersion=1.0'
                sh 'mvn package -Dmaven.test.skip=true'
            }
        }

        stage('Build & Push Docker image') {
            steps{
                echo "Start building [Build No.:${env.BUILD_NUMBER}]"
                    script{
                        def dockerfile = "./Dockerfile"
                        docker.withRegistry("http://${REGISTRY_URL}", "nexus-push") {
                        def img = docker.build("${IMAGE_NAME}:${IMAGE_TAG}", "-f ${dockerfile} .")
                        img.push("${IMAGE_TAG}")
                    }
                }
                echo "Removing docker image ${IMAGE_NAME}:${IMAGE_TAG}"
                sh "docker rmi ${IMAGE_NAME}:${IMAGE_TAG}"
            }
        
        }
        stage('update docker-compose file') {
            steps {
                 sh "sed -i 's/youimage/${IMAGE_TAG}/g' ${composefile}"
                // sh "sed -i 's/CI_ACTIVE_PROFILE/${profile}/g' ${composefile}"
            }
        }
        
        stage("Deploy to server "){
            when {
            expression { env.BRANCH_NAME != 'prod' }
            }

            steps {
                script{

                    
                    withCredentials ([sshUserPrivateKey(credentialsId: 'ptvn-user', keyFileVariable: 'identity')]) {        
                        switch(profile) {
                        case 'dev':
                            hosts = env.DEV_HOSTS
                        break
                        case 'qa':
                            hosts = env.QA_HOSTS
                        break
                        case 'uat':
                            hosts = env.UAT_HOSTS
                        break
                        }      
                        hosts.tokenize(",").each { server ->
                            remote = [:]
                            remote.name = "${env.BRANCH_NAME}"
                            remote.host = "${server}"
                            remote.user = 'centos'
                            remote.identityFile = identity
                            remote.allowAnyHosts = true                                
                            destinate = "/home/centos/sourcing/${profile}/docker-compose.yml"
                            // composefile ="./dockerScript/docker-compose.yml"
                            if("${env.BRANCH_NAME}" == 'uat'){
                                sshCommand remote: remote, command: "pwd"
                                sshCommand remote: remote, command: "mkdir -p /home/centos/sourcing/${env.BRANCH_NAME}/"
                                sshPut remote: remote, from: composefile, into: destinate
                                sshCommand remote: remote, command: "sudo docker image prune -a -f"
                                sshCommand remote: remote, command: "sudo docker-compose -f ${destinate} down && sudo docker-compose -f ${destinate} pull  && sudo docker-compose -f ${destinate} up -d"
                                sshCommand remote: remote, command: "exit"
                            }else{
                                sshCommand remote: remote, command: "pwd"
                                sshCommand remote: remote, command: "mkdir -p /home/centos/sourcing/${env.BRANCH_NAME}/"
                                sshPut remote: remote, from: composefile, into: destinate
                                sshCommand remote: remote, command: "docker image prune -a -f"
                                sshCommand remote: remote, command: "docker-compose -f ${destinate} down && docker-compose -f ${destinate} pull  && docker-compose -f ${destinate} up -d"
                                sshCommand remote: remote, command: "exit"
                            }
                            
                        }
                    }
                }//stages
            }//matrix
        }
    }
     post{
         success{
           notifyLINE("success")
           }
         failure{
            notifyLINE("failure")
          }
     }

}
