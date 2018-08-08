import json
import unittest

import requests

from utilities.RandomUtility import RandomUtility

"""
Test the live production version of the server running on Google App Engine
"""


class TestAuthentication(unittest.TestCase):

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


if __name__ == '__main__':
    unittest.main()
