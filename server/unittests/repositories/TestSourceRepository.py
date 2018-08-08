import unittest

from model.telemetry.Source import Source
from repositories.SourceRespository import SourceRepository
from unittests.repositories.DbTest import DbTest
from utilities.TimestampUtility import TimestampUtility


class TestSourceRepository(DbTest):
    def setUp(self):
        self.repository = SourceRepository()

    def test_create_parameters(self):
        item = self.repository.create_source(
            Source(
                value='123-456-7890',
                source_type="blacklist",
                verifications=10,
                created=TimestampUtility.now()
            )
        )
        self.assertEqual(item.value, '123-456-7890')

    def test_retrieve_sources(self):
        item = self.repository.create_source(
            Source(
                value='123-456-7890',
                source_type="blacklist",
                verifications=10,
                created=TimestampUtility.now()
            )
        )
        self.assertEqual(item.value, '123-456-7890')
        items = self.repository.retrieve_sources()
        self.assertGreater(len(items), 1)


if __name__ == '__main__':
    unittest.main()
