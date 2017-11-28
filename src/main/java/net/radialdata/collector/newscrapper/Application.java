package net.radialdata.collector.newscrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    @Override
    public void run(String... args) throws Exception {



        // nlp demo
//            Properties nlpProps = new Properties();
//            nlpProps.put("annotators","tokenize, ssplit, parse, sentiment");
//            StanfordCoreNLP pipeline = new StanfordCoreNLP(nlpProps);

//            String content = "This is the best content ever created!";
//            int mainSentiment = 0;
//            int longest = 0;
//            Annotation annotation = pipeline.process(content);
//            for (CoreMap sentence : annotation
//                    .get(CoreAnnotations.SentencesAnnotation.class)) {
//                Tree tree = sentence
//                        .get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
//                int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
//                String partText = sentence.toString();
//                if (partText.length() > longest) {
//                    mainSentiment = sentiment;
//                    longest = partText.length();
//                }
//
//            }

    }

}
