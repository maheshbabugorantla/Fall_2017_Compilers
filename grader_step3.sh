#!/bin/bash
grade=0
export CLASSPATH here
RESULTFOLDER=$(cat /dev/urandom | tr -dc 'a-zA-Z0-9' | fold -w 32 | head -n 1)
#first call make compiler
make clean
chmod +x runme
make compiler

if [ $? = "0" ]; then
    #now run their script on every test file
    rm -rf ./test/$RESULTFOLDER
    mkdir ./test/$RESULTFOLDER
    INPUT=./test/input
    RESULT=./test/$RESULTFOLDER
    OUTPUT=./test/output
    for file in `ls $INPUT`
    do
        echo "Testing....."$file
        ./runme $INPUT/$file $RESULT/${file%.micro}.out
        if [ $? = "0" ]; then
            CHECK=$(diff -b -w $OUTPUT/${file%.micro}.out $RESULT/${file%.micro}.out | grep -v ^@ | wc -l)
            if [ $CHECK = "0" ]; then 
                    echo "Test Passed"
                    let "grade+=7"
            else
                    echo "Test Failed. Number different lines: $CHECK"
            fi
        else
            echo "./runme $INPUT/$file $RESULT/${file%.micro}.out failed to run"
        fi
    done
else
    echo "Make compiler doesn't work when running from the main folder"
fi
if [ $grade == 98 ] ; then
    let "grade+=2" 
fi 
echo $grade > grade-tmp.txt
