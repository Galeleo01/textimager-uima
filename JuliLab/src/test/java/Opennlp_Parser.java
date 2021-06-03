import de.julielab.jcore.types.*;
import junit.framework.TestCase;
import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
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
import java.util.Iterator;

import static org.junit.Assert.assertEquals;

public class Opennlp_Parser extends TestCase {
    String[] Text = {"A study on the Prethcamide hydroxylation system in rat hepatic microsomes ."};
    String[] parsing = {"NP NP PP NP NP PP NP "};

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

        XMLInputSource posXML = new XMLInputSource("src/test/resources/Opennlp_Parser/opennlpparser/desc/jcore-opennlpparser-test.xml");
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
            String predicted_parsing = "";
            for (Constituent constituent : JCasUtil.select(jcas, Constituent.class))
            {
                predicted_parsing = predicted_parsing + constituent.getCat() + " ";
            }
            String correct_postag = parsing[i];

            //compare the result
            assertEquals(parsing[i], predicted_parsing);
        }
    }
}

