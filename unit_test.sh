# !/usr/bin/sh

make clean && make compiler # Might have to remove it
rm -rf my_output
mkdir my_output

cd classes

# Testing the fibonacci.micro
java -cp ../lib/antlr.jar:. Micro ../input/fibonacci.micro > ../my_output/fibonacci.out

# Testing the loop.micro
java -cp ../lib/antlr.jar:. Micro ../input/loop.micro > ../my_output/loop.out

# Testing the nested.micro
java -cp ../lib/antlr.jar:. Micro ../input/nested.micro > ../my_output/nested.out

# Testing the sqrt.micro
java -cp ../lib/antlr.jar:. Micro ../input/sqrt.micro > ../my_output/sqrt.out

cd ..

# Comparing the Program Outputs with the given outputs

# fibonacci.micro
diff -w my_output/fibonacci.out output/fibonacci.out > diff.out

if ! [[ -s diff.out ]]; then
  #statements
  printf '\nfibonacci.micro is parsed sucessfully\n';
fi

# loop.micro
diff -w my_output/loop.out output/loop.out > diff.out

if ! [[ -s diff.out ]]; then
  printf '\nloop.micro is parsed sucessfully\n';
fi

# nested.micro
diff -w my_output/nested.out output/nested.out > diff.out

if ! [[ -s diff.out ]]; then
  printf '\nnested.micro is parsed sucessfully\n';
fi

# sqrt.micro
diff -w my_output/sqrt.out output/sqrt.out > diff.out

if ! [[ -s diff.out ]]; then
  printf '\nsqrt.micro is parsed sucessfully\n\n';
fi

rm diff.out
