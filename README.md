# BookSorter
This is a Java application to sort books by Library of Congress Classifier, Dewey Decimal System, or other methods of classification.

Currently it runs from the command line, but future plans include a GUI.

GUI Branch is currently under development, and is the most current and supported branch, as of now.
After GUI branch is to be merged back to Master, Command Line version (1.0) will be branched and separate.

Currently it exports all book data to a CSV.

Requires an API key from isbndb.com, which requires a free account, but limits to 500 requests daily. API key goes on line 22 in main.java.

Requires GSON for JSON parsing.
