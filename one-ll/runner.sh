#!/bin/bash


folder=0
while [ 1 ];
do
mkdir -p logs
mkdir -p tests
sbt "run-main ru.niner.oneplusll.Runner -d 2 -i 2"
mkdir -p "results/$folder"
mv logs tests "results/$folder/"
folder=$((folder + 1))
done
