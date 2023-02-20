[![Java CI with Maven](https://github.com/LMartinezEXEX/BiasExploration4J/actions/workflows/maven.yml/badge.svg)](https://github.com/LMartinezEXEX/BiasExploration4J/actions/workflows/maven.yml)
[![Tests](https://github.com/LMartinezEXEX/BiasExploration4J/actions/workflows/testing.yml/badge.svg)](https://github.com/LMartinezEXEX/BiasExploration4J/actions/workflows/testing.yml)

# BiasExploration4J

## Description

The language models and word embeddings used today **made use of  huge amounts of unlabeled bias data for their respective training phase**, which was increased by the attention mechanism of the new Transformers as a new unsupervised learning technique.

With all these training samples, there are bound to be biased samples that affect the results of the resulting systems. 

Sometimes we have to make biased decisions with our systems, but it is also nice to know the bias within our model.


**BiasExploration4J** allows word embedding exploration and BERT-based language model _tendency_ measurement, the latter based on [(Nangia et al., 2020)](https://aclanthology.org/2020.emnlp-main.154/) work, to explore different biases within the given system.

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
## How to use and documentation

Check out our [Wiki pages](https://github.com/LMartinezEXEX/BiasExploration4J/wiki) where you will find information, notes, and tips on how to use **BiasExploration4J**, definitions of core concepts, and certain limitations to keep in mind when exploring bias in your data, as well as examples to help you better internalize it all.

## License Information 
This project is under a [MIT license](LICENSE).