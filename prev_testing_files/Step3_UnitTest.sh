# !/bin/sh

# Colors
GREEN='\033[1;32m'
RED='\033[0;31m'
NC='\033[0m'

make clean && make compiler
cd classes

testCases=(1 5 6 7 8 9 11 13 14 16 18 19 20 21)

for i in "${testCases[@]}"
do

  DIFF=$(java -cp ../lib/antlr-4.4-complete.jar:. Micro ../testingStep3Files/inputs/test$i.micro > temp && diff -b -B temp ../testingStep3Files/outputs/test$i.out)
  if [[ "$DIFF" != "" ]]
  then
    printf "\ntest$i.micro ${bold}${RED}FAILED${normal}${NC}\n"
  else
    printf "\ntest$i.micro ${bold}${GREEN}PASSED${normal}${NC}\n"
  fi

  rm temp
done

cd ..

unset testCases
