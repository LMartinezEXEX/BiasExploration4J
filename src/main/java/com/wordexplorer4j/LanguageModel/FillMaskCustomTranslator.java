package com.wordexplorer4j.LanguageModel;

import ai.djl.huggingface.tokenizers.Encoding;
import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import ai.djl.modality.Classifications;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.translate.ArgumentsUtil;
import ai.djl.translate.Batchifier;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/** The translator for Huggingface fill mask model. */
public class FillMaskCustomTranslator implements Translator<String, Classifications> {

    private HuggingFaceTokenizer tokenizer;
    private String maskToken;
    private long maskTokenId;
    private int topK;
    private Batchifier batchifier;
    private List<String> wordList;

    FillMaskCustomTranslator(
            HuggingFaceTokenizer tokenizer, String maskToken, List<String> wordList, int topK, Batchifier batchifier) {
        this.tokenizer = tokenizer;
        this.maskToken = maskToken;
        this.topK = topK;
        this.wordList = wordList;
        this.batchifier = batchifier;
        Encoding encoding = tokenizer.encode(maskToken, false);
        maskTokenId = encoding.getIds()[0];
    }

    /** {@inheritDoc} */
    @Override
    public Batchifier getBatchifier() {
        return batchifier;
    }

    /** {@inheritDoc} */
    @Override
    public NDList processInput(TranslatorContext ctx, String input) throws TranslateException {
        NDManager manager = ctx.getNDManager();
        Encoding encoding = tokenizer.encode(input);
        long[] indices = encoding.getIds();
        int maskIndex = -1;
        for (int i = 0; i < indices.length; ++i) {
            if (indices[i] == maskTokenId) {
                if (maskIndex != -1) {
                    throw new TranslateException("Only one mask supported.");
                }
                maskIndex = i;
            }
        }
        if (maskIndex == -1) {
            throw new TranslateException("Mask token " + maskToken + " not found.");
        }
        ctx.setAttachment("maskIndex", maskIndex);
        ctx.setAttachment("tokenMaskedIdx", indices[maskIndex]);
        long[] attentionMask = encoding.getAttentionMask();
        NDList ndList = new NDList(2);
        /*
        System.out.print("LEN: " + indices.length + " ");
        for(long l : indices) {
            System.out.print(tokenizer.decode(new long[] {l}) + " ");
        }
        System.out.println();
        */
        wordList.forEach(s -> System.out.println("Se procesa: " + s + "\n"));
        ndList.add(manager.create(indices));
        ndList.add(manager.create(attentionMask));
        return ndList;
    }

    /** {@inheritDoc} */
    @Override
    public Classifications processOutput(TranslatorContext ctx, NDList list) throws TranslateException{
        Classifications cls;
        if (wordList.isEmpty()) {
            cls = processOutputWithTop(ctx, list);
        } else {
            cls = processOutputWithWordList(ctx, list);
        }
        return cls;
    }
    
    public Classifications processOutputWithTop(TranslatorContext ctx, NDList list) {
        int maskIndex = (int) ctx.getAttachment("maskIndex");
        NDArray prob = list.get(0).get(maskIndex).softmax(0);
        NDArray array = prob.argSort(0, false);
        long[] classIds = new long[topK];
        List<Double> probabilities = new ArrayList<>(topK);
        for (int i = 0; i < topK; ++i) {
            classIds[i] = array.getLong(i);
            probabilities.add((double) prob.getFloat(classIds[i]));
        }
        String[] classes = tokenizer.decode(classIds).trim().split(" ");
        return new Classifications(Arrays.asList(classes), probabilities);
    }

    public Classifications processOutputWithWordList(TranslatorContext ctx, NDList list) {
        int maskIndex = (int) ctx.getAttachment("maskIndex");
        NDArray prob = list.get(0).get(maskIndex).softmax(0);
        List<Double> probabilities = new ArrayList<>(wordList.size());
        List<String> tokens = new ArrayList<>((int) prob.size(0));
        for (int i = 0; i < prob.size(0); ++i) {
            tokens.add(tokenizer.decode(new long[] {i}).trim());
        }

        String token = wordList.get(0);
        List<String> tokenClass = Arrays.asList(token);
        int tokenIdx = tokens.indexOf(token);
        probabilities.add((double) prob.getFloat(tokenIdx));
        return new Classifications(tokenClass, probabilities);
    }

    /**
     * Creates a builder to build a {@code FillMaskTranslator}.
     *
     * @param tokenizer the tokenizer
     * @return a new builder
     */
    public static Builder builder(HuggingFaceTokenizer tokenizer) {
        return new Builder(tokenizer);
    }

    /**
     * Creates a builder to build a {@code FillMaskTranslator}.
     *
     * @param tokenizer the tokenizer
     * @param arguments the models' arguments
     * @return a new builder
     */
    public static Builder builder(HuggingFaceTokenizer tokenizer, Map<String, ?> arguments) {
        Builder builder = builder(tokenizer);
        builder.configure(arguments);

        return builder;
    }

    /** The builder for fill mask translator. */
    public static final class Builder {

        private HuggingFaceTokenizer tokenizer;
        private String maskedToken = "[MASK]";
        private int topK = 5;
        private List<String> wordList = new ArrayList<>();
        private Batchifier batchifier = Batchifier.STACK;

        Builder(HuggingFaceTokenizer tokenizer) {
            this.tokenizer = tokenizer;
        }

        /**
         * Sets the id of the mask {@link Translator}.
         *
         * @param maskedToken the id of the mask
         * @return this builder
         */
        public Builder optMaskToken(String maskedToken) {
            this.maskedToken = maskedToken;
            return this;
        }

        /**
         * Set the topK number of classes to be displayed.
         *
         * @param topK the number of top classes to return
         * @return this builder
         */
        public Builder optTopK(int topK) {
            this.topK = topK;
            return this;
        }

        /**
         * Sets the {@link Batchifier} for the {@link Translator}.
         *
         * @param batchifier true to include token types
         * @return this builder
         */
        public Builder optBatchifier(Batchifier batchifier) {
            this.batchifier = batchifier;
            return this;
        }

        public Builder optWordList(List<String> wordList) {
            this.wordList = wordList;
            return this;
        }

        /**
         * Configures the builder with the model arguments.
         *
         * @param arguments the model arguments
         */
        public void configure(Map<String, ?> arguments) {
            optMaskToken(ArgumentsUtil.stringValue(arguments, "maskToken", "[MASK]"));
            optTopK(ArgumentsUtil.intValue(arguments, "topK", 5));
            String batchifierStr = ArgumentsUtil.stringValue(arguments, "batchifier", "stack");
            optBatchifier(Batchifier.fromString(batchifierStr));
        }

        /**
         * Builds the translator.
         *
         * @return the new translator
         * @throws IOException if I/O error occurs
         */
        public FillMaskCustomTranslator build() throws IOException {
            return new FillMaskCustomTranslator(tokenizer, maskedToken, wordList, topK, batchifier);
        }
    }
}

