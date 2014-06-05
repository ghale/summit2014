package summit2014

import com.terrafolio.gradle.plugins.jenkins.dsl.JenkinsJob
import com.terrafolio.gradle.plugins.jenkins.dsl.JenkinsConfiguration

public class CommonTemplates {
    static void defaultBuildServer(JenkinsConfiguration jenkins) {
        jenkins.with {
            servers {
                demo {
                    url 'http://localhost:8080'
                    secure false
                }
            }
            defaultServer servers.demo
        }	
    }

    static void repo(JenkinsJob job, String repoUrl, String branch) {
        job.with {
            dsl {
                scm {
                    git(repoUrl, branch) {
                        createTag(true)
                    }
                }
                publishers {
                    git { 
                        tag('origin', 'build_${BUILD_NUMBER}') {
                            create(true)
                            update(true)
                        }
                    }
                }
            }
        }
    }

    static void scmTrigger(JenkinsJob job, Integer mins) {
        job.with {
            dsl {
                triggers {
                    scm("H/${mins} * * * 1-5")
                }
            }
        }
    }

    static void gradleBuild(JenkinsJob job, String task) {
        job.with {
            dsl {
                steps {
                    gradle(task, '-I some/common/initscript')
                }
            }
        } 
    }

    static void gradleJob(JenkinsConfiguration jenkins, Map options) {
        def job = jenkins.jobs.create(options.name)
        repo(job, options.url, options.branch)
        scmTrigger(job, options.poll)
        gradleBuild(job, options.tasks)
    }
}
