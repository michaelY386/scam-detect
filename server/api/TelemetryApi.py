from flask import Blueprint, Response, request
from flask_jwt_extended import jwt_required, get_jwt_identity

from api.BaseApi import authentication_required, logger
from api.ResponseWrapper import ResponseWrapper
from model.BaseModel import database_proxy
from model.telemetry.Telemetry import Telemetry
from repositories.TelemetryRepository import TelemetryRepository
from utilities.TimestampUtility import TimestampUtility

telemetry = Blueprint('telemetry', __name__, url_prefix='/api/telemetry')


"""
The Telemetry API allows client devices to upload details of events from the devices. Such as scam call, application 
installation, application crashes and debugging details.
"""

@telemetry.before_request
def before_request():
    if database_proxy.is_closed():
        database_proxy.connect()


@telemetry.after_request
def after_request(response):
    database_proxy.close()
    return response


@telemetry.route('/', methods=['POST'])
@jwt_required
@authentication_required
def create():
    identifier = get_jwt_identity()
    telemetry_parameters = request.get_json()
    telemetry_repository = TelemetryRepository()
    result = telemetry_repository.create_telemetry(
        Telemetry(
            creator=identifier,
            data_type=telemetry_parameters['data_type'],
            content=telemetry_parameters['content'],
            created=TimestampUtility.parse(telemetry_parameters['created']),
            received=TimestampUtility.now()
        )
    )
    return Response(ResponseWrapper.wrap(identifier, 'telemetry.create', result), status=200, mimetype='application/json')


@telemetry.errorhandler(500)
def server_error(e):
    logger.exception('An error occurred during a request.')
    return """
    An internal error occurred: <pre>{}</pre>
    See logs for full stacktrace.
    """.format(e), 500
