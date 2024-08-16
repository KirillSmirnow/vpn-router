#!/usr/bin/env bash

docker compose -p vpn-router -f src/docker-compose.yml up -d --build
