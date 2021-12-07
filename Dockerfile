FROM jenkins/jenkins:lts-alpine

USER root
RUN apk add \
 docker \
 shadow

ENV TZ=America/Mexico_City
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
 
ADD jenkins /home/jenkins

USER jenkins

COPY user /tmp/user
COPY pass /tmp/pass
COPY security.groovy /var/jenkins_home/init.groovy.d/security.groovy
COPY jenkins.yaml /var/jenkins_home/jenkins.yaml
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt

ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false"

USER root
RUN usermod -aG docker jenkins
RUN chown jenkins:jenkins /home/jenkins -R
