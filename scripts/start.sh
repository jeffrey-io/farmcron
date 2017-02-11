#!/bin/sh
java8 -jar /home/ec2-user/app.jar --production > /dev/null 2> /dev/null < /dev/null &
