import com.cloudbees.plugins.credentials.CredentialsScope
import com.cloudbees.plugins.credentials.domains.Domain
import com.cloudbees.jenkins.plugins.sshcredentials.impl.BasicSSHUserPrivateKey
import com.cloudbees.plugins.credentials.SystemCredentialsProvider
import com.cloudbees.plugins.credentials.Credentials

username = "deploy"
password = ""
credId = "ssh-jenkins"

credsExist = false

SystemCredentialsProvider.getInstance().getCredentials().each {
  creds = (Credentials) it
  if (creds.getId() == credId) {
    credsExist = true
  }
}

if (!credsExist) {
  provider = SystemCredentialsProvider.getInstance()
  domain = Domain.global()
  key_source = new BasicSSHUserPrivateKey.UsersPrivateKeySource()
  provider.addCredentials(
    Domain.global(),
    new BasicSSHUserPrivateKey(
      CredentialsScope.GLOBAL, credId, username, key_source, password, credId))
}
