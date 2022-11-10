import pytest
from pathlib import Path
from sgtn4python.sgtnclient import I18N

PRODUCT = 'VMCUI'
VERSION = '1.0.0'
COMPONENT = 'AngularJS2'
LOCALE = 'fr'
Config_files = 'only_onlineprelong.yml'

# singleton\test\TRANSLATION
__TRANSLATION__ = Path(__file__).parent
__CONFIG__ = __TRANSLATION__.joinpath('config')
__RESOURCES__ = __TRANSLATION__.joinpath('resources')


class Test_da4:
    @pytest.mark.ci1
    def test_l1(self):
        print("online:component and key")
        file: Path = __CONFIG__.joinpath('only_onlineprelong.yml')
        I18N.add_config_file(file)
        # I18N.set_current_locale(LOCALE)
        # current = I18N.get_current_locale()
        I18N.set_current_locale(LOCALE)
        self.rel = I18N.get_release(PRODUCT, VERSION)
        # conf =self.rel.get_config()
        translation = self.rel.get_translation()
        # tran1 = translation.get_string("about","about.message")
        # print(tran1)
        # assert tran1 == "test fr key"
        # tran1 = translation.get_string("about", "about.message", locale = 'en')
        # print(tran1)
        tran2 = translation.get_string("AngularJS2", "vmc-kubernetes-what-is-kubernetes.architecture-desc", locale='de')
        print("tets html %s " % tran2)
        tran3 = translation.get_string("AngularJS2", "vmc-kubernetes-overview.tanzu-grid-simplified-desc", locale='de')
        print("test coooo %s" % tran3)
        tran4 = translation.get_string("AngularJS2", "create-sddc.configuration.standard.optional-linking", locale='ja')
        print("test link %s" % tran4)
        tran5 = translation.get_string("vmw-k8s", "vmc-kubernetes-faqs.answer-2", locale='fr')
        print("test common %s" % tran5)
        # assert tran2 == "test ja key"
    #
