# Singleton service

## Install Go

Follow the [official guidance](https://golang.org/doc/install) to install Golang binary.

## Download code

Clone the project into a directory in your workspace.

Change to the directory.

## Change settings in configuration file

Rename config_template.yaml to config.yaml in 'config' folder.
Change settings according to your environment.

## Build program

Run command to create a build in `builds` directory.

`make build`

## Run Singleton server

`./<build name> --config=<config file>`
