/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { defaultLoader } from '../src/loader';
import mockAxios from './__mocks__/axios';

describe('Loader module', () => {
  it('calls axios and returns data', async () => {
    // setup
    mockAxios.get.mockImplementationOnce(() =>
      Promise.resolve({
        data: {
          result: { key: 'value' }
        }
      }));
    // execute and replace axios.get with mockAxios.get
    const res = await defaultLoader.getI18nResource('http://localhost', {});

    // expect
    expect(res).toEqual({ result: { key: 'value' } });
    expect(mockAxios.get).toHaveBeenCalledTimes(1);
    expect(mockAxios.get).toHaveBeenCalledWith('http://localhost', {});
  });

  it('should throw Error when loader without valid paramerters', () => {
    expect(() => { defaultLoader.getI18nResource('http://localhost', {}); }).toThrowError;
  });

});
