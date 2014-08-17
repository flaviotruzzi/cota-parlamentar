FROM ubuntu:14.04

WORKDIR /tmp

ENV DEBIAN_FRONTEND noninteractive
RUN apt-get update 
RUN apt-get install --no-install-recommends -y curl openjdk-7-jre-headless python-pip 
RUN apt-get install --no-install-recommends -y python-dev python-simplejson python-requests 
RUN apt-get install --no-install-recommends -y python-boto python-flask python-mock 
RUN apt-get install --no-install-recommends -y python-openssl nginx python-nose nodejs 
RUN apt-get install --no-install-recommends -y nodejs-legacy memcached python-pylibmc 
RUN apt-get install --no-install-recommends -y python-statsd gcc make libssl-dev git libxml2-dev
RUN apt-get install --no-install-recommends -y libxslt-dev python-lxml vim nano tidy

RUN pip install xmltodict

WORKDIR /home/

RUN git clone http://github.com/flaviotruzzi/cota-parlamentar.git