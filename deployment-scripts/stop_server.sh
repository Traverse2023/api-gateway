#! /usr/bin/sh

sudo kill -9 'cat api-gateway_pid.txt' || true
sudo rm api-gateway_pid.txt || true

