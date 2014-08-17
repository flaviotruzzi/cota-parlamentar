wget http://www.camara.gov.br/cotas/AnoAtual.zip
wget http://www.camara.gov.br/cotas/AnoAnterior.zip

unzip AnoAtual.zip 
unzip AnoAnterior.zip

cat AnoAtual.xml | tidy -utf8 -xml -w 255 -i -c -q -asxml | python parse.py > ano_atual.json 
cat AnoAnterior.xml | tidy -utf8 -xml -w 255 -i -c -q -asxml | python parse.py > ano_anterior.json

