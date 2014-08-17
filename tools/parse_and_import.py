#!/usr/bin/python
# vim:ts=4:sts=4:sw=4:et:wrap:ai:fileencoding=utf-8:

import codecs
import elasticsearch
from elasticsearch.helpers import bulk_index
import json
import sys
import xmltodict


INDEX = "deputados"
TYPE = "despesa"

MAPPING_DESPESA = {
    "mappings": {
        "despesa": {
            "properties": {
                "vlrLiquido": {"type": "double"},
                "vlrGlosa": {"type": "double"},
                "vlrDocumento": {"type": "double"},
                "txtTrecho": {"type": "string"},
                "txtPassageiro": {"type": "string"},
                "txtNumero": {"type": "string"},
                "txtDescricaoEspecificacao": {"type": "string"},
                "txtDescricao": {"type": "string"},
                "txtCNPJCPF": {"type": "string"},
                "txtBeneficiario": {"type": "string"},
                "numEspecificacaoSubCota": {"type": "integer"},
                "numAno": {"type": "date", "format": "year"},
                "nuLegislatura": {"type": "string"},
                "nuCarteiraParlamentar": {"type": "integer"},
                "indTipoDocumento": {"type": "integer"},
                "ideCadastro": {"type": "string"},
                "datEmissao": {"format": "dateOptionalTime", "type": "date"},
                "codLegislatura": {"type": "integer"},
                "numLote": {"type": "integer"},
                "numMes": {"type": "integer"},
                "numParcela": {"type": "integer"},
                "numRessarcimento": {"type": "integer"},
                "numSubCota": {"type": "integer"},
                "sgPartido": {"type": "string"},
                "sgUF": {"type": "string"},
                "txNomeParlamentar": {"type": "string"}
            }
        }
    }
}


def dump_despesa(stack):
    """
    Just dump the json.

    :type stack: list
    """
    try:
        return json.dumps(xmltodict.parse("".join(stack))["DESPESA"])
    except:
        pass


def get_despesas(file_handler):
    """
    The one line xml is terrible. This works on the pretty xml.

    """
    stack = []
    flag = False
    for line in file_handler:
        if u'<DESPESA>' in line:
            flag = True
        elif u'</DESPESA>' in line:
            flag = False
            stack.append(line)
            yield dump_despesa(stack)
            stack = []
        if flag:
            stack.append(line)


def insert_data(file_handler):
    es = elasticsearch.Elasticsearch()

    despesas = get_despesas(codecs.getreader('utf-8')(file_handler))

    # delete index before re-importing
    if es.indices.exists(INDEX):
        es.indices.delete(INDEX)

    es.indices.create(index=INDEX, body=MAPPING_DESPESA)

    bulk = []
    for line in despesas:
        body = json.loads(line)

        bulk.append({"_index": INDEX, "_type": TYPE, "_source": body})

        if len(bulk) % 2000 == 0:
            bulk_index(es, bulk)
            bulk = []
    if bulk:
        bulk_index(es, bulk)


insert_data(sys.stdin)
