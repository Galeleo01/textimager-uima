//import de.julielab.jcore.ae.jpos.postagger.POSAnnotator;
//import de.julielab.jcore.types.STTSMedPOSTag;
import de.julielab.jcore.types.Sentence;
import de.julielab.jcore.types.Token;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.XMLInputSource;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class jPOSTest {

    String[] Text = {"Der kleine Baum", "Berlin ist eine große Stadt"};
    String[] Postags = {"ART;ADJA;NN;", "NE;VAFIN;ART;ADJA;NN"};


    public void get_token(JCas jcas, String text) {
        //split sentence to tokens
        String[] words = text.split(" ");

        //initialize index
        int index_start = 0;
        int index_end = 0;

        //loop for all words
        for (String word : words) {
            Token token = new Token(jcas);
            index_end = index_start + word.length();
            token.setBegin(index_start);
            token.setEnd(index_end);
            token.addToIndexes();
            index_start = index_end + 1;
        }

    }

    @Test
    public void test_process() throws Exception {

        XMLInputSource posXML = new XMLInputSource("src/test/resources/POSTagAnnotatorTest.xml");
        ResourceSpecifier posSpec = UIMAFramework.getXMLParser().parseResourceSpecifier(posXML);
        AnalysisEngine posAnnotator = UIMAFramework.produceAnalysisEngine(posSpec);

        for (int i=0; i<Text.length; i++){
            // initialize jcas
            JCas jcas = JCasFactory.createText(Text[i]);

            Sentence sentence = new Sentence(jcas);
            sentence.setBegin(0);
            sentence.setEnd(Text[i].length());
            sentence.addToIndexes();

            get_token(jcas, Text[i]);

            posAnnotator.process(jcas);
            // get the postag
            String predicted_postag = "";
            for (Token token : JCasUtil.select(jcas, Token.class))
            {
                predicted_postag = predicted_postag + token.getPosTag(0).getValue() + ";";
            }
            String correct_postag = Postags[i];

            //compare the result
            assertEquals(correct_postag, predicted_postag);
        }

    }
}
