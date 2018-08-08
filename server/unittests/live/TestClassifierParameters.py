import json
import unittest

import requests

from utilities.RandomUtility import RandomUtility

"""
Test the live production version of the server running on Google App Engine
"""


class TestClassifierParameters(unittest.TestCase):

    def test_index(self):
        # Data to send to the server
        data = {
            # Any structure for content
            'content': {
                'words': ['hello', 'world'],
                'more': 'important information'
            },
            # Hardcoded key
            'key': 'p4ZfOJxhXJOU5VE9mdPX8Mo5V8dveda1bCUQaQ4QzHo06nrklJxRvdNpUZSE4WnG'
        }
        # Send the data as a post requests
        response = requests.post(
            'https://eps-scams.appspot.com/api/parameters/',
            json=data)
        # Verify server accepted the data
        self.assertTrue(response.status_code == 200)
        json_body = response.json()
        # Check response message from server
        self.assertTrue(json_body['message'] == 'classifier parameters updated')

    def test_retrieve(self):
        register_data = {
                'identifier': 'test2_{}'.format(RandomUtility.random_string(6)),
                'secret': 'test2',
                'code': 'yRK3LxunjCFrovCXKyG32nB3pyST7ddE40T8FlxK8CCn75EyGr5jTanGyMqJ',
                'profile': 'profile',
                'recovery': 'recovery'}
        response = requests.post(
            'https://eps-scams.appspot.com/api/authentication/register',
            json=register_data)
        self.assertTrue(response.status_code == 200)
        login_data = {
            'identifier': register_data['identifier'],
            'secret': register_data['secret']}
        response = requests.post(
            'https://eps-scams.appspot.com/api/authentication/login',
            json=login_data)
        self.assertTrue(response.status_code == 200)
        login_json = response.json()
        access_token = login_json['result']['access_token']
        self.assertTrue(access_token is not None)
        retrieve_response = requests.get(
            'https://eps-scams.appspot.com/api/parameters/',
            headers={'Authorization': 'Bearer {}'.format(access_token)}
        )
        self.assertTrue(retrieve_response.status_code == 200)
        retrieve_json = retrieve_response.json()
        self.assertTrue(retrieve_json['operation'] == 'parameters.retrieve')


if __name__ == '__main__':
    unittest.main()
