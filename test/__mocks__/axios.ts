/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

const mockAxios: any = jest.genMockFromModule('axios');

mockAxios.create = jest.fn(() => mockAxios);

mockAxios.get = jest.fn(() => Promise.resolve({ data: {} }));

mockAxios.post = jest.fn(() => Promise.resolve({ data: {} }));

export default mockAxios;