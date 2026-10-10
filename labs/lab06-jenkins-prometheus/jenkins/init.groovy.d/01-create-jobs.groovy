import jenkins.model.Jenkins
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition

/**
 * Cria os três jobs Pipeline do lab (Core / AI / Gateway) a partir dos Jenkinsfiles montados.
 * Cada job é autônomo — falha em um não promove o artefato dos outros (ASR-11).
 */
def jenkins = Jenkins.instance
def base = '/workspace/labs/lab06-jenkins-prometheus'

def jobs = [
    'pipeline-core'   : "${base}/Jenkinsfile",
    'pipeline-ai'     : "${base}/Jenkinsfile.ai",
    'pipeline-gateway': "${base}/Jenkinsfile.gateway",
]

jobs.each { name, path ->
    def script = new File(path).getText('UTF-8')
    def existing = jenkins.getItem(name)
    if (existing != null) {
        existing.delete()
    }
    def job = jenkins.createProject(WorkflowJob, name)
    job.setDefinition(new CpsFlowDefinition(script, true))
    job.save()
    println "Job criado: ${name} ← ${path}"
}

jenkins.save()
