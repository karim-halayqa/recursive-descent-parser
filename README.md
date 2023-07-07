Recursive descent parser which takes a program in a file input and outputs any syntax errors according to the provided production rules.
The production rules:

project-declaration -> project-def     "."

project-def → project-heading declarations compound-stmt

project-heading → project "name" ";"

declarations → const-decl var-decl subroutine-decl

const-decl → const (const-item ";")+ | λ

const-item → "name" "=" "integer-value"

var-decl → var (var-item ";")+ | λ

var-item → name-list ":" int

name-list → "name" ("," "name")*

subroutine-decl → subroutine-heading declarations compound-stmt ";" | λ

subroutine-heading → routine "name" ";"

compound-stmt → start stmt-list end

stmt-list → (statement ";")*

statement → ass-stmt | inout-stmt | if-stmt | loop-stmt | λ

ass-stmt → "name" ":=" arith-exp

arith-exp → term (add-sign term)*

term → factor (mul-sign factor)*

factor → "(" arith-exp ")" | name-value

name-value → "name" | "integer-value"

add-sign → "+" | "-"

mul-sign → "*" | "/" | "%"

inout-stmt → input "(" "name" ")" | output "(" name-value ")"

if-stmt → if "(" bool-exp ")" then statement else-part endif

else-part → else statement | λ

loop-stmt → loop "(" bool-exp ")" do statement

bool-exp → name-value relational-oper name-value

relational-oper → "=" | "<>" | "<" | "<=" | ">" | ">="

If the parser encounters a syntax error, it immediately throws a runtime error with the token number as well as what's wrong.
This is a project for COMP439.
