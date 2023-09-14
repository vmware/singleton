# Copyright 2022-2023 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

bench ?= .
config ?= ${CURDIR}/tests/testdata/config/config.yaml

ALL_TAGS = jsoniter
LDFLAGS = -ldflags "-s -w"
PKG_PATH = ./cmd/singleton

ifdef tags
	ALL_TAGS += ${tags}
endif

PLATFORMS=linux windows
ARCHITECTURES=amd64

GOOS = $(shell go env GOOS)
GOARCH = $(shell go env GOARCH)
BUILDSFOLDER = builds
BINARY = ${BUILDSFOLDER}/singleton-$(GOOS)-$(GOARCH)

BUILD_CMD = go build -o $(BINARY) -tags="${ALL_TAGS}" ${LDFLAGS} "${PKG_PATH}"
TEST_CMD = go test ./tests --config="${config}" -tags="${ALL_TAGS}" -failfast

default: build

all: build

build:
	${BUILD_CMD}

build_all:
	$(foreach GOOS, $(PLATFORMS),\
	$(foreach GOARCH, $(ARCHITECTURES), $(shell export GOOS=$(GOOS); export GOARCH=$(GOARCH); ${BUILD_CMD})))

run:
	go run -tags="${ALL_TAGS}" ${LDFLAGS} "${PKG_PATH}" --config="${config}" --local-bundle.base-path=tests/testdata/bundles --AllowListFile=tests/testdata/allowlist.json

clean:
ifeq ($(OS),Windows_NT)
	@if exist ${BUILDSFOLDER} rd /s /q ${BUILDSFOLDER}
else
	@rm -rf ${BUILDSFOLDER}
endif

test:
ifdef tests
	${TEST_CMD} -v -run="${tests}" -race
else
	${TEST_CMD} -v -race
endif

bench:
	${TEST_CMD} -bench="${bench}" -benchmem --log.Filename= --log.Level=fatal --server.run-mode=test

profile:
	${TEST_CMD} -run=^$$ -bench="${bench}" -benchmem -blockprofile block.out -cpuprofile=cpu.out -memprofile mem.out -mutexprofile mutex.out -trace trace.out --log.Filename= --log.Level=fatal --server.run-mode=test

coverage: Cover := cover.out
coverage: TEMPCover := ${Cover}.temp
coverage:
	${TEST_CMD} -coverprofile=${TEMPCover} -coverpkg=./...
ifeq ($(OS),Windows_NT)
	type ${TEMPCover} | findstr /B /V ^sgtnserver/internal/bindata/.* > ${Cover}
	del ${TEMPCover}
else
	cat ${TEMPCover} | grep -v -e ^sgtnserver/internal/bindata/.* > ${Cover}
	rm ${TEMPCover}
endif
	go tool cover -html=${Cover} -o coverage.html
	go tool cover -func ${Cover}

cldr-bindata: downloadgo-bindata
	go generate assets/cldrdata_generator.go

bindata: downloadgo-bindata
	go generate assets/bindata_generator.go

flag-bindata: downloadgo-bindata
	go generate assets/flagdata_generator.go

downloadgo-bindata:
	go get -u github.com/go-bindata/go-bindata/...
	go install github.com/go-bindata/go-bindata/go-bindata

swagger:
	go install github.com/swaggo/swag/cmd/swag@v1.6.7
	swag init -d api -g v1/swagger/swagger.go --exclude api/v2 -o api/v1/swagger
	swag init -d api -g v2/swagger/swagger.go --exclude api/v1 -o api/v2/swagger

.PHONY: build run test bench profile coverage bindata downloadgo-bindata swagger build_all

help:
	@echo "make build: build the project"
	@echo "make run: build the project and run"
	@echo "make test: run test cases"
	@echo "make bench: run bench test cases"
	@echo "make bindata: generate bindata without checking source. force to regenerate"
	@echo "make internal/bindata/bindata.go: generate bindata after checking source changed and downloading go-bindata"
	@echo "make swagger: update swagger files"
