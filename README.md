[![Java CI with Maven](https://github.com/LMartinezEXEX/BiasExploration4J/actions/workflows/maven.yml/badge.svg)](https://github.com/LMartinezEXEX/BiasExploration4J/actions/workflows/maven.yml)
[![Tests](https://github.com/LMartinezEXEX/BiasExploration4J/actions/workflows/testing.yml/badge.svg)](https://github.com/LMartinezEXEX/BiasExploration4J/actions/workflows/testing.yml)

# BiasExploration4J

## Description

Language models and word embeddings used nowadays made use of huges amouts of unlabeled bias data for their respective training phase. This increased with the new Transformers's attention mechanism to use unsupervised leanring techniques.

In all this amounts of samples for training, bias samples are bound to be present and affect the results of the resulting systems.

Sometimes we need to make biased decisions with our systems, but it is also nice to know the bias within our model.

This tool allows for word embedding exploration and BERT-based language model preference measurement, the latter based on [(Nangia et al., 2020)](https://aclanthology.org/2020.emnlp-main.154/) work, to explore different bias within the given system.

## Download and use

This tool is composed by different classes that aims to evaluate some aspect of bias so you can instatiate it anywhere you like.

```bash
# Clone the repository
git clone https://github.com/LMartinezEXEX/BiasExploration4J.git

# Change CWD inside the cloned repository
cd BiasExploration4J

# For use as a dependency from other projects
mvn clean install
```

## Packages

**BiasExploration4J** is composed of two main packages:
- **WordExploration**: contains classes to explore word embedding bias in a 2D space or defining *kernels of meaning*.
- **PhraseBiasExplorer**: contains classes to explore BERT-based language models preferences.

There are also two utilities packages:
- **DataLoader**: classes to load embeddings from user files.
- **NearestNeighbour**: contains class to calculate neighbours of labeled points.

## WordExploration

### WordExplorer
This class allows to examinate word embeddings bias, checking related (neighbour) words or evaluating by defined *kernels of meaning*.

First you need to load your embeddings, currently only traditional `.vec` embeddings are supported, but you are encouraged to implement your own custom class that extends `DataLoader`.

```java
DataLoader data = new VecLoader();
data.loadDataset(Paths.get("path/to/embeddings.vec"));

WordExplorer we = new WordExplorer(data);
```

With this object you can now retrieve related words with

```java
we.getNeighbours(Arrays.asList("man", "woman"));
```

If you want to plot them in a 2D space to inspect their proximity among other words you need first to reduce the dimension of the embeddings to only two components, we achieve this with PCA dimensionality reduction method.

```java
we.we.calculateWordsPca(false);
```

> **Note**: This method takes as parameter a `boolean` in order to normalize (`true`) the word embeddings or don't (`false`).

Now you have almost everithing you need to plot, but remains a **crucial** part as of the current version and that is to start JavaFx thread, this is achieved with a call to the `Visualizer` static method `setup()`

```java
Visualizer().setup();
```

Now you can plot words from this class and BiasExplorer. Lets plot the words defined before with their respective neigbours.

```java
we.plot(Arrays.asList("man", "woman"), 2);
```

### BiasExplorer

With this class you can define up to **four** *kernels of meaning* to measure the bias within your embeddings.

A *kernel of meaning* is a list of words defining an aspect of bias, you may always want to define two opposed *kernels of meaning*. For example:

```java
List<String> femenine_kernel  = Arrays.asList("women", "she", "girl", "her");
List<String> masculine_kernel = Arrays.asList("man", "he", "boy", "his");
```

and evaluate the bias of words in your vocabulary with this defined kernels.

To instantiate this class, you only need a `WordExplorer` object.

```java
BiasExplorer be = new BiasExplorer(we);
```

Now you are ready to evaluate the bias with 2 kernels, which result in a histogram plot:

```java
List<String> words = Arrays.asList("astronaut", "teacher", "professor", "doctor");
be.plot2SpaceBias(words, femenine_kernel, masculine_kernel);
```

You can defined two more kernels for a more specific bias evaluation and plot which result in a 2D plot:

```java
List<String> young_kernel  = Arrays.asList("young", "kid", "immature", "child");
List<String> older_kernel = Arrays.asList("old", "mature", "elderly", "adult");

be.plot4SpaceBias(words, femenine_kernel, masculine_kernel, young_kernel, older_kernel);
```

## PhraseBiasExploration

### CorwsPairs

This class seek to define preferences of pretrained models when producing language. In order to calculate this preference we define two phrases, one with a **stereotype** and other with an **anti-setreotype**, if the model is biased one would have a higher preference value than the other.

The loading of models make use of the [Deep Java Library](https://github.com/deepjavalibrary/djl) (DJL), so you can load with a model URL any BERT-based model you like. So far we will load the base uncased model and evaluate it with two phrases.

```java
CrowsPairs cp = new CrowsPairs();
String stereotype = "<Homosexuals> should not be allowed to get married";
String anti_stereotype = "<Heterosexuals> should not be allowed to get married";
Map<String, Double> pllScores = cp.compare(Arrays.asList(stereotype, anti_stereotype));

// print the scores
// ...
```

The higher the value, the more preference the model have for that option. Note however that the words that the model will evaluate are all excpet the sorrounded with the symbols < and >. For more information about this model preference score see [(Nangia et al., 2020)](https://aclanthology.org/2020.emnlp-main.154/).

### MaskFillerRanker

Other way of evaluating models preferences is to define a phrase with a mask and give the models optional words to fill said mask, and evaluate the preference. This is achieved with this class. You just need to define a phrase that may contain an stereotype in the masked word and ask the model to fill it with, either, a list of words of your choice, or some selected by the model itself.

```java
// With given words
CrowsPairs mfr = new MaskFillerRanker();
String stereotype = "[MASK] are dangerous people";
List<String> wordsOfInteres = Arrays.asList("poor", "black", "rich", "young");
Map<String, Double> pllScores = mfr.compare(stereotype, wordsOfInteres);

// Without given words
Map<String, Double> pllScores = mfr.compare(stereotype);

// print the scores
// ...
```

> **IMPORTANT**: Currently the WordExploration package and PhraseBiasExploration package should not be used in the same JVM run because of conclicts between DJL and JavaFx. This would be fixed in a later release! ;D

## License Information 
This project is under a [MIT license](LICENSE).