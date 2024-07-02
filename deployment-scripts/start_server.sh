#!/bin/sh

source /etc/profile
sudo touch /api_gateway.log
sudo chmod 777 /api_gateway.log
nohup java -jar /home/ec2-user/server/api-gateway-0.0.1.jar >> /api_gateway.log 2>&1 &
echo $!



