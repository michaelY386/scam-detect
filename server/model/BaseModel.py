from peewee import Proxy, Model, PrimaryKeyField
from playhouse.shortcuts import model_to_dict

from utilities.JsonUtility import JsonUtility

database_proxy = Proxy()

"""
A base class for all model objects. Each model object represent a table in a relational database
"""


class BaseModel(Model):
    id = PrimaryKeyField()

    def __str__(self):
        return JsonUtility.to_json(model_to_dict(self, recurse=False))

    class Meta:
        database = database_proxy
