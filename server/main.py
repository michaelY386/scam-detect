import logging

import sys

from app import AppFactory


if __name__ == '__main__':
    print("Running in debug mode with sqlite database")
    consoleHandler = logging.StreamHandler(sys.stdout)
    rootLogger = logging.getLogger()
    rootLogger.setLevel(logging.DEBUG)
    rootLogger.addHandler(consoleHandler)
    app = AppFactory.create_app(test_flag=True)
    app.run(host='127.0.0.1', port=8080, debug=True)
else:
    app = AppFactory.create_app(test_flag=False)
