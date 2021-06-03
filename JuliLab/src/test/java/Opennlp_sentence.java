import de.julielab.jcore.types.Sentence;
import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.XMLInputSource;


import java.io.IOException;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;

public class Opennlp_sentence {
    /**
     * Logger for this class
     */

    String[] Text = {"First sentence. Second sentence!"};

    String[] Offsets = {"0-15;16-32;"};

    public void testProcess() throws IOException, UIMAException {

        XMLInputSource sentenceXML = new XMLInputSource(
                "src/test/resources/Opennlp_sentence/SentenceAnnotatorTest.xml");
        ResourceSpecifier sentenceSpec = UIMAFramework.getXMLParser().parseResourceSpecifier(
                sentenceXML);
        AnalysisEngine sentenceAnnotator = UIMAFramework
                .produceAnalysisEngine(sentenceSpec);

        for (int i=0; i<Text.length; i++){
            // initialize jcas
            JCas jcas = JCasFactory.createText(Text[i]);

            sentenceAnnotator.process(jcas);

            // get the parsing
            String predicted_offset = "";
            for (Sentence sentence : JCasUtil.select(jcas, Sentence.class))
            {
                predicted_offset = predicted_offset + sentence.getBegin() + "-" + sentence.getEnd() + ";" ;
            }
            String correct_offset = Offsets[i];

            //compare the result
            assertEquals(correct_offset, predicted_offset);
        }

    }
}
