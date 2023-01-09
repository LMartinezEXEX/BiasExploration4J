package com.wordexplorer4j;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import ai.djl.modality.nlp.DefaultVocabulary;
import ai.djl.modality.nlp.Vocabulary;
import ai.djl.modality.nlp.bert.BertToken;
import ai.djl.modality.nlp.bert.BertTokenizer;

public class Prueba {
    private List<String> tokens;
    private Vocabulary vocabulary;
    private BertTokenizer tokenizer;
    
    public void prepare() throws IOException {
        Path path = Paths.get("demo/src/main/java/com/wordexplorer4j/data/bert-base-uncased-vocab.txt");
        vocabulary = DefaultVocabulary.builder()
                        .optMinFrequency(1)
                        .addFromTextFile(path)
                        .optUnknownToken("[UNK]")
                        .build();
        tokenizer = new BertTokenizer();
    }

    public void process(String sentence) {
        BertToken token = 
            tokenizer.encode(sentence.toLowerCase(), "");

        tokens = token.getTokens();

        tokens.stream().mapToLong(vocabulary::getIndex).forEach(System.out::println);
    }
}
