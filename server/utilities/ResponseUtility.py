import json

from flask import Response

"""
Build standard HTTP responses
"""


class ResponseUtility(object):

    @staticmethod
    def unauthorized(message):
        return Response(
            response=json.dumps({'message': message}),
            status=403
        )

    @staticmethod
    def empty(method_name):
        return Response(
            response=json.dumps({'method': method_name,
                                 'message': 'empty'}),
            status=200
        )

