# Building

In a project directory:
```bash
mvn package
```

Compiles, runs unit tests and creates a JAR package. Requires Java 8.

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
 
# Assumptions and decisions

I/O:
- Payments and rates can be optionally read from files. The files are being read asynchronously. 
- If a specified file is not found, the application will stop.
- Payments are always being read (also) from stdin. Typing "quit" stops the application.
- Rates can be optionally read from stdin as the first thing (before payments). Typing "quit" switches to reading payments.
- If some IO error occurs while reading a file, the rest of the file will not be read.
- EOF (Ctrl+D on Linux) has the same meaning as "quit".

Input format:
- If the line is not in the expected format, it is skipped.
- The expected format is CURRENCY SPACE NUMBER, where:
    * CURRENCY are 3 capital letters,
    * SPACE is one or more white-space characters,
    * NUMBER is in the format compatible with Java's BigDecimal (e.g. `-132.456` will work).
- The format for configuring rates is the same as the format for payments, see above.
- Numbers (payment amounts and exchange rates) are read in an "arbitrary" precision.
- The application is case-sensitive.

Output format:
- Numbers are outputted with a maximal precision (no formatting string was provided, so numbers are printed "as they are").

Misc:
- There is no rounding when summing payments or converting to USD.
- If there is some logic error in an exchange rate (e.g. rate <= 0), then the rate is ignored (skipped).
- Rates can be changed in time (only the last one is stored and used).
