/*
 * Copyright 2020-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

var cache Cache

// !+cacheService
type cacheService struct {
	messageOrigin
}

func (s *cacheService) Get(item *dataItem) (err error) {
	data, ok := cache.Get(item.id)
	if ok {
		*item = *data.(*dataItem) //TODO: assign data only
		if s.messageOrigin.IsExpired(item) {
			//TODO: don't assign data and run tests
			go s.messageOrigin.Get(&dataItem{id: item.id, data: item.data, attrs: item.attrs})
		}

		return nil
	}

	return s.messageOrigin.Get(item)
	// err = s.origin.Get(item)
	// if err == nil {
	// 	if s.IsExpired(item) {
	// 		cache.Set(item.id, item)
	// 		// cache.Set(item.id, item.data) //TODO
	// 	}
	// } else {
	// 	return errors.New(fmt.Sprintf("Fail to get: %+v", item.id))
	// }

	// return err
}

// !-cacheService

// TODO: maybe this isn't necessary because item is changed directly. Is it safe? Seems item only need to be saved into cache once in  cache service?
// TODO: need to consider creating a new dataitem in dao classes.
type saveToCache struct {
	messageOrigin
}

func (s *saveToCache) Get(item *dataItem) (err error) {
	err = s.messageOrigin.Get(item)
	if err == nil {
		cache.Set(item.id, item)
	}

	return
}
