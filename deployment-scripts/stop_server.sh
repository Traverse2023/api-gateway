#! /usr/bin/sh

sudo kill -9 'cat api-gateway_pid.txt'
sudo rm api-gateway_pid.txt || echo api-gateway proccess not currently running...continuing...