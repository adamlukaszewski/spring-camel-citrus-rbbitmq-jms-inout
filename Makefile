.PHONY: app-package app-start-local-infrastructure

app-start-local-infrastructure:
	cd ./local && docker-compose up --remove-orphans
