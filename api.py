#!/usr/bin/python
# vim:ts=4:sts=4:sw=4:et:wrap:ai:fileencoding=utf-8:

from flask import Flask
from tools import INDEX, TYPE
import elasticsearch
import json


app = Flask(__name__)

es = elasticsearch.Elasticsearch()


@app.route("/parties", methods=["GET"])
def get_parties(with_counts=None):
    query = {
        "query": {"match_all": {}},
        "aggs": {"parties": {"terms": {"field": "sgPartido"}}}
    }
    result = es.search(index=INDEX, doc_type=TYPE, body=query)
    result = result["aggregations"]["parties"]["buckets"]

    if with_counts:
        return json.dumps(result)
    else:
        return json.dumps([item["key"] for item in result])


# FIXME: names must be FULL to avoid problems.
@app.route("/gasto/name/<name>", methods=["GET"])
def get_spending_by_name(name):
    query = {
        "query": {
            "match": {"txNomeParlamentar": name}
        },
        "aggs": {
            "spending": {"sum": {"field": "vlrLiquido"}}
        }
    }
    result = es.search(index=INDEX, doc_type=TYPE, body=query)

    return json.dumps(result["aggregations"]["spending"])


@app.route("/gasto/party/<name>", methods=["GET"])
def get_spending_by_party(party):
    query = {
        "query": {
            "match": {"sgPartido": party}
        },
        "aggs": {
            "spending": {"sum": {"field": "vlrLiquido"}}
        }
    }
    result = es.search(index=INDEX, doc_type=TYPE, body=query)

    return json.dumps(result["aggregations"]["spending"])


@app.route("/gasto/party", methods=["GET"])
def get_spending_rank():
    # TODO
    pass


if __name__ == "__main__":
    app.run()
