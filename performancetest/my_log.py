import logging
from logging import Formatter, StreamHandler

fmt = f'%(asctime)s | %(levelname)s | %(funcName)s:%(lineno)d | %(message)s'


def format_logger(log_name: str = None) -> logging.Logger:
    logger = logging.getLogger(log_name)
    logger.setLevel(level=logging.DEBUG)
    formatter: Formatter = Formatter(fmt)

    stream_handler: StreamHandler = StreamHandler()
    stream_handler.setLevel(level=logging.INFO)
    stream_handler.setFormatter(formatter)
    logger.addHandler(stream_handler)
    return logger

