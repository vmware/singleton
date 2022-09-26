/*
 * Copyright 2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package translationservice

import (
	"context"

	"sgtnserver/modules/translation"

	jsoniter "github.com/json-iterator/go"
)

const (
	pseudoTag = "#@"
)

type PseudoService struct {
	translation.Service
}

// GetMultipleBundles Get translation of multiple bundles
func (ps *PseudoService) GetMultipleBundles(ctx context.Context, name, version, _localeString, componentString string) (*translation.Release, error) {
	releaseData, err := ps.Service.GetMultipleBundles(ctx, name, version, translation.Latest, componentString)
	for i, bundle := range releaseData.Bundles {
		releaseData.Bundles[i] = addPseudoTag(*bundle)
	}
	releaseData.Pseudo = true

	return releaseData, err
}

// GetBundle ...
func (ps *PseudoService) GetBundle(ctx context.Context, id *translation.BundleID) (*translation.Bundle, error) {
	id.Locale = translation.Latest
	bundle, err := ps.Service.GetBundle(ctx, id)
	if err != nil {
		return nil, err
	}

	return addPseudoTag(*bundle), nil
}

// GetStringWithSource ...
func (ps *PseudoService) GetStringWithSource(ctx context.Context, id *translation.MessageID, source string) (map[string]interface{}, error) {
	id.Locale = translation.Latest
	result := map[string]interface{}{
		"productName": id.Name,
		"version":     id.Version,
		"locale":      id.Locale,
		"component":   id.Component,
		"key":         id.Key,
		"source":      source,
		"pseudo":      true}

	if stringMessage, err := ps.GetString(ctx, id); err == nil {
		result["translation"] = pseudoTag + stringMessage.Translation + pseudoTag
		result["status"] = translation.PseudoFound
	} else {
		result["translation"] = "@@" + source + "@@"
		result["status"] = translation.PseudoNotFound
	}

	return result, nil
}

//GetPseudoService ...
func GetPseudoService(service translation.Service) translation.Service {
	return &PseudoService{service}
}

func addPseudoTag(bundle translation.Bundle) *translation.Bundle {
	// store messages into a map
	messages := make(map[string]string)
	bundle.Messages.ToVal(&messages)

	// add pseudo tags
	for key, value := range messages {
		messages[key] = pseudoTag + value + pseudoTag
	}

	// marshal messages
	marshaled, _ := jsoniter.Marshal(messages)
	return &translation.Bundle{ID: bundle.ID, Pseudo: true, Messages: jsoniter.Get(marshaled)}
}
