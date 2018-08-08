import datetime
from peewee import *

from model.BaseModel import BaseModel


"""
Telemetries represnt the event objects recorded by the client devices.
"""


class Telemetry(BaseModel):
    creator = CharField(null=False)
    data_type = CharField(null=False)
    content = CharField(null=False)
    created = DateTimeField(default=datetime.datetime.now)
    received = DateTimeField(default=datetime.datetime.now)
