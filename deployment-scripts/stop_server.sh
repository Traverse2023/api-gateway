#! /usr/bin/sh

sudo kill -9 'cat api-gateway_pid.txt' || echo api-gateway proccess not currently running...continuing...
sudo rm api-gateway_pid.txt || true

