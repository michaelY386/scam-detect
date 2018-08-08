from peewee import ModelSelect
from playhouse import shortcuts
from playhouse.shortcuts import model_to_dict

from utilities.JsonUtility import JsonUtility


class ResponseWrapper(object):

    def __init__(self, identifier, operation, result):
        self.identifier = identifier
        self.operation = operation
        self.result = result

    def to_json(self):
        if isinstance(self.result, list) or isinstance(self.result, ModelSelect):
            result_set = [ResponseWrapper.to_dict(item) for item in self.result]
            return JsonUtility.to_json(
                {
                    'identifier': self.identifier,
                    'operation': self.operation,
                    'result': result_set
                })
        else:
            return JsonUtility.to_json(
                {
                    'identifier': self.identifier,
                    'operation': self.operation,
                    'result': ResponseWrapper.to_dict(self.result)
                })

    @staticmethod
    def to_dict(item):
        output = item
        if isinstance(output, dict):
            output = item
        else:
            output = model_to_dict(output, recurse=False)
        return output

    @staticmethod
    def wrap(identifier, operation, result):
        wrapped = ResponseWrapper(identifier, operation, result)
        return wrapped.to_json()
