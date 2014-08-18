FROM ubuntu:14.04

WORKDIR /tmp

ENV DEBIAN_FRONTEND noninteractive
RUN apt-get update 
RUN apt-get install --no-install-recommends -y curl openjdk-7-jre-headless python-pip 
RUN apt-get install --no-install-recommends -y python-dev python-simplejson python-requests 
RUN apt-get install --no-install-recommends -y python-boto python-flask python-mock 
RUN apt-get install --no-install-recommends -y python-openssl nodejs nodejs-legacy
RUN apt-get install --no-install-recommends -y python-pylibmc git libxml2-dev
RUN apt-get install --no-install-recommends -y gcc make libssl-dev libxslt-dev 
RUN apt-get install --no-install-recommends -y python-lxml vim nano tidy

# Elasticsearch
wget https://download.elasticsearch.org/elasticsearch/elasticsearch/elasticsearch-1.3.2.tar.gz
tar -xf elasticsearch-1.3.2.tar.gz

WORKDIR /home/

RUN git clone http://github.com/flaviotruzzi/cota-parlamentar.git

WORKDIR /home/cota-parlamentar

RUN pip install -r requirements.txt

