import logging
import unittest

import sys

from app import AppFactory


class TestBaseApi(unittest.TestCase):
    def setUp(self):
        console_handler = logging.StreamHandler(sys.stdout)
        root_logger = logging.getLogger()
        root_logger.setLevel(logging.DEBUG)
        root_logger.addHandler(console_handler)
        self.app = AppFactory.create_app(test_flag=True)
        self.client = self.app.test_client()

    def test_index(self):
        response = self.client.get('/')
        self.assertTrue(response.status_code == 200)


if __name__ == '__main__':
    unittest.main()
