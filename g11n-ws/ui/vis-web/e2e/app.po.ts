/*
* Copyright 2019 VMware, Inc.
* SPDX-License-Identifier: EPL-2.0
*/
import { browser, element, by } from 'protractor';


export class ClaritySeedAppHome {

  navigateTo() {
    return browser.get('/');
  }

  getParagraphText() {
    return element(by.css('my-app p')).getText();
  }
}
