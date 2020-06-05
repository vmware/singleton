/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

/**
 * verify value if undefined or null
 */

export function isDefined(value: any) {
  return value !== undefined && value !== null && value !== '';
}

export function isEmptyObject( obj: any ) {
  if (!isDefined(obj)) {
      return true;
  }
  const keys = Object.keys(obj);
  return keys.length ? false : true;
}

