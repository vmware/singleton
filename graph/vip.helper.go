package graph

import (
	"context"
	"fmt"
	"github.com/gin-gonic/gin"
	"gopkg.in/jucardi/go-logger-lib.v1/log"
	"sgtnserver/modules/translation"
	"sgtnserver/modules/translation/translationservice"
)

func GinContextFromContext(ctx context.Context) (*gin.Context, error) {
	ginContext := ctx.Value("GinContextKey")
	if ginContext == nil {
		err := fmt.Errorf("could not retrieve gin.Context")
		return nil, err
	}

	gc, ok := ginContext.(*gin.Context)
	if !ok {
		err := fmt.Errorf("gin.Context has wrong type")
		return nil, err
	}
	return gc, nil
}

func getMessages(ctx context.Context, product *string, version *string, component *string, locale *string) *translation.Bundle {
	bundleID := &translation.BundleID{Name: *product, Version: *version, Locale: *locale, Component: *component}
	data, err := l3Service.GetBundle(ctx, bundleID)
	if err != nil {
		log.Error(err)
	}
	return data
}

func GetAvailableLocales(ctx context.Context, product string, version string) []string {
	locales, _ := l3Service.GetAvailableLocales(ctx, product, version)
	return locales
}

func GetAvailableComponents(ctx context.Context, product string, version string) []string {
	components, _ := l3Service.GetAvailableComponents(ctx, product, version)
	return components
}

var l3Service translation.Service = translationservice.GetService()
