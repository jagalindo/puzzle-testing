#!/bin/sh

# PARAMS : 
# - max number of configurations
# - directories


# Perform experiment
for dir in "${@:2}"
do
    dir=${dir%*/}
	echo $dir	
	# Generate products
	java -Xmx12g -jar jars/MatrixGenerator.jar $dir $1

done

