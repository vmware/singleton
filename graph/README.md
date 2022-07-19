# GraphQL API in Singleton Service

## Run from source code

### Install Go

Follow the [official guidance](https://golang.org/doc/install) to install Golang binary.

### Download code

Clone the project into a directory in your workspace.

Change to the directory.

### Change settings in configuration file

Configure values in config.yaml in 'config' folder.
Change settings according to your environment.

### Run Singleton service from main
`go run cmd/singleton//main.go`

### Run Singleton service using build
Run `make build` to create a build in `builds` directory.
Run `./<build name> --config=<config file>` to start the service.

## Modify GraphQL API

See https://gqlgen.com/getting-started/ for reference

### Updating schema
Update schema in graph/schema.graphqls file.
Run `go run github.com/99designs/gqlgen generate` to automatically generate/update resolver function signatures in the graph/schema.resolvers.go file.

### Updating resolvers
Update resolver function implementation in graph/schema.resolvers.go file as needed.
Build and run the service per instructions above.

