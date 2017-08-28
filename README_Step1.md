# ***Project Step - 1 Scanner***
## ***Due Date: September 6, 2017***

### ***What needs to be done***
The first real step of the building the project is build a Scanner

#### ***What is a Scanner ?***
A scanner is an implementation of a deterministic finite automaton (DFA, Finite State Machine).

Which means that the scanner only loops through the whole code (recursion is not allowed)

Scanner once it parses the code it generates the tokens, and these tokens are input to the Parser to generate the Abstract Syntax Tree. This syntax tree is converted to IR (Intermediate Representation) code which is machine and architecture independent

C++ llvm, Java JVM

#### ***Job of Scanner***

In simple terms, The Job of the scanner is to scan the code written by the user and analyze the lexical abnormalities in the code. Hence, the Scanner can also be called as a Lexical Analyzer (or Lexer)

It's main task is to read the input characters (or code) and produce a sequence of tokens which can be classified as
* Identifiers
* Reserved Words
* Literals, etc

that in turn will be used by the Syntax Analyzer. Scanner is generally implemented as a subroutine, which is utilized by the syntax analyzer as needed i.e. whenever it wants a next token.

Secondary Task is to ignore the whitespaces (such as spaces, tabs and newline character in the source) and comments.Another task is to keep track of the line numbers so meaningful error messages can be generated

### ***Token Definitions***
Different types of tokens are defined using Regular Expressions

For Example
```g4
INTEGERLITERAL  : [0-9]+
FLOATLITERAL    : [0-9]*.[0-9]+
.
.
.
```

```regex
First set of valid strings {"a"} and Second Set of valid strings {"b"}

{"a"} + {"b"} => {"a b"}

Form words with letter from one set and a letter from another statements
{a, b} + {c, d} => {"ac", "ad", "bc", "bd"}

Empty String is defined using lambda

// Collection of strings that contain just 'a' or 'b' or empty string (lambda)
(a | b)* => {lambda, "a", "b", "aa", "ab", "bb", "aba", ...}

// NOT: Give me the set of strings that are not 'a' or 'b'
NOT((a | b)*) // Doubtful

(a|b)+ // Collection of strings that have one or more 'a's or 'b's

// Valid Identifiers in C
(_ | [A-Za-z])( _ |[A-Za-z]|[0-9])*

// Valid Integer in C doesnot start with Zero and can be signed or unsigned
(- | lambda)([1-9][0-9]*| 0) // here (- | lambda) => -? (in RegEx)

// Regex for Comments
[\/\/]

```

Scanner is not responsible for making sure that the input tokens are precise enough to be stored. Its main job is to just identify the patterns and classify a specific detected pattern into a specific token type

### ***How-to-submit-the-code***


### ***Helpful Links***
[ANTLR Lexer with DFA](http://web.mit.edu/dmaze/school/6.824/antlr-2.7.0/doc/lexer.html#dfacompare)
