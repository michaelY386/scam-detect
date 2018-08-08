import logging
from model.messages.Message import Message
from repositories.PersistenceError import PersistenceError
from utilities.TimestampUtility import TimestampUtility

"""
Contains Message table accesses.
"""


class MessageRepository(object):
    logger = logging.getLogger(__name__)

    def create_message(self, message):
        self.logger.debug("Inserting message: {}".format(str(message)))
        rows = message.save()
        if rows == 1:
            self.logger.debug("Successfully inserted message: {}".format(str(message)))
            return message
        else:
            self.logger.debug("Insert of message: {} returned {}".format(str(message), str(rows)))
            raise PersistenceError("Insert of message: {} returned {}".format(str(message), str(rows)))

    def retrieve_messages(self):
        self.logger.debug("Retrieving all message")
        return Message.select()

    def retrieve_messages_by_sender(self, identifier):
        self.logger.debug("Retrieving all messages to {}".format(identifier))
        return Message.select().where(Message.recipient == identifier and Message.state == 0)

    def update_message(self, identifier, recipient_received, state):
        message = self.retrieve_message_by_identifier(identifier)
        message.recipient_received = TimestampUtility.parse(recipient_received)
        message.state = state
        updates = message.save()
        if updates == 1:
            self.logger.debug("Successfully updated message: {}".format(message.identifier))
            return message
        else:
            self.logger.error("Update of message: {} returned {}".format(identifier, str(updates)))

    def retrieve_message_by_identifier(self, identifier):
        self.logger.debug('Retrieving message: {}'.format(identifier))
        return Message.select().where(Message.identifier == identifier).get()
