import yaml
from pathlib import Path

__RESOURCES__ = Path(__file__).parent.joinpath('config')


def config_reader(config_file: str):
    file: Path = __RESOURCES__.joinpath(config_file)
    with open(file, encoding='utf-8', mode='r') as f:
        return yaml.safe_load(f)


if __name__ == '__main__':
    print(config_reader('only_online.yml'))
