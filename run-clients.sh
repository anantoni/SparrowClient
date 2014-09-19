#!/bin/bash

classPath="$1"

for (( i = 0; i < 20; i++))
do

    java -jar dist/SparrowClient.jar &

    procs[${i}]=$!

done

for proc in `echo ${procs[@]}`
do
    wait $proc

done
