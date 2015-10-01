#!/bin/sh

# PARAMS : 
# - input matrix
# - output directory
# - enable or group computation
# - or group computation timeout
# - create arbitrary root
# - root name

scala -J-Xmx12g -classpath "jars/lib-afmsynthesis/*:jars/afmsynthesis_2.11-0.1.jar" \
foreverse.afmsynthesis.AFMSynthesis "$@"

