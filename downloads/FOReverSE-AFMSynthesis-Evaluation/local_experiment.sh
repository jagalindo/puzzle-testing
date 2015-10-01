#!/bin/sh


# PARAMS : 
# - variable parameter
# - value for fixed parameter 1
# - value for fixed parameter 2
# - enable or group computation?
# - timeout or group computation (will be ignored if or group computation is disabled)
# - number of iterations
# - values for the variable parameter

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
	done

done

