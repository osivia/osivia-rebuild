FROM tomcat:8.0
MAINTAINER Cédric Krommenhoek <ckrommenhoek@osivia.com>

RUN apt-get update && apt-get install -y locales vim  \
	&& localedef -i fr_FR -c -f UTF-8 -A /usr/share/locale/locale.alias fr_FR.UTF-8 \
	&& apt-get purge -y \
    && rm -rf /var/lib/apt/lists/*

ENV LANG fr_FR.utf8
RUN locale >> /etc/default/locale

ENV TZ=Europe/Paris
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ENV TOMCAT_ROOT /usr/local/tomcat/webapps/

ENV CATALINA_HOME /usr/local/tomcat
ENV PATH $CATALINA_HOME/bin:$PATH

ENV PORTAL_VERSION=5.0-SNAPSHOT
ENV PORTAL_DISTRIBUTION_NAME=osivia-portal-distribution
ENV PORTAL_DISTRIBUTION ${PORTAL_DISTRIBUTION_NAME}-${PORTAL_VERSION}
ENV PORTAL_DISTRIBUTION_ZIP ${PORTAL_DISTRIBUTION_NAME}-${PORTAL_VERSION}.zip

RUN mkdir -p "$CATALINA_HOME"
WORKDIR $CATALINA_HOME

# Distribution

# Distribution
COPY ${PORTAL_DISTRIBUTION}.zip /usr/local/tomcat
RUN cd / \
    && cd /usr/local/tomcat \
    && unzip -oq ${PORTAL_DISTRIBUTION_ZIP}  \
    && rm -f ${PORTAL_DISTRIBUTION_ZIP}

# Entrypoint
COPY entrypoint.sh /
RUN chmod +x /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]

EXPOSE 8000
EXPOSE 8080
