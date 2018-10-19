import hudson.plugins.sshslaves.SSHLauncher
import jenkins.model.Jenkins
import hudson.model.Node.Mode
import hudson.slaves.DumbSlave
import hudson.slaves.RetentionStrategy
import hudson.plugins.sshslaves.verifiers.NonVerifyingKeyVerificationStrategy

name = 'addons'
label = 'addons'
credsId = 'ssh-jenkins'
strategy = new NonVerifyingKeyVerificationStrategy()

def node = Jenkins.instance.getNode(name)

if (node) {
  println "build node exists, skipping"
} else {
  println "build node does not exist, creating"

  launcher = new SSHLauncher(
    host="web",
    port=22,
    credentialsId=credsId,
    jvmOptions=null,
    javaPath=null,
    prefixStartSlaveCmd=null,
    suffixStartSlaveCmd=null,
    launchTimeoutSeconds=210,
    maxNumRetries=10,
    retryWaitTime=15,
    sshHostKeyVerificationStrategy=strategy)

  node = new DumbSlave(
    name=name,
    remoteFS='/www/builds',
    launcher=launcher)
  node.setMode(Mode.NORMAL)
  node.setRetentionStrategy(new RetentionStrategy.Always())
  node.setLabelString(label)
  node.setNumExecutors(2)

  Jenkins.instance.addNode(node)
  Jenkins.instance.save()
}
