import de.julielab.jcore.types.*;
import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.XMLInputSource;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class Likelihood_Assignment {
    String[] Text = {"Mutational p53 oncoprotein may likely block apoptosis in adenocarcinoma."};
    int[][] Likelihood_begin = {{ 27, 31 }};
    int[][] Likelihood_end = {{ 30, 37 }};
    String[][] Likelihood_category = {{ "moderate", "high" }};
    int[][] Concept_begin = {{ 11, 38 }};
    int[][] Concept_end = {{ 14, 43 }};
    String[] Likelihood_assignment = {"p53;may;moderate;block;may;moderate;"};

    public void init_jcas(JCas jcas, String text, int[] like_begin, int[] like_end, String[] like_cat, int[] concept_begin, int[] concept_end) {
        for (int i = 0; i < like_begin.length; i++) {
            LikelihoodIndicator likelihood_indicator = new LikelihoodIndicator(jcas);
            likelihood_indicator.setBegin(like_begin[i]);
            likelihood_indicator.setEnd(like_end[i]);
            likelihood_indicator.setLikelihood(like_cat[i]);
            likelihood_indicator.addToIndexes();
        }

        for (int i = 0; i < concept_begin.length; i++) {
            ConceptMention concept_mention = new ConceptMention(jcas);
            concept_mention.setBegin(concept_begin[i]);
            concept_mention.setEnd(concept_end[i]);
            concept_mention.addToIndexes();
        }
    }

    @Test
    public void testprocess() throws IOException, UIMAException {
        XMLInputSource likelihood_assignment_xml = new XMLInputSource("src/test/resources/Likelihood_Assignment/jcore-likelihood-assignment-ae.xml");
        ResourceSpecifier likelihood_assignment_spec = UIMAFramework.getXMLParser().parseResourceSpecifier(likelihood_assignment_xml);
        AnalysisEngine likelihood_assignment_AnalysisEngline = UIMAFramework.produceAnalysisEngine(likelihood_assignment_spec);

        for (int i = 0; i<Text.length; i++) {
            JCas jcas = JCasFactory.createText(Text[i]);

            //create Sentence
            Sentence sentence = new Sentence(jcas);
            sentence.setBegin(0);
            sentence.setEnd(Text[i].length());
            sentence.addToIndexes();

            init_jcas(jcas, Text[i], Likelihood_begin[i], Likelihood_end[i], Likelihood_category[i], Concept_begin[i], Concept_end[i]);

            likelihood_assignment_AnalysisEngline.process(jcas);

            // get the parsing
            String predicted_likelihood_assignment = "";

            for (ConceptMention concept_mention : JCasUtil.select(jcas, ConceptMention.class))
            {
                predicted_likelihood_assignment =  predicted_likelihood_assignment + concept_mention.getCoveredText() + ";"
                                                   + concept_mention.getLikelihood().getCoveredText() + ";"
                                                   + concept_mention.getLikelihood().getLikelihood() + ";";
            }

            String correct_likelihood_assignment = Likelihood_assignment[i];

            //compare the result
            assertEquals(correct_likelihood_assignment, predicted_likelihood_assignment);
        }


    }
}
