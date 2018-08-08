from flask import Blueprint, Response, g, request
from flask_jwt_extended import jwt_required

from api.BaseApi import authentication_required, logger
from api.ResponseWrapper import ResponseWrapper
from model.BaseModel import database_proxy
from model.telemetry.Source import Source
from repositories.SourceRespository import SourceRepository
from utilities.TimestampUtility import TimestampUtility

sources = Blueprint('sources', __name__, url_prefix='/api/sources')


"""
The source API allows the server to maintain the blacklist of phone numbers. It is intended to be expandable for future
communication sources (email addresses, social media accounts, etc.) blacklists.
"""


@sources.before_request
def before_request():
    if database_proxy.is_closed():
        database_proxy.connect()


@sources.after_request
def after_request(response):
    database_proxy.close()
    return response


@sources.route('/', methods=['POST'])
@jwt_required
@authentication_required
def create():
    identifier = g.get('identifier', None)
    source_parameters = request.get_json()
    source_repository = SourceRepository()
    existing = source_repository.retrieve_source_by_value(source_parameters['value'])
    if len(existing) > 0:
        current = existing[0]
        updated = source_repository.update(Source(
            id=current.id,
            value=current.value,
            source_type=current.source_type,
            verifications=current.verifications + 1,
            created=current.created
        ))
        result = Response(ResponseWrapper.wrap(identifier, 'sources.create', updated),
                          status=200,
                          mimetype='application/json')
        return result
    else:
        created = source_repository.create_source(Source(
            value=source_parameters['value'],
            source_type=source_parameters['source_type'],
            verifications=1,
            created=TimestampUtility.now()
        ))
        result = Response(ResponseWrapper.wrap(identifier, 'sources.create', created),
                          status=200,
                          mimetype='application/json')
        return result


@sources.route('/', methods=['GET'])
@jwt_required
@authentication_required
def retrieve():
    identifier = g.get('identifier', None)
    source_repository = SourceRepository()
    results = source_repository.retrieve_sources_by_source_type('blacklist')
    result = Response(ResponseWrapper.wrap(identifier, 'sources.retrieve', results),
                      status=200,
                      mimetype='application/json')
    return result


@sources.errorhandler(500)
def server_error(e):
    logger.exception('An error occurred during a request.')
    return """
    An internal error occurred: <pre>{}</pre>
    See logs for full stacktrace.
    """.format(e), 500
