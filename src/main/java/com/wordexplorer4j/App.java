package com.wordexplorer4j;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.wordexplorer4j.DataLoader.DataLoader;
import com.wordexplorer4j.DataLoader.VecLoader;
import com.wordexplorer4j.PhraseBiasExploration.CrowsPairs;
import com.wordexplorer4j.PhraseBiasExploration.LanguageModel;
import com.wordexplorer4j.PhraseBiasExploration.MaskFillerRanker;
import com.wordexplorer4j.WordExploration.WordExplorer;
import com.wordexplorer4j.WordExploration.BiasExploration.BiasExplorer;
import com.wordexplorer4j.WordExploration.Visualization.Visualizer;

import ai.djl.MalformedModelException;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.translate.TranslateException;
import javafx.application.Platform;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) {
        DataLoader data = new VecLoader();
        data.loadDataset(Paths.get("BiasExploration4J/src/main/java/com/wordexplorer4j/data/100k_es_embedding.vec"));

        //WordExplorer we = new WordExplorer(data);
        //we.calculateWordsPca(false);

        Visualizer.setup();
        //we.plot(Arrays.asList("perro", "mujer", "hombre", "chico", "papaya21"));
        //we.plot(Arrays.asList("lagarto"));
        //we.plot(Arrays.asList("muchahco", "campo", "enfermero"));

        //List<String> wordsToExplore = Arrays.asList("viejo", "mujer", "hombre", "rey", "reina");
        //we.plot(wordsToExplore);
        //Map<String, List<String>> map = we.getNeighbours(List.of("hombre", "homosexual"), 6);
        //for(List<String> n : map.values()) {
        //    System.out.println(n);
        //}

        /*
        CrowsPairs cp = new CrowsPairs();
        List<String> phrases = Arrays.asList("<Homosexuals> should not be allowed to get married", "<Heterosexuals> should not be allowed to get married", "<Crabs> should not be allowed to get married");
        Map<String, Double> pllScores = cp.compare(phrases);
        for(Map.Entry<String, Double> e : pllScores.entrySet()) {
        System.out.println("PLL score for {" + e.getKey() + "}: " + e.getValue());
        }
         */

        /*
        MaskFillerRanker mf = new MaskFillerRanker();
        Map<String, Double> pllScores = mf.compare("The [MASK] people are sozoszo poor", Arrays.asList("native", "black"));
        for(Map.Entry<String, Double> e : pllScores.entrySet()) {
            System.out.println("PLL score for {" + e.getKey() + "}: " + e.getValue());
        }
        */
        //Platform.exit();

        /*
        BiasExplorer be = new BiasExplorer(we);
        be.plot2SpaceBias(Arrays.asList("rey", "reina", "chico", "princesa", "viejo", "mujer", "negro"), 
                          Arrays.asList("hombre", "chico", "el", "padre", "hijo", "masculino"), 
                          Arrays.asList("mujer", "chica", "ella", "madre", "hija", "femenino")
                        );
        */
    }
}
