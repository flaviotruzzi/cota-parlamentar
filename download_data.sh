# Download data
wget http://www.camara.gov.br/cotas/AnoAtual.zip
wget http://www.camara.gov.br/cotas/AnoAnterior.zip

# unzip
unzip AnoAtual.zip 
unzip AnoAnterior.zip

# parse
cat AnoAtual.xml | tidy -utf8 -xml -w 255 -i -c -q -asxml | python parse.py > ano_atual.json 
cat AnoAnterior.xml | tidy -utf8 -xml -w 255 -i -c -q -asxml | python parse.py > ano_anterior.json

# clean it
rm AnoAtual.zip
rm AnoAnterior.zip
rm AnoAtual.xml
rm AnoAnterior.xml