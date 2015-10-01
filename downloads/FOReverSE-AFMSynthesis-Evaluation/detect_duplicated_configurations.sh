#!/bin/sh

# PARAMS : 
# - directory

# Perform experiment
for dir in $@
do
    dir=${dir%*/}
	matrix=$dir/input_matrix.csv	
	nb_rows=`cat $matrix | wc -l`
	nb_uniq_rows=`cat $matrix | uniq | wc -l`
	if [ $nb_rows != $nb_uniq_rows ]; then
		echo "not ok...$dir"
	fi
done

