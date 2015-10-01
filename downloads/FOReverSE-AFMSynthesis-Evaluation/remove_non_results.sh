#!/bin/sh

# PARAMS : 
# - max number of configurations
# - directories

# Perform experiment
for dir in $1/*
do
    dir=${dir%*/}
	ok=`find $dir -name metrics.csv`
	if [ ! $ok ]; then
		echo $dir	
		rm -r $dir
	fi


done

