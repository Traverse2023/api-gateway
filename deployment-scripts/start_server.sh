#! /usr/bin/sh

sudo nohup java -jar /tmp/api-gateway-0.0.1.jar >api_gateway.log 2>&1 &
echo $! > api_gateway_pid.txt