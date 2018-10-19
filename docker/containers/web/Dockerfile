FROM theatlantic/ollie-centos7:1.4.8

MAINTAINER The Atlantic <programmers@theatlantic.com>
ARG BUILD_DATE
ARG VERSION
ARG VCS_REV
ENV VCS_URL git@github.com:theatlantic/nginxconf-2018.git

ENV DOCKER_HOST unix:///tmp/docker.sock
ENV DNS_IP 127.0.0.1
ENV HOSTMACHINE_IP 127.0.0.1

LABEL org.label-schema.name="nginxconf-2018-demo-web" \
      org.label-schema.description="NGiNX Conf 2018 demo" \
      org.label-schema.vendor="The Atlantic" \
      org.label-schema.version="$VERSION" \
      org.label-schema.vcs-url="$VCS_URL" \
      org.label-schema.license="BSD-2" \
      org.label-schema.schema-version="1.2"

ADD passenger.repo /etc/yum.repos.d/passenger.repo

RUN yum update -y --setopt=tsflags=nodocs \
 && yum install -y --setopt=tsflags=nodocs \
     http://repo.mysql.com/mysql57-community-release-el7-9.noarch.rpm \
 && yum clean all

RUN yum update -y --setopt=tsflags=nodocs \
 && yum -y install --setopt=tsflags=nodocs \
     nginx \
 && yum clean all \
 && mkdir -p /usr/share/nginx/html

RUN echo "nginx on CentOS7" > /usr/share/nginx/html/index.html

ENV SWIG_FEATURES="-D__x86_64__"

RUN yum update -y --setopt=tsflags=nodocs \
    && yum install -y --setopt=tsflags=nodocs \
        # nginx packages
        supervisor dnsmasq \
        nginx-filesystem \
        nginx-mod-passenger passenger \
        # misc utilities
		sudo \
        perl \
        supervisor \
        gcc-c++ \
        curl \
        make \
        libjpeg-devel \
        libxml2-devel \
        libxslt-devel \
        zlib-devel \
        libffi-devel \
        openssl-devel \
        sqlite-devel \
        readline-devel \
        glib2-devel \
        cmake \
        bison \
        byacc \
        # python base packages
        python-devel \
        python2-pip \
        python2-setuptools \
        python2-virtualenv \
        python2-wheel \
        git \
        nodejs-devel \
        npm \
        # Dependencies for mysql-python
        mysql-community-devel \
        mysql-community-client \
        mysql-community-libs \
        swig \
        gettext \
        # Use rsvg-convert to render our static theme previews
        librsvg2-tools \
        # pngcrush to optimize the PNGs uploaded by developers
        pngcrush \
        # makefile and ui-tests require uuid to be installed
        uuid \
        # uWSGI
        python-uwsgidecorators \
        uwsgi-devel \
        uwsgi-plugin-python \
        uwsgi-plugin-zergpool \
        uwsgi-router-http \
        uwsgi-router-raw \
        uwsgi-router-uwsgi \
        openssh-server \
        java-1.8.0-openjdk \
        libgit2-devel \
    && yum clean all \
    && cd /usr/lib/locale \
    && localedef --list-archive | grep -v -i ^en | xargs localedef --delete-from-archive \
    && localedef -i en_US -f UTF-8 en_US.UTF-8 \
    && mv -f locale-archive locale-archive.tmpl \
    && build-locale-archive

EXPOSE 80 22 443 19321

ADD supervisord.conf /etc/supervisord.conf

RUN mkdir /www \
    && chown -R deploy:deploy /www \
    && mkdir -p /var/run/uwsgi \
    && chown deploy:deploy /var/run/uwsgi \
    && mkdir -p /var/log/uwsgi \
    && chown deploy:deploy /var/log/uwsgi \
	&& mkdir -p /mnt/vols \
	&& chown deploy:deploy /mnt/vols \
    && mkdir /code \
    && chown -R deploy:deploy /code

WORKDIR /www

RUN ln -fs /usr/share/zoneinfo/America/New_York /etc/localtime

ADD container-entrypoint /usr/bin/container-entrypoint
ADD sudoers-deploy /etc/sudoers.d/deploy
ADD nginx/nginx.conf /etc/nginx/nginx.conf

RUN mkdir -p /home/deploy/.ssh
ADD deploy_authorized_keys /home/deploy/.ssh/authorized_keys
RUN chown -R deploy:deploy /home/deploy/.ssh \
    && chmod 600 /home/deploy/.ssh/authorized_keys \
    && chmod 700 /home/deploy/.ssh

RUN ssh-keygen -A

ENV RABBITMQADMIN_URL "https://raw.githubusercontent.com/rabbitmq"\
"/rabbitmq-management/rabbitmq_v3_6_16/bin/rabbitmqadmin"

RUN cd /usr/bin && curl -O $RABBITMQADMIN_URL && chmod 755 /usr/bin/rabbitmqadmin

VOLUME ["/www", "/code", "/var/yum/cache", "/var/tmp", "/mnt/vols", "/var/log/uwsgi"]

ENTRYPOINT ["container-entrypoint"]
CMD ["supervisord", "--nodaemon", "-c", "/etc/supervisord.conf"]

LABEL org.label-schema.build-date="$BUILD_DATE" \
      org.label-schema.vcs-rev="$VCS_REV"

ENV DOCKER_CONTAINER_NAME nginxconf-demo-web
