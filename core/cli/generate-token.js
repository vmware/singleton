/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
const superagent = require('superagent');
const debug = require('debug')('generate-token');

const generateToken = function(host, refreshToken) {
    return superagent
        .post(`${host}/csp/gateway/am/api/auth/api-tokens/authorize`)
        .set('Content-Type', 'application/x-www-form-urlencoded')
        .send({
            'refresh_token': refreshToken
        })
        .then(response => {
            debug(`Token has been generated successfully.`);
            return response.body.access_token;
        }).catch((error) => {
            debug(`Token generation failed.`);
            return Promise.reject(error);
        });
}

module.exports = generateToken;
