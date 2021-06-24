import de.julielab.jcore.types.*;
import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JFSIndexRepository;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;

public class Likelihood_Detection {
    String DESCRIPTOR = "de.julielab.jcore.ae.likelihooddetection.desc.jcore-likelihood-detection-ae";

    String[] Text = {"PML appears to be transcriptionally regulated by class I "
            + "and II interferons , which raises the possibility that interferons modulate the function "
            + "and growth and differentiation potential of normal myeloid cells and precursors ."};
    String[][] Lemmas = {{"pml", "appear", "to", "be",
            "transcriptionally", "regulate", "by", "class", "i", "and", "ii",
            "interferon", ",", "which", "raise", "the", "possibility", "that",
            "interferon", "modulate", "the", "function", "and", "growth",
            "and", "differentiation", "potential", "of", "normal", "myeloid",
            "cell", "and", "precursor", "."}};
    String[] Likelihood_detection = {"appears;moderate;raises the possibility;moderate;"};

    public void init_jcas(JCas jcas, String text, String[] lemmas) {
        //split sentence to tokens
        String[] words = text.split(" ");

        //initialize index
        int index_start = 0;
        int index_end = 0;
        int len = words.length;

        //loop for all words
        for (int i=0; i < len; i++) {
            Token token = new Token(jcas);
            index_end = index_start + words[i].length();
            token.setBegin(index_start);
            token.setEnd(index_end);

            Lemma lemma = new Lemma(jcas);
            lemma.setBegin(index_start);
            lemma.setEnd(index_end);
            lemma.setValue(lemmas[i]);

            token.setLemma(lemma);
            token.addToIndexes();
            index_start = index_end + 1;
        }

    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testProcess() throws UIMAException, IOException {

        XMLInputSource likelihood_assignment_xml = new XMLInputSource("src/test/resources/Likelihood_Detection/jcore-likelihood-detection-ae.xml");
        ResourceSpecifier likelihood_assignment_spec = UIMAFramework.getXMLParser().parseResourceSpecifier(likelihood_assignment_xml);
        AnalysisEngine likelihood_assignment_AnalysisEngline = UIMAFramework.produceAnalysisEngine(likelihood_assignment_spec);


        for (int i = 0; i<Text.length; i++) {
            JCas jcas = JCasFactory.createText(Text[i]);


            init_jcas(jcas, Text[i], Lemmas[i]);

            likelihood_assignment_AnalysisEngline.process(jcas);

            // get the parsing
            String predicted_likelihood_detection = "";

            for (LikelihoodIndicator indicator : JCasUtil.select(jcas, LikelihoodIndicator.class)) {
                predicted_likelihood_detection = predicted_likelihood_detection + indicator.getCoveredText() + ";"
                        + indicator.getLikelihood() + ";";
            }

            String correct_likelihood_detection = Likelihood_detection[i];

            //compare the result
            assertEquals(correct_likelihood_detection, predicted_likelihood_detection);
        }
    }
}
