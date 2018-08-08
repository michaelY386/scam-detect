import logging
import os
from flask import Flask
from flask_jwt_extended import JWTManager
from peewee import SqliteDatabase
from playhouse.db_url import connect

from api.ParametersApi import parameters
from api.SourceApi import sources
from api.TelemetryApi import telemetry
from app import ConfigModule
from api.AuthenticationApi import authentication
from api.BaseApi import base
from api.MessagesApi import messages
from model.BaseModel import database_proxy
from model.identities.Identity import Identity
from model.messages.Message import Message
from model.telemetry.Parameters import Parameters
from model.telemetry.Source import Source
from model.telemetry.Telemetry import Telemetry

logger = logging.getLogger(__name__)


"""
This factory class builds the application in either test or production mode. In test, a SQLite database is built on
the local filesystem. In production a database URI is provided through the application configuration (which is read
from the environment variables.
"""


def create_app(test_flag):
    app = Flask(__name__)

    if test_flag is True:
        logger.debug("Setting up in testing mode")
        app.config.from_object(ConfigModule.TestingConfig)
        sqlite_file = 'local.db'
        try:
            os.remove(sqlite_file)
        except:
            pass
        sqlite_db = SqliteDatabase(sqlite_file)
        database_proxy.initialize(sqlite_db)
        database_proxy.create_tables([Message, Identity, Telemetry, Parameters, Source], safe=True)
        database_proxy.close()
    else:
        logger.debug("Setting up in production mode")
        app.config.from_object(ConfigModule.ProductionConfig)
        app.config['PEEWEE_DATABASE_URI'] = os.environ['PEEWEE_DATABASE_URI']
        database = connect(app.config['PEEWEE_DATABASE_URI'])
        database_proxy.initialize(database)
        database_proxy.create_tables([Message, Identity, Telemetry, Parameters, Source], safe=True)
        database_proxy.close()
    jwt = JWTManager(app)
    app.register_blueprint(authentication)
    app.register_blueprint(messages)
    app.register_blueprint(telemetry)
    app.register_blueprint(parameters)
    app.register_blueprint(sources)
    app.register_blueprint(base)
    return app
