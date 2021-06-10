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

public class BioLemmatizer {
    String[] Text = {"Three horses were going contemplatively around bushy bushes."};
    String[] Postag = {"DT NNS VBD VBG RB IN JJ NNS ."};

    public void init_jcas(JCas jcas, String text, String POSTAG) {
        //split sentence to tokens
        String[] words = text.split(" ");
        String[] postags = POSTAG.split(" ");

        //initialize index
        int index_start = 0;
        int index_end = 0;

        //loop for all words
        for (int i=0; i< words.length; i++) {
            index_end = index_start + words[i].length();
            Token token = new Token(jcas);
            POSTag pos = new POSTag(jcas);

            token.setBegin(index_start);
            token.setEnd(index_end);
            token.addToIndexes();

            pos.setBegin(index_start);
            pos.setEnd(index_end);
            pos.setValue(postags[i]);
            pos.addToIndexes();

            index_start = index_end + 1;
        }
    }

    @Test
    public void testProcess() throws IOException, UIMAException {

        XMLInputSource biolemmatizer_XML = new XMLInputSource(
                "src/test/resources/biolemmatizer/jcore-biolemmatizer-ae.xml");
        ResourceSpecifier biolemmatizer_Spec = UIMAFramework.getXMLParser().parseResourceSpecifier(
                biolemmatizer_XML);
        AnalysisEngine biolemmatizer_Annotator = UIMAFramework
                .produceAnalysisEngine(biolemmatizer_Spec);

        for (int i=0; i<Text.length; i++){
            // initialize jcas
            JCas jcas = JCasFactory.createText(Text[i]);

            init_jcas(jcas, Text[i], Postag[i]);
            biolemmatizer_Annotator.process(jcas);

            // get the parsing
            String predicted_lemma = "";
            for (Token token : JCasUtil.select(jcas, Token.class))
            {
                predicted_lemma = predicted_lemma + token.getLemma().getValue() + ";" ;
            }
            System.out.println(predicted_lemma);

            //compare the result
            //assertEquals(correct_offset, predicted_offset);
        }

    }

}

