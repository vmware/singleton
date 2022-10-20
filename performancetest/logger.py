# @Time 2022/10/20 14:52
# Author: beijingm

import logging
from logging import Formatter, StreamHandler


def simple_logger(log_name: str = None) -> logging.Logger:
    logger = logging.getLogger(log_name)
    logger.setLevel(level=logging.DEBUG)
    formatter: Formatter = Formatter('{asctime} - {levelname:6s} - {message}', style='{')

    stream_handler: StreamHandler = StreamHandler()
    stream_handler.setLevel(level=logging.DEBUG)
    stream_handler.setFormatter(formatter)
    logger.addHandler(stream_handler)

    return logger


if __name__ == '__main__':
    log = simple_logger(__name__)
    log.info('info')
    log.error('error')
    log.critical('critical')
