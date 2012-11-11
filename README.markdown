Sam's Branch

Usage
---
Run Main.java or ScannerGenerator.jar package like so:

	ScannerGenerator <SPECIFICATION_FILE> <INPUT_FILE> [<OUTPUT_FILE>]

If no output file is provided it appends `_Output.txt` to the input file and uses that as filename.

File Structure
---

* Main.java - Main driver for system
* DFATable.java - The specialized HashMap that acts as our DFA Table
* ScannerGenerator.java - provides static function to generate a DFA Table from specification file
* TableWalker.java - Pumps out tokens using the DFA Table while being provided a stream of input chars

Helpers:

* State.java
* StateCharacter.java
* Token.java

Character Class Parsing
---
Input Spec File has:

	$DIGIT [0-9]
	$NON-ZERO [^0] IN $DIGIT
	$CHAR [a-zA-Z]
	$UPPER [^a-z] IN $CHAR
	$LOWER [^A-Z] IN $CHAR

Results in a HashMap< String, HashSet<Character> > as shown below:

	$UPPER(26) : [D, E, F, G, A, B, C, L, M, N, O, H, I, J, K, U, T, W, V, Q, P, S, R, Y, X, Z]
	$DIGIT(10) : [3, 2, 1, 0, 7, 6, 5, 4, 9, 8]
	$CHAR(52) : [D, E, F, G, A, B, C, L, M, N, O, H, I, J, K, U, T, W, V, Q, P, S, R, Y, X, Z, f, g, d, e, b, c, a, n, o, l, m, j, k, h, i, w, v, u, t, s, r, q, p, z, y, x]
	$LOWER(26) : [f, g, d, e, b, c, a, n, o, l, m, j, k, h, i, w, v, u, t, s, r, q, p, z, y, x]
	$NON-ZERO(9) : [3, 2, 1, 7, 6, 5, 4, 9, 8]

Since we're using a HashSet to store the data there is no order, however we get O(1) in/out ops which is all we'd ever use when checking via DFA Table.

Identifier Parsing
---

When doing ```Parsing Identifier: $FLOAT ($DIGIT)+ \. ($DIGIT)+```, the value ```($DIGIT)+\.($DIGIT)+``` is tokenized like so

	L_PAREN
	CHARCLASS
	R_PAREN
	ONE_OR_MORE
	SPECIAL_CHAR
	L_PAREN
	CHARCLASS
	R_PAREN
	ONE_OR_MORE


The logic is as follows (spaces/tabs ignored, <EPS> stands for nothing):

	<expr>   = <term> '|' <expr>  |  <term> | <term> <expr>
	<term>   = <factor>
	<factor> = <base> <count>
	<count>  = '*' | '+' | <EPS>
	<base>   = <char> |  '\' <char>   |  '(' <expr> ')'  

So a set of runs:

	Trying to Recursively Parse '$LOWER($LOWER|$DIGIT)*'...
	EXPR
	TERM
	FACTOR
	BASE
	 MATCH: $LOWER
	TERM
	FACTOR
	BASE
	 MATCH: (
	EXPR
	TERM
	FACTOR
	BASE
	 MATCH: $LOWER
	 MATCH: |
	EXPR
	TERM
	FACTOR
	BASE
	 MATCH: $DIGIT
	 MATCH: )
	ZERO OR MORE
	 MATCH: *
	Finished Recursive Parse.

	Trying to Recursively Parse '($DIGIT)+'...
	EXPR
	TERM
	FACTOR
	BASE
	 MATCH: (
	EXPR
	TERM
	FACTOR
	BASE
	 MATCH: $DIGIT
	 MATCH: )
	ONE OR MORE
	 MATCH: +
	Finished Recursive Parse.

	Trying to Recursively Parse '($DIGIT)+\.($DIGIT)+'...
	EXPR
	TERM
	FACTOR
	BASE
	 MATCH: (
	EXPR
	TERM
	FACTOR
	BASE
	 MATCH: $DIGIT
	 MATCH: )
	ONE OR MORE
	 MATCH: +
	TERM
	FACTOR
	BASE
	 MATCH: \.
	Finished Recursive Parse.

	Trying to Recursively Parse '='...
	EXPR
	TERM
	FACTOR
	BASE
	 MATCH: =
	Finished Recursive Parse.

	Trying to Recursively Parse '\+'...
	EXPR
	TERM
	FACTOR
	BASE
	 MATCH: \+
	Finished Recursive Parse.

	Trying to Recursively Parse '-'...
	EXPR
	TERM
	FACTOR
	BASE
	 MATCH: -
	Finished Recursive Parse.

	Trying to Recursively Parse '\*'...
	EXPR
	TERM
	FACTOR
	BASE
	 MATCH: \*
	Finished Recursive Parse.

	Trying to Recursively Parse 'PRINT'...
	EXPR
	TERM
	FACTOR
	BASE
	 MATCH: P
	EXPR
	TERM
	FACTOR
	BASE
	 MATCH: R
	EXPR
	TERM
	FACTOR
	BASE
	 MATCH: I
	EXPR
	TERM
	FACTOR
	BASE
	 MATCH: N
	EXPR
	TERM
	FACTOR
	BASE
	 MATCH: T
	Finished Recursive Parse.
