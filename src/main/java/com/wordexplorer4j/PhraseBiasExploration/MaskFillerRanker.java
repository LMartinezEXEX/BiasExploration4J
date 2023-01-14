package com.wordexplorer4j.PhraseBiasExploration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.hadoop.shaded.org.apache.commons.lang3.StringUtils;

public class MaskFillerRanker {
    private LanguageModel lm;

    public MaskFillerRanker() {
        this.lm = new LanguageModel();
    }

    public MaskFillerRanker(String modelUrl) {
        this.lm = new LanguageModel(modelUrl);
    }

    public Map<String, Double> compare(String phrase) {
        if (Objects.isNull(phrase)) {
            throw new IllegalArgumentException("Phrase can not be null");
        }

        List<String> modelsWords = lm.getTop5(phrase);
        return compare(phrase, modelsWords);
    }

    public Map<String, Double> compare(String phrase, List<String> words) {
        if (Objects.isNull(phrase)) {
            throw new IllegalArgumentException("Phrase can not be null");
        } else if (Objects.isNull(words)) {
            throw new IllegalArgumentException("Word list to fill sentence can not be null");
        }

        int numberOfMasks = StringUtils.countMatches(phrase, "[MASK]");
        if (numberOfMasks == 0) {
            throw new IllegalArgumentException("Phrase must contain a `[MASK]` token to replace with the words list");
        } else if (numberOfMasks > 1) {
            throw new IllegalArgumentException("Phrase may contain only one `[MASK]` token");
        }
        
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