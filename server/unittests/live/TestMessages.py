import unittest

import requests

from utilities.RandomUtility import RandomUtility
from utilities.TimestampUtility import TimestampUtility

"""
Test the live production version of the server running on Google App Engine
"""


class TestMessages(unittest.TestCase):

    def test_register(self):
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

        create_response = requests.post(
            'https://eps-scams.appspot.com/api/messages/',
            json={
                'recipient': 'test',
                'content': 'hello world',
                'created': TimestampUtility.now().timestamp()},
            headers={'Authorization': 'Bearer {}'.format(access_token)}
        )
        self.assertTrue(create_response.status_code == 200)
        retrieve_response = requests.get(
            'https://eps-scams.appspot.com/api/messages/',
            headers={'Authorization': 'Bearer {}'.format(access_token)}
        )
        self.assertTrue(retrieve_response.status_code == 200)
        retrieve_json = retrieve_response.json()
        self.assertTrue(retrieve_json['operation'] == 'messages.retrieve')


if __name__ == '__main__':
    unittest.main()
