package com.biasexplorer4j;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.biasexplorer4j.DataLoader.DataLoader;
import com.biasexplorer4j.DataLoader.VecLoader;
import com.biasexplorer4j.PhraseBiasExploration.CrowsPairs;
import com.biasexplorer4j.PhraseBiasExploration.LanguageModel;
import com.biasexplorer4j.PhraseBiasExploration.MaskFillerRanker;
import com.biasexplorer4j.WordExploration.WordExplorer;
import com.biasexplorer4j.WordExploration.BiasExploration.BiasExplorer;
import com.biasexplorer4j.WordExploration.Visualization.Plots.PlotManager;

import ai.djl.MalformedModelException;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.translate.TranslateException;
import javafx.application.Platform;
import java.awt.EventQueue;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) {
        DataLoader data = new VecLoader();
        data.loadDataset(Paths.get("BiasExploration4J/src/main/java/com/biasexplorer4j/data/100k_es_embedding.vec"));

        WordExplorer we = new WordExplorer(data);
        we.calculateWordsPca(false);

        we.plot(Arrays.asList("perro", "mujer", "hombre", "chico", "papaya21"));
        //we.plot(Arrays.asList("lagarto"));
        //we.plot(Arrays.asList("muchahco", "campo", "enfermero"));

        //var ex = new Prueba2();
        //ex.setVisible(true);
        //var ex2 = new Prueba();
        //ex2.setVisible(true);
        //EventQueue.invokeLater(() -> {
        //    var ex = new Prueba2();
        //    ex.setVisible(true);
        //});

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

        
        MaskFillerRanker mf = new MaskFillerRanker();
        Map<String, Double> pllScores = mf.compare("The [MASK] people are sozoszo poor", Arrays.asList("native", "black"));
        for(Map.Entry<String, Double> e : pllScores.entrySet()) {
            System.out.println("PLL score for {" + e.getKey() + "}: " + e.getValue());
        }
        
        BiasExplorer be = new BiasExplorer(we);
        be.plot4SpaceBias(Arrays.asList("rey", "reina", "chico", "princesa", "viejo", "mujer", "negro"), 
                          Arrays.asList("hombre", "chico", "el", "padre", "hijo", "masculino"), 
                          Arrays.asList("mujer", "chica", "ella", "madre", "hija", "femenino"),
                          Arrays.asList("joven", "chico", "inmaduro"),
                          Arrays.asList("viejo", "maduro", "anciano", "adulto")
                        );
        
        //PlotManager.getInstance().cleanUp();
    }
}
