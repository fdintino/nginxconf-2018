FROM theatlantic/ollie-centos7:1.4.8

MAINTAINER The Atlantic Programmers <programmers@theatlantic.com>
ARG BUILD_DATE
ARG VERSION
ARG VCS_REV
ARG user=jenkins
ARG group=jenkins
ARG uid=508
ARG gid=509
ARG http_port=8080
ARG agent_port=50000
ENV VCS_URL git@github.com:theatlantic/nginxconf-2018.git
ENV JENKINS_HOME /var/jenkins_home
ENV JENKINS_SLAVE_AGENT_PORT ${agent_port}

LABEL org.label-schema.name="nginxconf-2018-demo-jenkins" \
      org.label-schema.description="NGiNX Conf 2018 jenkins demo" \
      org.label-schema.vendor="The Atlantic" \
      org.label-schema.version="$VERSION" \
      org.label-schema.vcs-url="$VCS_URL" \
      org.label-schema.license="BSD-2" \
      org.label-schema.schema-version="1.2"

# Rename the deploy user to jenkins (but keep the uid and gid the same)
# Jenkins is run with user `jenkins`, uid = 508
# If you bind mount a volume from the host or a data container, 
# ensure you use the same uid
RUN groupmod -n ${group} deploy \
    && usermod -l ${user} -m -d "$JENKINS_HOME" -s /bin/bash deploy

RUN mkdir -p /www/build && chown -R jenkins:jenkins /www

RUN yum update -y --setopt=tsflags=nodocs \
    && yum install -y --setopt=tsflags=nodocs \
		sudo \
        perl \
        supervisor \
        curl \
        git \
        java-1.8.0-openjdk-devel \
        unzip \
        which \
    && yum clean all \
    && cd /usr/lib/locale \
    && localedef --list-archive | grep -v -i ^en | xargs localedef --delete-from-archive \
    && localedef -i en_US -f UTF-8 en_US.UTF-8 \
    && mv -f locale-archive locale-archive.tmpl \
    && build-locale-archive \
    && rm -rf /var/yum/cache

# `/usr/share/jenkins/ref/` contains all reference configuration we want 
# to set on a fresh new installation. Use it to bundle additional plugins 
# or config file with your custom jenkins Docker image.
RUN mkdir -p /usr/share/jenkins/ref/init.groovy.d

ENV TINI_VERSION 0.14.0
ENV TINI_SHA 6c41ec7d33e857d4779f14d9c74924cab0c7973485d2972419a3b7c7620ff5fd

# Use tini as subreaper in Docker container to adopt zombie processes 
RUN curl -fsSL https://github.com/krallin/tini/releases/download/v${TINI_VERSION}/tini-static-amd64 \
  -o /bin/tini && chmod +x /bin/tini \
  && echo "$TINI_SHA  /bin/tini" | sha256sum -c -

ARG JENKINS_VERSION
ENV JENKINS_VERSION ${JENKINS_VERSION:-2.138.2}

ARG JENKINS_SHA=d8ed5a7033be57aa9a84a5342b355ef9f2ba6cdb490db042a6d03efb23ca1e83
ARG JENKINS_URL=https://repo.jenkins-ci.org/public/org/jenkins-ci/main/jenkins-war/${JENKINS_VERSION}/jenkins-war-${JENKINS_VERSION}.war

RUN curl -fsSL ${JENKINS_URL} -o /usr/share/jenkins/jenkins.war \
  && echo "${JENKINS_SHA}  /usr/share/jenkins/jenkins.war" | sha256sum -c -

ENV JENKINS_UC https://updates.jenkins.io
ENV JENKINS_UC_EXPERIMENTAL=https://updates.jenkins.io/experimental
RUN chown -R ${user} "$JENKINS_HOME" /usr/share/jenkins/ref

# for main web interface:
EXPOSE ${http_port}

# will be used by attached slave agents:
EXPOSE ${agent_port}

ENV COPY_REFERENCE_FILE_LOG $JENKINS_HOME/copy_reference_file.log

USER ${user}

COPY sudoers-jenkins /etc/sudoers.d/jenkins

COPY jenkins-support /usr/local/bin/jenkins-support
COPY jenkins.sh /usr/local/bin/jenkins.sh
ENTRYPOINT ["/bin/tini", "--", "/usr/local/bin/jenkins.sh"]

# from a derived Dockerfile, can use `RUN install-plugins.sh github-branch-source ...`
# to setup /usr/share/jenkins/ref/plugins from a support bundle
COPY install-plugins.sh /usr/local/bin/install-plugins.sh

WORKDIR /var/jenkins_home

ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false"
 
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt

RUN mkdir -p "${JENKINS_HOME}/.ssh"

RUN chown -R jenkins:jenkins "${JENKINS_HOME}/.ssh" \
    && chmod 700 "${JENKINS_HOME}/.ssh"

COPY --chown=jenkins:jenkins jenkins_id_rsa $JENKINS_HOME/.ssh/id_rsa
COPY --chown=jenkins:jenkins jenkins_id_rsa.pub $JENKINS_HOME/.ssh/id_rsa.pub

RUN chmod 600 $JENKINS_HOME/.ssh/id_rsa

# Jenkins home directory is a volume, so configuration and build history
# can be persisted and survive image upgrades
VOLUME ["/var/jenkins_home", "/www/builds"]

LABEL org.label-schema.build-date="$BUILD_DATE" \
      org.label-schema.vcs-rev="$VCS_REV"

ENV DOCKER_CONTAINER_NAME nginxconf-demo-jenkins
