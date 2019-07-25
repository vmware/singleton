/*
* Copyright 2019 VMware, Inc.
* SPDX-License-Identifier: EPL-2.0
*/
import {ClaritySeedAppHome} from './app.po';

fdescribe('clarity-seed app', function () {

  let expectedMsg: string = 'This is a Clarity seed application. This is the default page that loads for the application.';

  let page: ClaritySeedAppHome;

  beforeEach(() => {
    page = new ClaritySeedAppHome();
  });

  it('should display: ' + expectedMsg, () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual(expectedMsg)
  });
});
