import datetime
from peewee import *

from model.BaseModel import BaseModel

"""
Parameters are the classifier paramters passed between the classification trainer and the client devices.
"""

class Parameters(BaseModel):
    content = TextField(null=False)
    created = DateTimeField(default=datetime.datetime.now)
