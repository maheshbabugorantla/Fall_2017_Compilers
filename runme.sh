echo "Running script"

cd classes

java -cp ../lib/antlr.jar:. Micro ../$1 > ../$2