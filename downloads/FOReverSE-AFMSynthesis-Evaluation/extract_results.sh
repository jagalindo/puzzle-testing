#!/bin/sh

# PARAMS : 
# - input directory
# - output directory

# Perform experiment
mkdir $2
for dir in $1/*
do
    dir=${dir%*/}
	echo $dir
	dirname=`basename $dir`
	output_dir=$2/$dirname
	mkdir $output_dir
	cp $dir/metrics.csv $output_dir
	cp $dir/input_matrix.csv $output_dir
	cp $dir/synthesized_afm.afm $output_dir
done

