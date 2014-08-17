#!/usr/bin/python
# vim:ts=4:sts=4:sw=4:et:wrap:ai:fileencoding=utf-8:

import codecs
import elasticsearch
from elasticsearch.helpers import bulk_index
import json
import sys
import xmltodict


INDEX = "senadores"
TYPE = "despesa"


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

    bulk = []
    for line in despesas:
        body = json.loads(line)

        bulk.append({"_index": INDEX, "_type": TYPE, "_source": body})

        if len(bulk) % 1000 == 0:
            bulk_index(es, bulk)
            bulk = []
    if bulk:
        bulk_index(es, bulk)


insert_data(sys.stdin)

# TODO: Criar um mapping apropriado.
# TODO: Transformar campos de string para double/date e afins
