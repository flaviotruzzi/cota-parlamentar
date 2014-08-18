#!/usr/bin/python
# vim:ts=4:sts=4:sw=4:et:wrap:ai:fileencoding=utf-8:

from flask import Flask
from tools import INDEX, TYPE, lru_cache
import elasticsearch
import json


app = Flask(__name__)

es = elasticsearch.Elasticsearch()


@app.route("/parties", methods=["GET"])
@lru_cache(maxsize=1)
def get_parties(with_counts=None):
    query = {
        "aggs": {"parties": {"terms": {"field": "sgPartido"}}}
    }
    result = es.search(index=INDEX, doc_type=TYPE, body=query)
    result = result["aggregations"]["parties"]["buckets"]

    if with_counts:
        return json.dumps(result)
    else:
        return json.dumps([item["key"] for item in result])


@app.route("/deputados", methods=["GET"])
@lru_cache(maxsize=1)
def get_congressmans(with_counts=None):
    query = {
        "aggs": {
            "parties": {
                "terms": {
                    "field": "txNomeParlamentar",
                    "size": 0
                }
            }
        }
    }
    result = es.search(index=INDEX, doc_type=TYPE, body=query)
    result = result["aggregations"]["parties"]["buckets"]

    if with_counts:
        return json.dumps(result)
    else:
        return json.dumps([item["key"] for item in result])


# FIXME: names must be FULL to avoid problems.
@app.route("/gasto/deputados/<name>", methods=["GET"])
@lru_cache(maxsize=200)
def get_spending_by_name(name):
    query = {
        "query": {
            "match": {"txNomeParlamentar": name}
        },
        "aggregations": {
            "spending": {"sum": {"field": "vlrLiquido"}}
        }
    }
    result = es.search(index=INDEX, doc_type=TYPE, body=query)

    return json.dumps(result["aggregations"]["spending"])


@app.route("/gasto/party/<party>", methods=["GET"])
@lru_cache(maxsize=20)
def get_spending_by_party(party):
    query = {
        "query": {
            "match": {"sgPartido": party}
        },
        "aggregations": {
            "spending": {"sum": {"field": "vlrLiquido"}}
        }
    }
    result = es.search(index=INDEX, doc_type=TYPE, body=query)

    return json.dumps(result["aggregations"]["spending"])


@app.route("/gasto/party", methods=["GET"])
@lru_cache(maxsize=1)
def get_spending_rank_by_party():
    parties = get_parties()
    return json.dumps({party: get_spending_by_party(party) for
                       party in json.loads(parties)})


@app.route("/gasto/deputados", methods=["GET"])
@lru_cache(maxsize=1)
def get_spending_rank_by_congressman():
    congressmans = get_congressmans()
    return json.dumps({congressman: get_spending_by_name(congressman) for
                       congressman in json.loads(congressmans)})

# if __name__ == "__main__":
#     app.run(debug=True)
