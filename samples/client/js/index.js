/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
const express = require('express');
const path = require('path');

// set up rate limiter: maximum of five requests per minute
const RateLimit = require('express-rate-limit');
const limiter = RateLimit({
  windowMs: 1 * 60 * 1000, // 1 minute
  max: 5
});

const app = express();
const port = process.env.PORT || 3000;

// apply rate limiter to all requests
app.use(limiter);

app.use ('/dist',express.static(__dirname + '/dist/'));

app.use ('/assets',express.static(__dirname + '/assets/'));

app.use ('/src/translations',express.static(__dirname + '/src/translations'));

app.get('/', (req, res) => {
  res.send(`Welcome to the static localizer. Navigate to /example`);
});

app.get('/example', (req, res) => {
  res.sendFile(path.join(__dirname + '/html/example.html'));
})

app.listen(port, (err) => {
  console.log(`Server is listening on port ${port}`);
});