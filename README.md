# logic-table-generator
Java program that reads logic statements to create logic tables

# Definitions
values = Letters that are to be assigned either 1 or 0 (e.g. A, B, C)  
operators = Logical operators that are assigned boolean logic (e.g. or, and, not)  
expression = The logical expression that the user enters at the start of the program  
check values = The values on the left hand side of the table, the actual value of the values that will be put into the expression

# Operators
v = or  
^ = and  
~ = not  
-> = ¬A v B = implies

# Example Expressions
A v B Would give  
`| A | B | Result |`  
`| 0 | 0 | 0 |`  
`| 0 | 1 | 1 |`  
`| 1 | 0 | 1 |`  
`| 1 | 1 | 1 |`  

A ^ B Would give  
`| A | B | Result |`  
`| 0 | 0 | 0 |`  
`| 0 | 1 | 0 |`  
`| 1 | 0 | 0 |`  
`| 1 | 1 | 1 |`  

NOTE: does not support xor (yet)

# Compiling and Running
To compile: `javac @sources.txt`  
To run: `java Generator`


Expressions can be entered as command line arguments when running the program (eg. `java Generator avb`), however, if an implies is in the expression then the argument must be surrounded by quotes (eg. `java Generator "a->b"`)


If a new file is added, add the file to sources.txt. This will mean that the file will be compiled with the above compile command