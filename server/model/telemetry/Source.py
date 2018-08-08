
import datetime
from peewee import *

from model.BaseModel import BaseModel


"""
Sources represent blacklisted sources of communication such as phone numbers.
"""

class Source(BaseModel):
    value = TextField(null=False)
    source_type = CharField(null=False)
    verifications = IntegerField(null=False)
    created = DateTimeField(default=datetime.datetime.now)