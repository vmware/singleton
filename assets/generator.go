/*
 * Copyright 2022-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

//go:generate go-bindata -nomemcopy -pkg cldrbindata -o ../internal/bindata/cldr/bindata.go cldr/...
//go:generate go-bindata -nomemcopy -pkg otherbindata -o ../internal/bindata/other/bindata.go other/...

package assets
