from utilities.RandomUtility import RandomUtility


"""
The Config Module provides simple configurations for deployment version of the application such as production, development,
and testing.
"""

class Config(object):
    DEBUG = False
    TESTING = False
    DATABASE_URI = 'sqlite://:memory:'


class ProductionConfig(Config):
    DATABASE_URI = 'mysql://user@localhost/foo'
    JWT_SECRET_KEY = RandomUtility.random_string(64)


class DevelopmentConfig(Config):
    DEBUG = True


class TestingConfig(Config):
    TESTING = True
    JWT_SECRET_KEY = 'helloworldvalue'
