/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
const express = require('express');
const i18nHandler = require('./i18n/i18nHandler')

const app = express();
const port = process.env.PORT || 8000;

app.use(i18nHandler.handle);


app.get('/', (req, res) => {
  let _Thead=`<thead><tr><th>Type</th><th>Input</th><th>Output</th></tr></thead>`
  let _Tbody=`<tbody>
    <tr align='center'><td>Datetime</td><td>${new Date()}</td><td>${req.formatDate(new Date(),'medium')}</td></tr>
    <tr align='center'><td>Percent</td><td>0.5569</td><td>${req.formatPercent('0.5569')}</td></tr>
    <tr align='center'><td>Number</td><td>1.2345</td><td>${req.formatNumber('1.2345')}</td></tr>
    <tr align='center'><td>Currency</td><td>2.5445</td><td>${req.formatCurrency('2.5445','USD')}</td></tr>
  </tbody>`

  res.send(`${req.t('title')}</br></br><table border='1' width="60%">${_Thead}${_Tbody}</table>`);
});

app.listen(port, (err) => {
  console.log(`Server is listening on port ${port}`);
});