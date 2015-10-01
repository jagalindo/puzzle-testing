#!/bin/sh


# PARAMS : 
# - enable or group computation?
# - timeout or group computation (will be ignored if or group computation is disabled)
# - create arbitrary root
# - root name

# Init environment
export PATH=$PATH:/usr/local/sicstus4.3.0/bin/
export PATH=$PATH:/usr/local/scala-2.11.1/bin
export http_proxy=http://proxy:3128/ && export https_proxy=https://proxy:3128/

# Copy files for the experiment in /tmp/afm_experiment
EXPERIMENT_DIR=/home/gbecan/afm-synthesis/FOReverSE-AFMSynthesis-Evaluation
DATASET_DIR=/home/gbecan/afm-synthesis/dataset
TMP_DIR=/tmp/afm_experiment
RESULTS_DIR=/home/gbecan/afm-synthesis/results
LOG=/home/gbecan/afm-synthesis/results/log.txt


mkdir $TMP_DIR
cp -rf $EXPERIMENT_DIR/* $TMP_DIR
cp -rf $DATASET_DIR $TMP_DIR
cd $TMP_DIR

# Perform experiment

for filename in $TMP_DIR/dataset/*
do
	start=$(( $1 * 10 ))
	end=$(( ($1 + 1) * 10 ))
	for (( i=$start; i<$end; i++ ))
	do
		echo $filename
		basefilename=$(basename "$filename")
		#extension="${basefilename##*.}"
		outdir="${basefilename%.*}_$i"

		./simple_synthesis.sh "$filename" "$TMP_DIR/results/$outdir" ${@:2}

		# Save results
		rm -f $TMP_DIR/results/*/sicstus_output.txt
		rm -f $TMP_DIR/results/*/converted_matrix.csv
		cp -r $TMP_DIR/results/* $RESULTS_DIR
		rm -rf $TMP_DIR/results/*
	done

done

# Clean /tmp
rm -rf $TMP_DIR

