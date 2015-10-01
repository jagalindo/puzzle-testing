#!/bin/sh

# PARAMS : timeout

for dir in $@
do
    dir=${dir%*/}
	echo $dir

	# Check configuration semantics	
	scala -J-Xmx2g -classpath "jars/lib-afmsynthesis/*:jars/afmsynthesis_2.10-0.1.jar" \
	foreverse.afmsynthesis.ConfigurationSemanticsChecker \
	$dir

done



