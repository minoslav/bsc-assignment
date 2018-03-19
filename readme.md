# Building

In a project directory:
```bash
mvn package
```

Compiles, runs unit tests and creates a JAR package.

# Running

In a project directory:
```bash
java -cp target/bsc-assignment-1.0-SNAPSHOT.jar sk.jancar.bsc.app.App
```

This command runs the application in a mode that reads payment data from the standard input (stdin).

Optional switches:
- `-r=filename1` : read also (asynchronously) exchange rates (for a conversion to USD) from a file named `filename1`
- `-p=filename2` : read also (asynchronously) payments from a file named `filename2`
- `-rs` : read also exchange rates from the standard input

When `-rs` switch is supplied, *first*, *exchange rates will be read* from stdin and, subsequently (after typing `quit`), payments will be read from stdin.
 
# Assumptions