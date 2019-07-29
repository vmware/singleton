import * as CoreSDK from '@vip/vip-core-sdk-dev';
import * as Consts from '../utils/constants';

debugger

var config = Consts.config;

describe('Initialization - positive', () => {
    it('Normal init', async () => {
        CoreSDK.i18nClient.init(config);

        const mockCallback = jest.fn();
        await CoreSDK.i18nClient.coreService.loadI18nData(mockCallback).then((res) => {
            expect(res).not.toBeNull();
        });

        expect(mockCallback.mock.calls.length).toBe(1);
    });
    
    it('Create an new instance', async () => {
        let inst_new = CoreSDK.i18nClient.createInstance(config);
        expect(inst_new).not.toBeNull();
        expect(inst_new).not.toBe(CoreSDK.i18nClient);
    });
});

describe('Initialization - negative', () => {
    it('productID is empty string', () => {
        let errMsg = "Paramater: 'ProductID' required for 'CoreService'";
        expect(()=> {CoreSDK.i18nClient.init({...config, productID : ''});})
        .toThrowError(errMsg);
    });
    it('version is empty string', () => {
        expect(()=> {CoreSDK.i18nClient.init({...config, version : ''});})
        .toThrowError();
    });
    it('host is empty string', () => {
        expect(()=> {CoreSDK.i18nClient.init({...config, host : ''});})
        .toThrowError();
    }); 
});


// Don't need to test plug, this has been tested in unit test.














