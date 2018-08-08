import logging
from model.telemetry.Telemetry import Telemetry
from repositories.PersistenceError import PersistenceError

"""
Contains Telemetry table accesses.
"""


class TelemetryRepository(object):
    logger = logging.getLogger(__name__)

    def create_telemetry(self, telemetry):
        self.logger.debug("Inserting telemetry: {}".format(str(telemetry)))
        rows = telemetry.save()
        if rows == 1:
            self.logger.debug("Successfully inserted telemetry: {}".format(str(telemetry)))
            return telemetry
        else:
            self.logger.debug("Insert of telemetry: {} returned {}".format(str(telemetry), str(rows)))
            raise PersistenceError("Insert of telemetry: {} returned {}".format(str(telemetry), str(rows)))

    def retrieve_telemetry(self):
        self.logger.debug("Retrieving all telemetries")
        return Telemetry.select()

    def retrieve_telemetry_by_data_type(self, data_type):
        self.logger.debug("Retrieving all telemetries with data type {}".format(data_type))
        return Telemetry.select().where(Telemetry.data_type == data_type)
