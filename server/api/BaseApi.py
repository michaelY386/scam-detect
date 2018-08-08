import json
from functools import wraps

from flask_jwt_extended import get_jwt_identity
from werkzeug.local import LocalProxy
from flask import current_app, Blueprint, Response, g

from utilities.ResponseUtility import ResponseUtility

logger = LocalProxy(lambda: current_app.logger)

base = Blueprint('base', __name__, url_prefix='')


@base.route('/')
def index():
    return Response(json.dumps({'status': 'ok'}), status=200, mimetype='application/json')


def authentication_required(func):
    @wraps(func)
    # Define decorator function
    def wrapper(*args, **kwargs):
        identifier = get_jwt_identity()
        if identifier is None:
            return ResponseUtility.unauthorized('login required')
        g.identifier = identifier
        return func(*args, **kwargs)
    return wrapper
