#!/bin/sh

# PARAMS : 
# - input directory
# - output directory

# Perform experiment
for dir in $1/*
do
    dir=${dir%*/}
	log=$dir/log_products.txt
	if [ -e $log ]; then
		generation_done=`cat $log | grep done`
		if [ $generation_done ]; then
			echo $dir
			mv $dir $2
		fi
	fi

done

