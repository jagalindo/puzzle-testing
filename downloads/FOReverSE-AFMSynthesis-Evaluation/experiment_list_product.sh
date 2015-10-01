#!/bin/sh

# PARAMS : 
# - max number of configurations
# - directory

# Init environment
export PATH=$PATH:/usr/local/sicstus4.3.0/bin/
export PATH=$PATH:/usr/local/scala/bin
export http_proxy=http://proxy:3128/ && export https_proxy=https://proxy:3128/

# Copy files for the experiment in /tmp/afm_experiment
EXPERIMENT_DIR=/home/gbecan/afm-synthesis/FOReverSE-AFMSynthesis-Evaluation
TMP_DIR=/tmp/afm_experiment
#RESULTS_DIR=/data/gbecan_690668/afm-synthesis/results
#LOG=/home/gbecan/afm-synthesis/results/log.txt

mkdir $TMP_DIR
cp -rf $EXPERIMENT_DIR/* $TMP_DIR
cd $TMP_DIR

ls -d $2/* | grep true | xargs -P 0 -n 10 ./list_products.sh $1

# Clean /tmp
rm -rf $TMP_DIR
