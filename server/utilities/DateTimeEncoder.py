from datetime import datetime
import json

from utilities.TimestampUtility import TimestampUtility

"""
Encode datetime in json format
"""


class DateTimeEncoder(json.JSONEncoder):
    def default(self, o):
        if isinstance(o, datetime):
            return TimestampUtility.to_timestamp(o)
        return json.JSONEncoder.default(self, o)