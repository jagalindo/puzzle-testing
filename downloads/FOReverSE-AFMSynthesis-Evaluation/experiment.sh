#!/bin/sh


# PARAMS : 
# - variable parameter
# - value for fixed parameter 1
# - value for fixed parameter 2
# - enable or group computation?
# - timeout or group computation (will be ignored if or group computation is disabled)
# - number of iterations
# - values for the variable parameter

# Init environment
export PATH=$PATH:/usr/local/sicstus4.3.0/bin/
export PATH=$PATH:/usr/local/scala-2.11.1/bin
export http_proxy=http://proxy:3128/ && export https_proxy=https://proxy:3128/

# Copy files for the experiment in /tmp/afm_experiment
EXPERIMENT_DIR=/home/gbecan/afm-synthesis/FOReverSE-AFMSynthesis-Evaluation
TMP_DIR=/tmp/afm_experiment
RESULTS_DIR=/home/gbecan/afm-synthesis/results
LOG=/home/gbecan/afm-synthesis/results/log.txt


mkdir $TMP_DIR
cp -rf $EXPERIMENT_DIR/* $TMP_DIR
cd $TMP_DIR

# Perform experiment

for v in ${@:7}
do 
	for i in $(seq 1 $6); do 
		echo "Synthesis $v - $i"
		if [ $1 = "f" ]; then
			./synthesis.sh $v $2 $3 $4 $5
		fi

		if [ $1 = "c" ]; then
			./synthesis.sh $2 $v $3 $4 $5
		fi

		if [ $1 = "d" ]; then
			./synthesis.sh $2 $3 $v $4 $5
		fi
	
		# Save results
		rm -f $TMP_DIR/results/*/sicstus_output.txt
		rm -f $TMP_DIR/results/*/converted_matrix.csv
		cp -r $TMP_DIR/results/* $RESULTS_DIR
		rm -rf $TMP_DIR/results/*
	done

done

# Clean /tmp
rm -rf $TMP_DIR

