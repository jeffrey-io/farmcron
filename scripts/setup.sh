#!/bin/sh
java8 -cp /home/ec2-user/app.jar farm.bsg.SetupToolCLI --production --bucket state.bsg.farm --region us-west-2 $*
