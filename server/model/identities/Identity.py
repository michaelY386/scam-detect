from enum import IntEnum
from peewee import *

from model.BaseModel import BaseModel


"""
Identities represent device identities. So each phone has a unique identity.
"""

class Identity(BaseModel):
    identifier = CharField(null=False)
    secret = CharField(null=False)
    profile = CharField(null=False)
    recovery = CharField(null=False)
    status = IntegerField(null=False)


class IdentityState(IntEnum):
    NOT_IN_USE = 0
    IN_USE = 1
