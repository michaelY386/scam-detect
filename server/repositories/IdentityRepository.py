import logging
from model.identities.Identity import Identity
from repositories.PersistenceError import PersistenceError


"""
Contains Identity table accesses.
"""


class IdentityRepository(object):
    logger = logging.getLogger(__name__)

    def create_identity(self, identity):
        self.logger.debug("Inserting client identity: {}".format(str(identity)))
        rows = identity.save()
        if rows == 1:
            self.logger.debug("Successfully inserted identity: {}".format(str(identity)))
            return identity
        else:
            self.logger.debug("Insert of message: {} returned {}".format(str(identity), str(rows)))
            raise PersistenceError("Insert of message: {} returned {}".format(str(identity), str(rows)))

    def retrieve_identities(self):
        self.logger.debug("Retrieving all identities")
        return Identity.select()

    def retrieve_identity_by_identifier(self, identifier):
        self.logger.debug('Retrieving identity: {}'.format(identifier))
        return Identity.select().where(Identity.identifier == identifier).get()

    def update_identity(self, identifier, profile, recovery, status):
        identity = self.retrieve_identity_by_identifier(identifier)
        identity.profile = profile
        identity.recovery = recovery
        identity.status = status
        updates = identity.save()
        if updates == 1:
            self.logger.debug("Successfully updated identity: {}".format(identity.identifier))
            return identity
        else:
            self.logger.debug("Update of identity: {} returned {}".format(identity.identifier, str(updates)))
            raise PersistenceError("Failed to update identity: {}".format(identity.identifier))
