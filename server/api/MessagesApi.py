from flask import Blueprint, logging, Response, g, request
from flask_jwt_extended import jwt_required

from api.BaseApi import authentication_required, logger
from api.ResponseWrapper import ResponseWrapper
from model.BaseModel import database_proxy
from model.messages.Message import Message, MessageState
from repositories.MessageRepository import MessageRepository
from utilities.RandomUtility import RandomUtility
from utilities.TimestampUtility import TimestampUtility

messages = Blueprint('messages', __name__, url_prefix='/api/messages')

"""
The Messages API allows devices to send messages to other devices and receive pending messages from other devices.
"""


@messages.before_request
def before_request():
    if database_proxy.is_closed():
        database_proxy.connect()


@messages.after_request
def after_request(response):
    database_proxy.close()
    return response


@messages.route('/', methods=['POST'])
@jwt_required
@authentication_required
def create():
    identifier = g.get('identifier', None)
    message_parameters = request.get_json()
    message_repository = MessageRepository()
    message = message_repository.create_message(Message(
        identifier=RandomUtility.random_string(32),
        sender=identifier,
        recipient=message_parameters['recipient'],
        content=message_parameters['content'],
        state=MessageState.PENDING,
        created=TimestampUtility.parse(message_parameters['created']),
        received=TimestampUtility.now(),
        recipient_received=None
    ))
    result = Response(ResponseWrapper.wrap(identifier, 'messages.create', message),
                      status=200,
                      mimetype='application/json')
    return result


@messages.route('/', methods=['GET'])
@jwt_required
@authentication_required
def retrieve():
    identifier = g.get('identifier', None)
    message_repository = MessageRepository()
    results = message_repository.retrieve_messages_by_sender(identifier)
    result = Response(ResponseWrapper.wrap(identifier, 'messages.retrieve', results),
                      status=200,
                      mimetype='application/json')
    return result


@messages.route('/', methods=['PUT'])
@jwt_required
@authentication_required
def update():
    identifier = g.get('identifier', None)
    message_parameters = request.get_json()
    message_repository = MessageRepository()
    message = message_repository.update_message(
        message_parameters['identifier'],
        message_parameters['recipient_received'],
        MessageState.RECEIVED)
    result = Response(ResponseWrapper.wrap(identifier, 'messages.update', message),
                      status=200,
                      mimetype='application/json')
    return result


@messages.errorhandler(500)
def server_error(e):
    logger.exception('An error occurred during a request.')
    return """
    An internal error occurred: <pre>{}</pre>
    See logs for full stacktrace.
    """.format(e), 500
