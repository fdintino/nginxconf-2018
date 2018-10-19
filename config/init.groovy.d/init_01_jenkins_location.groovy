#!groovy
 
import jenkins.model.*
import hudson.security.*
import jenkins.security.s2m.AdminWhitelistRule
 
def jlc = JenkinsLocationConfiguration.get()
jlc.setUrl("http://jenkins.nginxdemo:8001/")
jlc.save()
