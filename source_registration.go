/*
 * Copyright 2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import "github.com/pkg/errors"

var mapSource = registeredSource{make(map[releaseID](map[string]ComponentMsgs))}

type registeredSource struct {
	releases map[releaseID](map[string]ComponentMsgs)
}

func (s *registeredSource) GetComponentList(name, version string) ([]string, error) {
	componentMap, err := s.getRelease(name, version)
	if err != nil {
		return nil, err
	}

	i := 0
	componentNames := make([]string, len(componentMap))
	for component := range componentMap {
		componentNames[i] = component
		i++
	}

	return componentNames, nil
}

// GetComponentMessages Get component messages
func (s *registeredSource) GetComponentMessages(name, version, component string) (ComponentMsgs, error) {
	componentMap, err := s.getRelease(name, version)
	if err != nil {
		return nil, err
	}

	msgs, foundComponent := componentMap[component]
	if !foundComponent {
		return nil, errors.Errorf(errorComponentNonexistent, component)
	}

	return msgs, nil
}

func (s *registeredSource) getRelease(name, version string) (map[string]ComponentMsgs, error) {
	componentMap, found := s.releases[releaseID{name, version}]
	if !found {
		return nil, errors.Errorf(errorReleaseNonexistent, name, version)
	}

	return componentMap, nil
}
