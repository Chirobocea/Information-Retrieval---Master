package org.example;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
// import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
// import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
// import org.apache.lucene.analysis.ro.RomanianAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.snowball.SnowballFilter;
// import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
// import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
// import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
// import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
// import org.apache.lucene.search.BooleanClause;
// import org.apache.lucene.search.BooleanQuery;
// import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
// import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.parser.microsoft.ooxml.OOXMLParser;
// import org.apache.tika.parser.txt.TXTParser;
import org.apache.tika.sax.BodyContentHandler;
import org.tartarus.snowball.ext.RomanianStemmer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.text.Normalizer;
// import java.util.ArrayList;
import java.util.Arrays;
// import java.util.HashMap;
import java.util.List;
// import java.util.Map;
import java.util.stream.Collectors;

import java.util.logging.Logger;
import java.util.logging.Level;

public class Main {

    private static final String INDEX_DIRECTORY = "index";

    private static final List<String> STOP_WORDS = Arrays.asList("a", "abia", "acea", "aceasta", "aceasta", "aceea", "aceeasi", "acei", "aceia", "acel", "acela", "acelasi", "acele", "acelea", "acest", "acesta", "aceste", "acestea", "acestei", "acestia", "acestui", "acesti", "acestia", "acolo", "acord", "acum", "adica", "ai", "aia", "aiba", "aici", "aiurea", "al", "ala", "alaturi", "ale", "alea", "alt", "alta", "altceva", "altcineva", "alte", "altfel", "alti", "altii", "altul", "am", "anume", "apoi", "ar", "are", "as", "asa", "asemenea", "asta", "astazi", "astea", "astfel", "astazi", "asupra", "atare", "atat", "atata", "atatea", "atatia", "ati", "atit", "atita", "atitea", "atitia", "atunci", "au", "avea", "avem", "aveti", "avut", "azi", "as", "asadar", "ati", "b", "ba", "bine", "bucur", "buna", "c", "ca", "cam", "cand", "capat", "care", "careia", "carora", "caruia", "cat", "catre", "caut", "ce", "cea", "ceea", "cei", "ceilalti", "cel", "cele", "celor", "ceva", "chiar", "ci", "cinci", "cind", "cine", "cineva", "cit", "cita", "cite", "citeva", "citi", "citiva", "conform", "contra", "cu", "cui", "cum", "cumva", "curand", "curind", "cand", "cat", "cate", "catva", "cati", "cind", "cit", "cite", "citva", "citi", "ca", "caci", "carei", "caror", "carui", "catre", "d", "da", "daca", "daca", "dar", "dat", "datorita", "data", "dau", "de", "deasupra", "deci", "decit", "degraba", "deja", "deoarece", "departe", "desi", "despre", "desi", "din", "dinaintea", "dintr", "dintr-", "dintre", "doar", "doi", "doilea", "doua", "drept", "dupa", "dupa", "da", "e", "ea", "ei", "el", "ele", "era", "eram", "este", "eu", "exact", "esti", "f", "face", "fara", "fata", "fel", "fi", "fie", "fiecare", "fii", "fim", "fiu", "fiti", "foarte", "fost", "frumos", "fara", "g", "geaba", "gratie", "h", "halba", "i", "ia", "iar", "ieri", "ii", "il", "imi", "in", "inainte", "inapoi", "inca", "incit", "insa", "intr", "intre", "isi", "iti", "j", "k", "l", "la", "le", "li", "lor", "lui", "langa", "linga", "m", "ma", "mai", "mare", "mea", "mei", "mele", "mereu", "meu", "mi", "mie", "mine", "mod", "mult", "multa", "multe", "multi", "multa", "multi", "multumesc", "maine", "miine", "ma", "n", "ne", "nevoie", "ni", "nici", "niciodata", "nicaieri", "nimeni", "nimeri", "nimic", "niste", "niste", "noastre", "noastra", "noi", "noroc", "nostri", "nostru", "nou", "noua", "noua", "nostri", "nu", "numai", "o", "opt", "or", "ori", "oricare", "orice", "oricine", "oricum", "oricand", "oricat", "oricind", "oricit", "oriunde", "p", "pai", "parca", "patra", "patru", "patrulea", "pe", "pentru", "peste", "pic", "pina", "plus", "poate", "pot", "prea", "prima", "primul", "prin", "printr-", "putini", "putin", "putina", "putina", "pana", "pina", "r", "rog", "s", "sa", "sa-mi", "sa-ti", "sai", "sale", "sau", "se", "si", "sint", "sintem", "spate", "spre", "sub", "sunt", "suntem", "sunteti", "sus", "suta", "sint", "sintem", "sinteti", "sa", "sai", "sau", "t", "ta", "tale", "te", "ti", "timp", "tine", "toata", "toate", "toata", "tocmai", "tot", "toti", "totul", "totusi", "totusi", "toti", "trei", "treia", "treilea", "tu", "tuturor", "tai", "tau", "u", "ul", "ului", "un", "una", "unde", "undeva", "unei", "uneia", "unele", "uneori", "unii", "unor", "unora", "unu", "unui", "unuia", "unul", "v", "va", "vi", "voastre", "voastra", "voi", "vom", "vor", "vostru", "voua", "vostri", "vreme", "vreo", "vreun", "va", "x", "z", "zece", "zero", "zi", "zice", "ii", "il", "imi", "impotriva", "in", "inainte", "inaintea", "incotro", "incat", "incit", "intre", "intrucat", "intrucit", "iti", "ala", "alea", "asta", "astea", "astia", "sapte", "sase", "si", "stiu", "ti", "tie"); 
    private static final CharArraySet STOP_WORDS_SET = new CharArraySet(STOP_WORDS, true);

    public static void main(String[] args) throws IOException, ParseException {

        Logger.getLogger("org.apache.lucene.store.MMapDirectory").setLevel(Level.SEVERE);
        // System.setProperty("org.apache.lucene.mmap.disableWarning", "true");

        if (args.length < 2) {
            System.out.println("Usage:");
            System.out.println("  -index -directory <path to docs>");
            System.out.println("  -search -query <keyword>");
            return;
        }

        String command = args[0];
        String option = args[1];

        WhitespaceAnalyzer analyzer = new WhitespaceAnalyzer();
        Directory index = FSDirectory.open(Paths.get(INDEX_DIRECTORY));

        if (command.equals("-index") && option.equals("-directory")) {
            String docsPath = args[2];
            indexDocuments(analyzer, index, docsPath);
        } else if (command.equals("-search") && option.equals("-query")) {
            String querystr = args[2];
            searchIndex(analyzer, index, querystr);
            // String filepath = args[2];
            // File file = new File(filepath);
            // String content = readTxtFile(file);
            // searchIndex(analyzer, index, content);
        } else {
            System.out.println("Invalid arguments");
        }
    }

    private static void indexDocuments(Analyzer analyzer, Directory index, String docsPath) throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(index, config);
    
        File folder = new File(docsPath);
        if (folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                // System.out.println("Found file: " + file.getName());
                if (file.isFile() && isSupportedFileType(file)) {
                    indexFile(writer, file);
                } else {
                    System.out.println("Ignoring unsupported file type: " + file.getName());
                }
            }
        } else if (folder.isFile() && isSupportedFileType(folder)) {
            indexFile(writer, folder);
        } else {
            System.out.println("Provided path is not a directory or supported file.");
        }
    
        writer.close();
        // System.out.println("Indexing completed.");
        // printIndexContents(index);
    }

    // Reads content from a .txt file
    private static String readTxtFile(File file) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            System.err.println("Error reading .txt file " + file.getName() + ": " + e.getMessage());
        }
        return content.toString().trim();
    }

    // Parses content from a .pdf file
    private static String parsePdfFile(File file) {
        String parsedContent = null;
        try (FileInputStream inputStream = new FileInputStream(file)) {
            BodyContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            ParseContext context = new ParseContext();
            new PDFParser().parse(inputStream, handler, metadata, context);
            parsedContent = handler.toString();
        } catch (Exception e) {
            System.err.println("Error parsing .pdf file " + file.getName() + ": " + e.getMessage());
        }
        return parsedContent != null ? parsedContent.trim() : "";
    }

    // Parses content from .doc or .docx files
    private static String parseDocFile(File file) {
        String parsedContent = null;
        try (FileInputStream inputStream = new FileInputStream(file)) {
            BodyContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            ParseContext context = new ParseContext();
            new OOXMLParser().parse(inputStream, handler, metadata, context);
            parsedContent = handler.toString();
        } catch (Exception e) {
            System.err.println("Error parsing .doc or .docx file " + file.getName() + ": " + e.getMessage());
        }
        return parsedContent != null ? parsedContent.trim() : "";
    }

    private static void indexFile(IndexWriter writer, File file) throws IOException {
        if (!file.exists()) {
            System.err.println("File does not exist: " + file.getAbsolutePath());
            return;
        }
    
        String extension = getFileExtension(file);
        String content = null;
    
        // Read content based on file type
        switch (extension) {
            case "txt":
                content = readTxtFile(file);
                break;
            case "pdf":
                content = parsePdfFile(file);
                break;
            case "doc":
            case "docx":
                content = parseDocFile(file);
                break;
            default:
                System.out.println("Unsupported file type: " + file.getName());
                return;
        }
    
        if (content != null && !content.isEmpty()) {
            content = processContent(content);
            Document doc = new Document();
            doc.add(new TextField("contents", content, Field.Store.YES));
            doc.add(new TextField("filename", file.getName(), Field.Store.YES));
    
            writer.addDocument(doc);
            // System.out.println("Indexed file: " + file.getName());
            // System.out.println("Content length: " + content.length());
        } else {
            System.out.println("No content found in file: " + file.getName());
        }
    }
    

    private static String processContent(String content) {
        String processed = content;
        processed = processed.toLowerCase();
        processed = normalizeText(processed);
        processed = removePunctuation(processed);
        processed = stemWords(processed);
        processed = removeStopWords(processed);
        return processed;
    }

    private static String normalizeText(String text) {
        // Remove diacritics
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD)
            .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        // System.out.println("After normalization: " + normalized.substring(0, Math.min(50, normalized.length())));
        return normalized;
    }

    private static String removeStopWords(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "";
        }
        
        // Remove Romanian stop words using CharArraySet
        String[] words = text.split("\\s+");
        List<String> filteredWords = Arrays.stream(words)
            .filter(word -> !STOP_WORDS_SET.contains(word)) // CharArraySet is case-sensitive
            .collect(Collectors.toList());
            
        String filtered = String.join(" ", filteredWords);
        
        // if (!filtered.isEmpty()) {
        //     System.out.println("After stop words removal: " + filtered.substring(0, Math.min(50, filtered.length())));
        // }
        
        return filtered;
    }

    private static String stemWords(String text) {
        StringBuilder stemmed = new StringBuilder();
        try {
            // Split the text by white spaces
            String[] words = text.split("\\s+");
            
            for (String word : words) {
                // Create a token stream from the word
                WhitespaceTokenizer tokenizer = new WhitespaceTokenizer();
                tokenizer.setReader(new StringReader(word));
                
                // Create snowball filter with Romanian stemmer
                TokenStream tokenStream = new SnowballFilter(tokenizer, new RomanianStemmer());
                
                CharTermAttribute termAttr = tokenStream.addAttribute(CharTermAttribute.class);
                tokenStream.reset();
                
                if (tokenStream.incrementToken()) {
                    stemmed.append(termAttr.toString()).append(" ");
                }
                
                tokenStream.end();
                tokenStream.close();
            }
        } catch (IOException e) {
            System.err.println("Error during stemming: " + e.getMessage());
        }
        
        // System.out.println("After stemming: " + stemmed.substring(0, Math.min(50, stemmed.length())));
        return stemmed.toString().trim();
    }

    private static String removePunctuation(String text) {
        // Remove punctuation and special characters
        String cleaned = text.replaceAll("[^a-zA-Z0-9\\s]", "");
        // System.out.println("After punctuation removal: " + cleaned.substring(0, Math.min(50, cleaned.length())));
        return cleaned;
    }

    private static boolean isSupportedFileType(File file) {
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".txt") || fileName.endsWith(".pdf") || fileName.endsWith(".docx") || fileName.endsWith(".doc");
    }

    // private static void printIndexContents(Directory index) throws IOException {
    //     try (DirectoryReader reader = DirectoryReader.open(index)) {
    //         System.out.println("\n=== INDEX CONTENTS ===");
    //         System.out.println("Total documents: " + reader.numDocs());
    
    //         for (int docId = 0; docId < reader.maxDoc(); docId++) {
    //             Document doc = reader.document(docId);
    //             String contents = doc.get("contents"); // Get stored content
                
    //             System.out.println("\nDocument #" + docId);
    //             System.out.println("Filename: " + doc.get("filename"));
    //             if (contents != null && !contents.isEmpty()) {
    //                 System.out.println("Content preview: " + contents.substring(0, Math.min(100, contents.length())) + "...");
    //             } else {
    //                 System.out.println("Warning: No content found!");
    //             }
    //         }
    //     }
    // }

    private static void searchIndex(Analyzer analyzer, Directory index, String querystr) throws IOException, ParseException {
        // System.setProperty("org.apache.lucene.mmap.disableWarning", "true");

        String processedQuery = processContent(querystr);
        
        if (processedQuery == null || processedQuery.trim().isEmpty()) {
            // System.out.println("No valid query provided.");
            return;
        }
        
        // Use QueryParser with fuzzy matching enabled
        QueryParser parser = new QueryParser("contents", analyzer);
        parser.setAllowLeadingWildcard(true);
        parser.setFuzzyMinSim(0.9f); // Set fuzzy similarity threshold
        
        // Add fuzzy operator to query terms
        String fuzzyQuery = processedQuery.replaceAll("\\b(\\w+)\\b", "$1~1");
        Query q = parser.parse(fuzzyQuery);
        
        // Search
        IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(index));
        TopDocs docs = searcher.search(q, 5);
        ScoreDoc[] hits = docs.scoreDocs;
    
        // System.out.println("Found " + hits.length + " hits.");
        for (int i = 0; i < hits.length; i++) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            System.out.println(d.get("filename"));
            // System.out.println("Score: " + hits[i].score);
        }
    }

    private static String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOfDot = name.lastIndexOf('.');
        if (lastIndexOfDot == -1) {
            return ""; // no extension
        }
        return name.substring(lastIndexOfDot + 1).toLowerCase();
    }
}