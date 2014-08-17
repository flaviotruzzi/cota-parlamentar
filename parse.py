#!/usr/bin/python
# vim:ts=4:sts=4:sw=4:et:wrap:ai:fileencoding=utf-8:
import codecs
import json
import sys
import xmltodict


def dump_despesa(stack):
    """
    Just dump the json.

    :type stack: list
    """
    try:
        print json.dumps(xmltodict.parse("".join(stack)))
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
            dump_despesa(stack)
            stack = []
        if flag:
            stack.append(line)

get_despesas(codecs.getreader('utf-8')(sys.stdin))
