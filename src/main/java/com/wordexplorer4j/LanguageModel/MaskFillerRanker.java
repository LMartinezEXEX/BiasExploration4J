package com.wordexplorer4j.LanguageModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MaskFillerRanker {
    private LanguageModel lm;

    public MaskFillerRanker() {
        this.lm = new LanguageModel();
    }

    public MaskFillerRanker(String modelUrl) {
        this.lm = new LanguageModel(modelUrl);
    }

    public Map<String, Double> compare(String phrase) {
        List<String> modelsWords = lm.getTop5(phrase);
        return compare(phrase, modelsWords);
    }

    public Map<String, Double> compare(String phrase, List<String> words) {
        List<Double> ranks = new ArrayList<>(words.size());
        List<String> phrases = new ArrayList<>(words.size());
        for(String word : words) {
            ranks.add(compare(phrase, word));
            phrases.add(phrase.replace("[MASK]", "<" + word.toUpperCase() + ">"));
        }

        PseudoLikelihood pll = new PseudoLikelihood(ranks);
        return PseudoLikelihood.zipToMap(phrases, pll.getPLLScores());
    }

    private Double compare(String phrase, String word) {
        String maskedPhraseForProcessing = phrase.replace("[MASK]", "<" + word + ">");
        List<String> processedPhrases = lm.processInput(maskedPhraseForProcessing);
        List<String> validTokens = lm.getValidTokens(maskedPhraseForProcessing);
        return lm.rank(processedPhrases, validTokens);
    }
}