
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import de.julielab.jcore.types.Constituent;
import de.julielab.jcore.types.PennBioIEPOSTag;
import de.julielab.jcore.types.Sentence;
import de.julielab.jcore.types.Token;
import org.apache.commons.lang.StringUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JFSIndexRepository;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.XMLInputSource;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
public class Opennlp_Postag {
    String[] Text = {"A study on the Prethcamide"};
    String[] Postags = {"DT;NN;IN;DT;NN;"};

    public void get_cas(JCas jcas, String text) {
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
    public void test() throws IOException, UIMAException {

        XMLInputSource posXML = new XMLInputSource("src/test/resources/Opennlp_Postag/PosTagAnnotatorTest.xml");
        ResourceSpecifier posSpec = UIMAFramework.getXMLParser().parseResourceSpecifier(posXML);
        AnalysisEngine open_nlp_parser = UIMAFramework.produceAnalysisEngine(posSpec);

        for (int i=0; i<Text.length; i++){
            // initialize jcas
            JCas jcas = JCasFactory.createText(Text[i]);

            Sentence sentence = new Sentence(jcas);
            sentence.setBegin(0);
            sentence.setEnd(Text[i].length());
            sentence.addToIndexes();

            get_cas(jcas, Text[i]);

            open_nlp_parser.process(jcas);

            // get the parsing
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
