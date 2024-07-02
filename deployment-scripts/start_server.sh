#! /usr/bin/sh

sudo nohup java -jar /home/ec2-user/server/api-gateway-0.0.1.jar > api_gateway.log 2>&1 &
