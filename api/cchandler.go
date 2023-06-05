/*
 * Copyright 2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package api

import (
	"net/http"

	"sgtnserver/internal/config"

	"github.com/gin-gonic/gin"
	"github.com/go-http-utils/headers"
)

// CC ...
func CC(w bool) gin.HandlerFunc {
	return newCCHandler(w).Handle
}

type ccHandler struct {
	weak bool
	cc   string
}

func newCCHandler(w bool) *ccHandler {
	handler := &ccHandler{
		weak: w,
		cc:   config.Settings.Server.CacheControl}
	return handler
}

func (g *ccHandler) Handle(c *gin.Context) {
	if c.Request.Method == http.MethodGet {
		c.Writer = &ccWriter{
			request:        c.Request,
			ResponseWriter: c.Writer,
			weak:           g.weak,
			cc:             g.cc}
	}
}

type ccWriter struct {
	weak bool
	cc   string
	gin.ResponseWriter
	request *http.Request
}

func (w *ccWriter) Write(data []byte) (int, error) {
	if !IsHTTPSuccess(w.Status()) || !w.processCC(data) {
		return w.ResponseWriter.Write(data)
	}
	return 0, nil
}

func (w *ccWriter) processCC(bts []byte) (notModified bool) {
	etag := GenerateEtag(bts, w.weak)
	// Write ETag directly because JAVA client can't read header case-insensitively.
	w.ResponseWriter.Header()[headers.ETag] = []string{etag}
	notModified = w.request.Header.Get(headers.IfNoneMatch) == etag
	if notModified {
		w.ResponseWriter.WriteHeader(http.StatusNotModified)
	}

	// Write Cache-Control header
	w.ResponseWriter.Header().Set(headers.CacheControl, w.cc)

	return
}
