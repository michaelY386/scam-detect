import json
import logging
import unittest
import sys

from app import AppFactory
from utilities.JsonUtility import JsonUtility
from utilities.RandomUtility import RandomUtility


class TestSourceApi(unittest.TestCase):
    def setUp(self):
        console_handler = logging.StreamHandler(sys.stdout)
        root_logger = logging.getLogger()
        root_logger.setLevel(logging.DEBUG)
        root_logger.addHandler(console_handler)
        self.app = AppFactory.create_app(test_flag=True)
        self.client = self.app.test_client()
        identifier = 'test_{}'.format(RandomUtility.random_string(6))
        register_response = self.client.post(
            '/api/authentication/register',
            data=json.dumps({
                'identifier': identifier,
                'secret': 'test',
                'code': 'yRK3LxunjCFrovCXKyG32nB3pyST7ddE40T8FlxK8CCn75EyGr5jTanGyMqJ',
                'profile': 'profile',
                'recovery': 'recovery'}),
            content_type='application/json'
        )
        self.assertTrue(register_response.status_code == 200)
        login_response = self.client.post(
            '/api/authentication/login',
            data=json.dumps({'identifier': identifier, 'secret': 'test'}),
            content_type='application/json'
        )
        login_json = json.loads(login_response.data)
        self.access_token = login_json['result']['access_token']

    def test_create(self):
        create_response = self.client.post(
            '/api/sources/',
            data=JsonUtility.to_json(
                {'value': '123-456-7890', 'source_type': 'blacklist'}),
            content_type='application/json',
            headers={'Authorization': 'Bearer {}'.format(self.access_token)}
        )
        self.assertTrue(create_response.status_code == 200)
        create_json = json.loads(create_response.data)
        self.assertTrue(create_json['operation'] == 'sources.create')

    def test_retrieve(self):
        create_response = self.client.post(
            '/api/sources/',
            data=JsonUtility.to_json(
                {'value': '123-456-7899', 'source_type': 'blacklist'}),
            content_type='application/json',
            headers={'Authorization': 'Bearer {}'.format(self.access_token)}
        )
        self.assertTrue(create_response.status_code == 200)
        create_json = json.loads(create_response.data)
        self.assertTrue(create_json['operation'] == 'sources.create')
        retrieve_response = self.client.get(
            '/api/sources/',
            content_type='application/json',
            headers={'Authorization': 'Bearer {}'.format(self.access_token)}
        )
        self.assertTrue(retrieve_response.status_code == 200)
        retrieve_json = json.loads(retrieve_response.data)
        self.assertTrue(retrieve_json['operation'] == 'sources.retrieve')


if __name__ == '__main__':
    unittest.main()
