package graph

// This file will be automatically regenerated based on the schema, any resolver implementations
// will be copied through when generating and any unknown code will be moved to the end.

import (
	"context"
	"encoding/json"
	"sgtnserver/api"
	"sgtnserver/graph/generated"
	"sgtnserver/graph/model"
	"sgtnserver/internal/config"

	"github.com/go-http-utils/headers"
)

// Bundles is the resolver for the bundles field.
func (r *queryResolver) Bundles(ctx context.Context, product string, version string, locales []string, components []*string) ([]*model.Bundle, error) {
	var bundles []*model.Bundle

	if len(locales) == 0 {
		var supportedLocales = GetAvailableLocales(ctx, product, version)
		for _, languageTag := range supportedLocales {
			var l = languageTag
			locales = append(locales, l)
		}
	}

	if len(components) == 0 {
		var allComponents = GetAvailableComponents(ctx, product, version)
		for _, comp := range allComponents {
			var c = comp
			components = append(components, &c)
		}
	}
	for _, locale := range locales {
		for _, comp := range components {
			var bundle = getMessages(ctx, &product, &version, comp, &locale)
			if bundle == nil {
				continue
			}
			var messages map[string]interface{}
			json.Unmarshal([]byte(bundle.Messages.ToString()), &messages)

			var b = model.Bundle{
				Product:   product,
				Version:   version,
				Component: *comp,
				Locale:    locale,
				Messages:  messages,
			}
			bundles = append(bundles, &b)
		}
	}

	ginCtx, _ := GinContextFromContext(ctx)
	ginCtx.Writer.Header().Set(headers.CacheControl, config.Settings.Server.CacheControl)
	b, _ := json.Marshal(bundles)
	var etag = api.GenerateEtag(b, false)
	ginCtx.Writer.Header().Set(headers.ETag, etag)
	var notModified = ginCtx.Request.Header.Get(headers.IfNoneMatch) == etag
	if notModified {
		ginCtx.Writer.WriteHeader(304)
	}

	return bundles, nil
}

// Locales is the resolver for the locales field.
func (r *queryResolver) Locales(ctx context.Context, product string, version string) (*model.Locales, error) {
	langTags, _ := l3Service.GetAvailableLocales(ctx, product, version)
	var locales = model.Locales{
		LanguageTags: langTags,
	}
	return &locales, nil
}

// Query returns generated.QueryResolver implementation.
func (r *Resolver) Query() generated.QueryResolver { return &queryResolver{r} }

type queryResolver struct{ *Resolver }
