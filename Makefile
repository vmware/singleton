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

BUILD_CMD = go build -tags="${ALL_TAGS}" ${LDFLAGS} -o $(BINARY) "${PKG_PATH}"
TEST_CMD = go test ./tests --config="${config}" -tags="${ALL_TAGS}" -failfast

default: build

all: build

build:
	${BUILD_CMD}

build_all:
	$(foreach GOOS, $(PLATFORMS),\
	$(foreach GOARCH, $(ARCHITECTURES), $(shell export GOOS=$(GOOS); export GOARCH=$(GOARCH); ${BUILD_CMD})))

run:
	go run -tags="${ALL_TAGS}" ${LDFLAGS} "${PKG_PATH}" --config="${config}" --local-bundle.base-path=tests/testdata/bundles

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
	${TEST_CMD} -run=^Bench -bench="${bench}" -benchmem

coverage: Cover := cover.out
coverage: TEMPCover := ${Cover}.temp
coverage:
	${TEST_CMD} -coverprofile=${TEMPCover} -coverpkg=./...
ifeq ($(OS),Windows_NT)
	type ${TEMPCover} | findstr /B /L /V /C:sgtnserver/internal/bindata/bindata.go > ${Cover}
	del ${TEMPCover}
else
	cat ${TEMPCover} | grep -v sgtnserver/internal/bindata/bindata.go > ${Cover}
	rm ${TEMPCover}
endif
	go tool cover -html=${Cover} -o coverage.html
	go tool cover -func ${Cover}

bindata: downloadgo-bindata
	go generate ./...

internal/bindata/bindata.go: downloadgo-bindata
	go generate ./...

downloadgo-bindata:
	go get -u github.com/go-bindata/go-bindata/...

swagger:
	swag init -d api -g v1/swagger/swagger.go --exclude api/v2 -o api/v1/swagger
	swag init -d api -g v2/swagger/swagger.go --exclude api/v1 -o api/v2/swagger

.PHONY: build run test bench coverage bindata downloadgo-bindata swagger build_all

help:
	@echo "make build: build the project"
	@echo "make run: build the project and run"
	@echo "make test: run test cases"
	@echo "make bench: run bench test cases"
	@echo "make bindata: generate bindata without checking source. force to regenerate"
	@echo "make internal/bindata/bindata.go: generate bindata after checking source changed and downloading go-bindata"
	@echo "make swagger: update swagger files"
