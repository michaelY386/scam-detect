import random
import string

"""
Create random strings
"""


class RandomUtility(object):

    @staticmethod
    def random_string(size):
        character_set = string.ascii_uppercase + string.digits
        return ''.join(random.choice(character_set) for character in range(size))
