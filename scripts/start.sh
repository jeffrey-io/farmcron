#!/bin/sh
java8 -jar /home/ec2-user/app.jar --production --bucket state.bsg.farm --region us-west-2 > /dev/null 2> /dev/null < /dev/null &
