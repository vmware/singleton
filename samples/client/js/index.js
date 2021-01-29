/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
const express = require('express');
const path = require('path');

const app = express();
const port = process.env.PORT || 3000;

app.use(express.static(__dirname));

app.get('/', (req, res) => {
  res.send(`Welcome to the static localizer. Navigate to /example`);
});

app.get('/example', (req, res) => {
  res.sendFile(path.join(__dirname + '/html/example.html'));
})

app.listen(port, (err) => {
  console.log(`Server is listening on port ${port}`);
});