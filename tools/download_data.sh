#!/usr/bin/env bash
# Download data
wget http://www.camara.gov.br/cotas/AnoAtual.zip
#wget http://www.camara.gov.br/cotas/AnoAnterior.zip

# unzip
unzip AnoAtual.zip
#unzip AnoAnterior.zip

# parse
#cat AnoAnterior.xml | tidy -utf8 -xml -w 255 -i -c -q -asxml > ano_anterior.xml
cat AnoAtual.xml | tidy -utf8 -xml -w 255 -i -c -q -asxml > ano_atual.xml
#cat ano_anterior.xml ano_atual.xml | python2 parse_and_import.py
cat ano_atual.xml | python2 parse_and_import.py

# clean it
rm AnoAtual.zip
#rm AnoAnterior.zip
rm AnoAtual.xml
#rm AnoAnterior.xml
#rm ano_anterior.xml
rm ano_atual.xml
