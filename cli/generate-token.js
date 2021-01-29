/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
const axios = require('axios');
const debug = require('debug')('generate-token');

const generateToken = function (host, refreshToken) {

    return axios.post(`${host}/csp/gateway/am/api/auth/api-tokens/authorize`,
        { 'refresh_token': refreshToken }, {
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            }
        })
        .then(response => {
            debug(`Token has been generated successfully.`);
            return response.data.access_token;
        }).catch((error) => {
            debug(`Token generation failed.`);
            return Promise.reject(error);
        });
}

module.exports = generateToken;
