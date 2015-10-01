#!/bin/sh

# PARAMS : 
# - timeout
# - directories


echo "Compute list of products"
# Perform experiment
for dir in $1/*
do
    dir=${dir%*/}
	echo $dir	
	scala -J-Xmx2g -classpath "jars/lib-afmsynthesis/*:jars/afmsynthesis_2.10-0.1.jar" \
foreverse.afmsynthesis.CorrectMetrics $dir

done

