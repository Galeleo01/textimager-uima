import de.julielab.jcore.types.*;
import de.julielab.jcore.utility.JCoReTools;
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
public class Lingscope {
    String[] Sentence = {"It might be true ."};
    String[] Lemmas = {"It may be true ."};
    String[] PosTags = {"PRP MD VB JJ ."};
    String[] Negation = {"negation;"};
    String[] Likelihood = {"moderate;"};
    String[] Indicator = {"3;8;"};
    String[] Scope = {"3;16;"};

    public void init_jcas(JCas jcas, String sentences, String lemmas, String postags) {
        //split sentence to tokens
        String[] tok = sentences.split(" ");
        String[] lem = lemmas.split(" ");
        String[] pos = postags.split(" ");

        //initialize index
        int index_start = 0;
        int index_end = 0;
        int len = tok.length;

        //loop for all words
        for (int i=0; i < len; i++) {
            index_end = index_start + tok[i].length();

            Token token = new Token(jcas, index_start, index_end);

            PennBioIEPOSTag pennpostag = new PennBioIEPOSTag(jcas, index_start, index_end);
            pennpostag.setValue(pos[i]);

            Lemma lemma = new Lemma(jcas, index_start, index_end);
            lemma.setValue(lem[i]);

            token.setLemma(lemma);
            token.setPosTag(JCoReTools.addToFSArray(null, pennpostag));
            token.addToIndexes();
            index_start = index_end + 1;
        }

    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testProcess() throws UIMAException, IOException {

        XMLInputSource lingscope_xml = new XMLInputSource("src/test/resources/Lingscope/jcore-lingscope-ae.xml");
        ResourceSpecifier lingscope_spec = UIMAFramework.getXMLParser().parseResourceSpecifier(lingscope_xml);
        AnalysisEngine lingscope_AnalysisEngline = UIMAFramework.produceAnalysisEngine(lingscope_spec);


        for (int i = 0; i<Sentence.length; i++) {
            JCas jcas = JCasFactory.createText(Sentence[i]);

            Sentence sentence = new Sentence(jcas);
            sentence.setBegin(0);
            sentence.setEnd(Sentence[i].length());
            sentence.addToIndexes();

            init_jcas(jcas, Sentence[i], Lemmas[i], PosTags[i]);

            lingscope_AnalysisEngline.process(jcas);

            // get the parsing
            String predicted_likelihood = "";
            String predicted_indicator= "";
            String predicted_negation = "";
            String predicted_scope = "";

            for (LikelihoodIndicator indicator : JCasUtil.select(jcas, LikelihoodIndicator.class)) {
                predicted_negation = predicted_negation + indicator.getLikelihood() + ";";
//                predicted_likelihood = predicted_likelihood + indicator.getCoveredText();
                predicted_indicator = predicted_indicator + indicator.getBegin() + ";" + indicator.getEnd() + ";";
            }

            for (Scope scope : JCasUtil.select(jcas, Scope.class)) {
                predicted_scope = predicted_scope + scope.getBegin() + ";"
                        + scope.getEnd() + ";";
            }

            String correct_predicted_likelihood = Likelihood[i];
            String correct_predicted_indicator= Indicator[i];
            String correct_predicted_negation = Negation[i];
            String correct_predicted_scope = Scope[i];

            //compare the result
//            assertEquals(correct_predicted_likelihood, predicted_likelihood);
            assertEquals(correct_predicted_indicator, predicted_indicator);
            assertEquals(correct_predicted_negation, predicted_negation);
            assertEquals(correct_predicted_scope, predicted_scope);
        }
    }
}
