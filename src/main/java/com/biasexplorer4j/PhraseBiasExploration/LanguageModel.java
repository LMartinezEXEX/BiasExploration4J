package com.biasexplorer4j.PhraseBiasExploration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ai.djl.MalformedModelException;
import ai.djl.huggingface.tokenizers.Encoding;
import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.Classifications.Classification;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;

public class LanguageModel {
    private ZooModel<String, Classifications> model;
    private HuggingFaceTokenizer tokenizer;
    private Pattern pattern = Pattern.compile("<(.*?)>");
    private static final String BIAS_EXPLORATION_MASK = "[PAD]";

    protected LanguageModel() {
        this("djl://ai.djl.huggingface.pytorch/bert-base-uncased");
    }

    protected LanguageModel(String modelUrl) {
        if (Objects.isNull(modelUrl)) {
            throw new IllegalArgumentException("Model URL string can not be null");
        }

        Criteria<String, Classifications> criteria = Criteria.builder()
                .setTypes(String.class, Classifications.class)
                .optModelUrls(modelUrl)
                .optEngine("PyTorch")
                .build();

        try {
            model = criteria.loadModel();
        } catch (ModelNotFoundException e) {
            throw new IllegalArgumentException("Model not found");
        } catch (MalformedModelException | IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        setTokenizer();
    }

    public double rank(List<String> processedPhrases, List<String> validTokens) {
        if (Objects.isNull(processedPhrases)) {
            throw new IllegalArgumentException("Processed phrases list can not be null");
        } else if (Objects.isNull(validTokens)) {
            throw new IllegalArgumentException("Valid tokens list can not be null");
        } else if (processedPhrases.size() != validTokens.size()) {
            throw new IllegalArgumentException("Different amount of processed phrases (" 
                                                + processedPhrases.size() + ") and valid tokens (" + 
                                                validTokens.size() + ") encountered");
        }

        double rank = 0.0;
        for(int i = 0; i < processedPhrases.size(); ++i) {
            Translator<String, Classifications> translator = getTranslator(validTokens.get(i));
            Predictor<String, Classifications> predictor = model.newPredictor(translator);
            try {
                Classification res = predictor.predict(processedPhrases.get(i)).item(0);
                rank +=  Math.log10(res.getProbability());

            } catch (TranslateException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }

        return rank;
    }

    public List<String> getTop5(String phrase) {
        if (Objects.isNull(phrase)) {
            throw new IllegalArgumentException("Phrase can not be null");
        }

        Translator<String, Classifications> translator = getTranslator();
        Predictor<String, Classifications> predictor = model.newPredictor(translator);

        List<String> topKWords = new ArrayList<>(5);
        try {
            Classifications res = predictor.predict(phrase);
            for(Classification c : res.topK(5)) {
                topKWords.add(c.getClassName());
            }
        } catch (TranslateException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        return topKWords;
    }

    protected List<String> processInput(String phrase) {
        if (Objects.isNull(phrase)) {
            throw new IllegalArgumentException("Phrase can not be null");
        }

        phrase = phrase.replace(",", " , ").replace(".", " . ").replace("?", " ? ").replace("!", " ! ");
        List<String> surroundedWords = getSurroundedWords(phrase);
        String maskedPhrase = maskSurroundedWords(phrase);

        List<String> phrasesToSubmit = new ArrayList<>();
        String[] maskedPhraseTokens = maskedPhrase.split(" ");
        for (int i = 0; i < maskedPhraseTokens.length; ++i) {
            String s = maskedPhraseTokens[i];
            if (s.equals(BIAS_EXPLORATION_MASK)) {
                continue;
            }
            int numberOfParts = tokenizer.encode(s).getTokens().length - 2;
            for (int j = 0; j < numberOfParts; ++j) {
                maskedPhraseTokens[i] = "[MASK]";
                String phraseToSubmit = String.join(" ", maskedPhraseTokens);
                maskedPhraseTokens[i] = s;
                phrasesToSubmit.add(phraseToSubmit);
            }
        }

        replaceSurroundedWordsMasked(phrasesToSubmit, surroundedWords);
        return phrasesToSubmit;
    }

    private List<String> getSurroundedWords(String phrase) {
        Matcher m = pattern.matcher(phrase);

        List<String> surroundedWords = new ArrayList<>();
        while (m.find()) {
            surroundedWords.add(m.group().replace("<", "").replace(">", ""));
        }
        return surroundedWords;
    }

    private String maskSurroundedWords(String phrase) {
        Matcher m = pattern.matcher(phrase);
        return m.replaceAll(BIAS_EXPLORATION_MASK);
    }

    private void replaceSurroundedWordsMasked(List<String> phrases, List<String> wordsToReplace) {
        for (int i = 0; i < phrases.size(); ++i) {
            String[] tokens = phrases.get(i).split(" ");

            int wordToReplaceIdx = 0;
            for (int j = 0; j < tokens.length; ++j) {
                if (tokens[j].equals(BIAS_EXPLORATION_MASK)) {
                    tokens[j] = wordsToReplace.get(wordToReplaceIdx++);
                }
            }
            phrases.set(i, String.join(" ", tokens));
        }
    }

    protected List<String> getValidTokens(String phrase) {
        if (Objects.isNull(phrase)) {
            throw new IllegalArgumentException("Phrase can not be null");
        }

        String maskedPhrase = maskSurroundedWords(phrase);
        return getAndProcessTokens(maskedPhrase);
    }

    private List<String> getAndProcessTokens(String phrase) {
        Encoding encoding = tokenizer.encode(phrase);
        List<String> tokens = new ArrayList<>(Arrays.asList(encoding.getTokens()));
        tokens.removeAll(Arrays.asList("[CLS]", "[SEP]", BIAS_EXPLORATION_MASK));
        return tokens;
    }

    private void setTokenizer() {
        try {
            this.tokenizer = HuggingFaceTokenizer.builder()
                                .optTokenizerPath(model.getModelPath())
                                .optManager(model.getNDManager())
                                .build();
        } catch (IOException e) {
            System.out.println("Error during tokenizer building");
            e.printStackTrace();
        }
    }

    private Translator<String, Classifications> getTranslator() {
        try {
            return PredictTokenTranslator.builder(tokenizer).build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Translator<String, Classifications> getTranslator(String tokenToPredict) {
        try {
            return PredictTokenTranslator.builder(tokenizer)
                                            .optTokenToPredict(tokenToPredict)
                                            .build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
