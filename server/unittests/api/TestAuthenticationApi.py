import json
import logging
import unittest

import sys

import os

from app import AppFactory

"""
Test the authentication API
"""


class TestAuthenticationApi(unittest.TestCase):

    def setUp(self):
        console_handler = logging.StreamHandler(sys.stdout)
        root_logger = logging.getLogger()
        root_logger.setLevel(logging.DEBUG)
        root_logger.addHandler(console_handler)
        self.app = AppFactory.create_app(test_flag=True)
        self.client = self.app.test_client()

    def test_register(self):
        register_response = self.client.post(
            '/api/authentication/register',
            data=json.dumps({
                'identifier': 'test',
                'code': 'yRK3LxunjCFrovCXKyG32nB3pyST7ddE40T8FlxK8CCn75EyGr5jTanGyMqJ',
                'secret': 'test',
                'profile': 'profile',
                'recovery': 'recovery'
            }),
            content_type='application/json'
        )
        self.assertTrue(register_response.status_code == 200)
        register_json = json.loads(register_response.data)
        register_result = register_json['result']
        self.assertTrue(register_result is not None)

    def test_login(self):
        register_response = self.client.post(
            '/api/authentication/register',
            data=json.dumps({
                'identifier': 'test2',
                'secret': 'test2',
                'code': 'yRK3LxunjCFrovCXKyG32nB3pyST7ddE40T8FlxK8CCn75EyGr5jTanGyMqJ',
                'profile': 'profile',
                'recovery': 'recovery'}),
            content_type='application/json'
        )
        self.assertTrue(register_response.status_code == 200)
        response = self.client.post(
            '/api/authentication/login',
            data=json.dumps({'identifier': 'test2', 'secret': 'test2'}),
            content_type='application/json'
        )
        self.assertTrue(response.status_code == 200)
        login_json = json.loads(response.data)
        access_token = login_json['result']['access_token']
        self.assertTrue(access_token is not None)

    def test_access_token(self):
        register_response = self.client.post(
            '/api/authentication/register',
            data=json.dumps({
                'identifier': 'test3',
                'secret': 'test3',
                'code': 'yRK3LxunjCFrovCXKyG32nB3pyST7ddE40T8FlxK8CCn75EyGr5jTanGyMqJ',
                'profile': 'profile',
                'recovery': 'recovery'}),
            content_type='application/json'
        )
        self.assertTrue(register_response.status_code == 200)
        response = self.client.post(
            '/api/authentication/login',
            data=json.dumps({'identifier': 'test3', 'secret': 'test3'}),
            content_type='application/json'
        )
        self.assertTrue(response.status_code == 200)
        login_json = json.loads(response.data)
        access_token = login_json['result']['access_token']
        self.assertTrue(access_token is not None)
        protected_response = self.client.get(
            '/api/authentication/protected',
            content_type='application/json',
            headers={'Authorization': 'Bearer {}'.format(access_token)}
        )
        self.assertTrue(protected_response.status_code == 200)


if __name__ == '__main__':
    unittest.main()
