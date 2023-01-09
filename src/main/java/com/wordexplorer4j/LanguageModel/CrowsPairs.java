package com.wordexplorer4j.LanguageModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.hadoop.shaded.org.apache.commons.lang3.StringUtils;

public class CrowsPairs {
    private LanguageModel lm;

    public CrowsPairs() {
        this.lm = new LanguageModel();
    }

    public CrowsPairs(String modelUrl) {
        this.lm = new LanguageModel(modelUrl);
    }

    public Map<String, Double> compare(List<String> phrases) {
        phrases.forEach(this::check);

        List<Double> ranks = new ArrayList<>(phrases.size());                                
        for (int i = 0; i < phrases.size(); ++ i) {
            List<String> processedPhrases = lm.processInput(phrases.get(i)); 
            List<String> validTokens = lm.getValidTokens(phrases.get(i));
            ranks.add(lm.rank(processedPhrases, validTokens));
        }                                            
                                                 
        PseudoLikelihood pll = new PseudoLikelihood(ranks);
        return PseudoLikelihood.zipToMap(phrases, pll.getPLLScores());
    }

    private String check(String phrase) {
        if (!isCorrectlyFormatted(phrase))
            throw new IllegalArgumentException("Sentence { " + phrase + " } is wrongly formatted");
        
        phrase = phrase.replace("<", "").replace(">", "");
        return phrase;
    }

    private boolean isCorrectlyFormatted(String phrase) {
        int openCharCount = StringUtils.countMatches(phrase, "<");
        int closeCharCount = StringUtils.countMatches(phrase, ">");
        if ( openCharCount == 0 || closeCharCount == 0 || 
            (openCharCount == 1 && closeCharCount == 1 && 
             phrase.charAt(0) == '<' && phrase.charAt(phrase.length()-1) == '>')
            ) {
            return false;
        }

        Stack<Character> stack = new Stack<>();
        for (int i = 0; i < phrase.length(); ++i) {
            char c = phrase.charAt(i);
            if (c == '<') {
                stack.push(c);
            } else if (c == '>') {
                if (stack.isEmpty() || stack.pop() != '<' || phrase.charAt(i-1) == '<') {
                    return false;
                }
            }
        }

        return stack.isEmpty();
    }
}
