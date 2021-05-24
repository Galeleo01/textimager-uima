import de.julielab.jcore.ae.jpos.postagger.POSAnnotator;
import de.julielab.jcore.types.STTSMedPOSTag;
import de.julielab.jcore.types.Sentence;
import de.julielab.jcore.types.Token;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JFSIndexRepository;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.XMLInputSource;
import org.junit.Test;
import java.util.*;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.junit.Assert.assertEquals;

public class jPOSTest {

    String[] Text = {"Der kleine Baum", "Am blauen Himmel ziehen die Wolken"};
    String[] Postags = {"ART;ADJA;NN;", "APPRART;ADJA;NN;VVFIN;ART;NN"};

    public void initCas(final JCas jcas, String text) {
        jcas.reset();
        jcas.setDocumentText(text);
        final Sentence sentence = new Sentence(jcas);
        sentence.setBegin(0);
        sentence.setEnd(text.length());
        sentence.addToIndexes();

        String[] words = text.split(" ");

        //initialize index
        int index_start = 0;
        int index_end = 0;

        for (String word : words) {
            final Token token = new Token(jcas);
            index_end = index_start + word.length();
            token.setBegin(index_start);
            token.setEnd(index_end);
            token.addToIndexes();
            index_start = index_end + 1;
        }

    }

    @Test
    public void firstTest() throws Exception {

        XMLInputSource posXML = new XMLInputSource("src/test/resources/POSTagAnnotatorTest.xml");
        ResourceSpecifier posSpec = UIMAFramework.getXMLParser().parseResourceSpecifier(posXML);
        AnalysisEngine posAnnotator = UIMAFramework.produceAnalysisEngine(posSpec);

        final JCas jcas = JCasFactory.createJCas();
        // get test cas with sentence annotation
        for (int i=0; i<Text.length; i++){
            initCas(jcas, Text[i]);

            posAnnotator.process(jcas);
            // get the offsets of the sentences
            String predictedpostag = "";
            for (Token token : JCasUtil.select(jcas, Token.class))
            {
                predictedpostag = predictedpostag + token.getPosTag(0).getValue() + ";";
            }
            String correctpostag = Postags[i];
            assertEquals(correctpostag, predictedpostag);
        }

    }
}
