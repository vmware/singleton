from sgtnclient.I18N import Logger

import logging
from logging import Formatter
from logging.handlers import TimedRotatingFileHandler as FileHandler

fmt = f'%(asctime)s - %(levelname)s - %(filename)s:%(funcName)s:%(lineno)d - %(message)s'
LOG_FILE = 'log/run.log'

LOG_TYPE_MAP = {
    'info': 10,
    'debug': 20,
    'warn': 30,
    'warning': 30,
    'error': 40,
    'critical': 50
}


class MyLogger(Logger):
    def __init__(self, log_name: str = None, log_file: str = LOG_FILE):
        self.logger = logging.getLogger(log_name)
        self.logger.setLevel(level=logging.DEBUG)
        formatter: Formatter = Formatter(fmt)

        file_handler: FileHandler = FileHandler(filename=log_file, encoding='utf-8')
        file_handler.setLevel(level=logging.DEBUG)
        file_handler.setFormatter(formatter)
        self.logger.addHandler(file_handler)

    def log(self, text, log_type):
        if isinstance(log_type, int):
            self.logger.log(log_type, msg=text)
        elif isinstance(log_type, str):
            log_type = log_type.lower()
            self.logger.log(LOG_TYPE_MAP[log_type], text)
