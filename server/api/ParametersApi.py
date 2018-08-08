import json

from flask import Blueprint, Response, g, request
from flask_jwt_extended import jwt_required

from api.BaseApi import authentication_required, logger
from api.ResponseWrapper import ResponseWrapper
from model.BaseModel import database_proxy
from model.telemetry.Parameters import Parameters
from repositories.ParametersRepository import ParametersRepository
from repositories.TelemetryRepository import TelemetryRepository
from utilities.TimestampUtility import TimestampUtility

parameters = Blueprint('parameters', __name__, url_prefix='/api/parameters')

PARAMETERS_KEY = 'p4ZfOJxhXJOU5VE9mdPX8Mo5V8dveda1bCUQaQ4QzHo06nrklJxRvdNpUZSE4WnG'

"""
The Parameters API allows the client devices to retrieve classifier parameters and the classification training process
to update the parameters stored in the database. The classification training process can also retrieve the resulting 
classifications reported by clients.
"""


@parameters.before_request
def before_request():
    if database_proxy.is_closed():
        database_proxy.connect()


@parameters.after_request
def after_request(response):
    database_proxy.close()
    return response


@parameters.route('/', methods=['POST'])
def create():
    message_parameters = request.get_json()
    if message_parameters['key'] == PARAMETERS_KEY:
        parameters_repository = ParametersRepository()
        parameters_repository.create_parameters(Parameters(
            content=json.dumps(message_parameters['content']),
            created=TimestampUtility.now()
        ))
        result = Response(json.dumps({'message': 'classifier parameters updated'}),
                          status=200,
                          mimetype='application/json')
        return result
    else:
        result = Response(json.dumps({'message': 'unknown'}),
                          status=401,
                          mimetype='application/json')
        return result


@parameters.route('/results', methods=['POST'])
def results():
    message_parameters = request.get_json()
    if message_parameters['key'] == PARAMETERS_KEY:
        telemetry_repository = TelemetryRepository()
        result_set = telemetry_repository.retrieve_telemetry_by_data_type('call')
        result = Response(ResponseWrapper.wrap("system", 'parameters.results', result_set),
                          status=200,
                          mimetype='application/json')
        return result
    else:
        result = Response(json.dumps({'message': 'unknown'}),
                          status=401,
                          mimetype='application/json')
        return result


@parameters.route('/', methods=['GET'])
@jwt_required
@authentication_required
def retrieve():
    identifier = g.get('identifier', None)
    parameters_repository = ParametersRepository()
    parameter_set = parameters_repository.retrieve_parameter()
    result = Response(ResponseWrapper.wrap(identifier, 'parameters.retrieve', parameter_set),
                      status=200,
                      mimetype='application/json')
    return result


@parameters.errorhandler(500)
def server_error(e):
    logger.exception('An error occurred during a request.')
    return """
    An internal error occurred: <pre>{}</pre>
    See logs for full stacktrace.
    """.format(e), 500
