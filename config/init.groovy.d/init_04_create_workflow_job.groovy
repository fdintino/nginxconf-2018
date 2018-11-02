import hudson.model.Cause
import jenkins.branch.BranchSource
import jenkins.model.Jenkins
import jenkins.plugins.git.GitSCMSource
import jenkins.plugins.git.traits.BranchDiscoveryTrait
import jenkins.scm.impl.trait.RegexSCMHeadFilterTrait
import org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject


def createProject(remote, projectName) {
  def jenkins = Jenkins.getInstance()

  project = jenkins.createProject(WorkflowMultiBranchProject.class, projectName)
  project.getSourcesList().add(createBranchSource(remote))
  project.scheduleBuild(new Cause.UserIdCause())

  return project
}

def createBranchSource(remote) {
  def scmSource = new GitSCMSource(null, remote, '', '*', '', false)
  scmSource.traits = [
    new BranchDiscoveryTrait(),
    new RegexSCMHeadFilterTrait('(develop|beta\\/.*|PR\\-.*)'),
  ]
  return new BranchSource(scmSource)
}

def projectName = "addons-server"
def remote = '/www/repos/addons-server.git'

def jenkins = Jenkins.getInstance()

if (!jenkins.getItem(projectName)) {
  createProject(remote, projectName)

  println "****************************************************************"
  println " Blue Ocean setup complete for ${projectName}"
  println "****************************************************************"
}
