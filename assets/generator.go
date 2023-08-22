/*
 * Copyright 2022-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
//go:generate go-bindata -nomemcopy -pkg bindata  -o ../internal/bindata/bindata.go -ignore=\.go$ ./...
//go:generate go-bindata -nomemcopy -pkg otherbindata  -o ../internal/otherbindata/bindata.go -ignore=\.go$ ./other/...

package assets
