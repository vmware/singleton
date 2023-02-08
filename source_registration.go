/*
 * Copyright 2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import "github.com/pkg/errors"

var mapSource registeredSource = registeredSource{make(map[releaseID](map[string]ComponentMsgs))}

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
	for k := range componentMap {
		componentNames[i] = k
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

	if msgs, foundComponent := componentMap[component]; !foundComponent {
		return nil, errors.Errorf(errorComponentNonexistent, component)
	} else {
		return msgs, nil
	}
}

func (s *registeredSource) getRelease(name, version string) (map[string]ComponentMsgs, error) {
	if componentMap, found := s.releases[releaseID{name, version}]; !found {
		return nil, errors.Errorf(errorReleaseNonexistent, name, version)
	} else {
		return componentMap, nil
	}
}
