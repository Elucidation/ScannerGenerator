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