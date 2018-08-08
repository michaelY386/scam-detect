import os
import unittest

from model.telemetry.Parameters import Parameters
from repositories.ParametersRepository import ParametersRepository
from unittests.repositories.DbTest import DbTest
from utilities.TimestampUtility import TimestampUtility


class TestParametersRepository(DbTest):
    def setUp(self):
        self.repository = ParametersRepository()

    def test_create_parameters(self):
        item = self.repository.create_parameters(
            Parameters(
                content="ParameterTest",
                created=TimestampUtility.now()
            )
        )
        self.assertEqual(item.content, 'ParameterTest')

    def test_retrieve_parameters(self):
        item = self.repository.create_parameters(
            Parameters(
                content="ParameterTest",
                created=TimestampUtility.now()
            )
        )
        self.assertEqual(item.content, 'ParameterTest')
        items = self.repository.retrieve_parameters()
        self.assertGreater(len(items), 1)


if __name__ == '__main__':
    unittest.main()
