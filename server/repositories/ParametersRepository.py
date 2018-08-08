import logging
from model.telemetry.Parameters import Parameters
from repositories.PersistenceError import PersistenceError

"""
Contains Parameters table accesses.
"""


class ParametersRepository(object):
    logger = logging.getLogger(__name__)

    def create_parameters(self, parameters):
        self.logger.debug("Inserting parameters: {}".format(str(parameters)))
        rows = parameters.save()
        if rows == 1:
            self.logger.debug("Successfully inserted parameters: {}".format(str(parameters)))
            return parameters
        else:
            self.logger.debug("Insert of parameters: {} returned {}".format(str(parameters), str(rows)))
            raise PersistenceError("Insert of parameters: {} returned {}".format(str(parameters), str(rows)))

    def retrieve_parameters(self):
        self.logger.debug("Retrieving all parameters")
        return Parameters.select().order_by(Parameters.created.desc())

    def retrieve_parameter(self):
        self.logger.debug("Retrieving all parameters")
        return Parameters.select().order_by(Parameters.created.desc()).get()
