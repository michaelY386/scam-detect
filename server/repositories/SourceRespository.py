import logging
from model.telemetry.Source import Source
from repositories.PersistenceError import PersistenceError

"""
Contains Source table accesses.
"""


class SourceRepository(object):
    logger = logging.getLogger(__name__)

    def create_source(self, source):
        self.logger.debug("Inserting source: {}".format(str(source)))
        rows = source.save()
        if rows == 1:
            self.logger.debug("Successfully inserted source: {}".format(str(source)))
            return source
        else:
            self.logger.debug("Insert of source: {} returned {}".format(str(source), str(rows)))
            raise PersistenceError("Insert of source: {} returned {}".format(str(source), str(rows)))

    def retrieve_sources(self):
        self.logger.debug("Retrieving all source")
        return Source.select().order_by(Source.created.desc())

    def update(self, input_source):
        source = self.retrieve_source_by_value(input_source.value)
        source.source_type = input_source.source_type
        source.verifications = input_source.verifications
        updates = source.save()
        if updates == 1:
            self.logger.debug("Successfully updated source: {}".format(source.value))
            return source
        else:
            self.logger.debug("Update of source: {} returned {}".format(source.value, str(updates)))
            raise PersistenceError("Failed to update source: {}".format(source.value))

    def retrieve_sources_by_source_type(self, source_type):
        self.logger.debug("Retrieving all source with type {}".format(source_type))
        return Source.select().where(Source.source_type == source_type).order_by(Source.created.desc())

    def retrieve_source_by_value(self, value):
        self.logger.debug("Retrieving all source with value {}".format(value))
        return Source.select().where(Source.value == value)
