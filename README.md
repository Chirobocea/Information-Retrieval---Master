# Code Contribution

## Overview

This project is a document search application that supports indexing and searching through various document types. My contribution to this project includes adding support for reading from `.docx`, `.pdf`, and `.txt` files, as well as implementing several preprocessing steps to enhance the search functionality. Additionally, I have integrated fuzzy search to improve the accuracy of search results.

## Features

### Supported File Types

- **.txt**: Plain text files
- **.pdf**: PDF documents
- **.docx**: Microsoft Word documents

### Preprocessing Steps

1. **Lowercase Conversion**: All text is converted to lowercase to ensure case-insensitive search.
2. **Diacritics Removal**: Diacritics are removed from characters to normalize the text.
3. **Punctuation Removal**: Punctuation and special characters are removed from the text.
4. **Stop Words Removal**: Common stop words are removed to focus on meaningful words.
5. **Stemming**: Words are reduced to their root form using a Romanian stemmer.

### Fuzzy Search

Fuzzy search is implemented to handle minor typos and variations in the search query, improving the chances of finding relevant documents.

## Usage

### Build

To build the project, run the following command:

```mvn clean install```

# Indexing Documents
To index documents from a specified directory, use the following command:
```java -jar target/docsearch-1.0-SNAPSHOT.jar -index -directory <path to docs>```

# Searching Documents
To search for a keyword in the indexed documents, use the following command:
```java -jar target/docsearch-1.0-SNAPSHOT.jar -search -query <keyword>```

# Implementation Details

## Reading Files
- The `readTxtFile` method reads content from .txt files.
- The `parsePdfFile` method parses content from .pdf files.
- The `parseDocFile` method parses content from .doc and .docx files.

## Preprocessing
- The `processContent` method orchestrates the preprocessing steps.
- The `normalizeText` method removes diacritics.
- The `removePunctuation` method removes punctuation.
- The `removeStopWords` method removes stop words.
- The `stemWords` method performs stemming.

## Fuzzy Search
The `searchIndex` method implements fuzzy search using Lucene's `QueryParser`.