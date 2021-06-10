import de.julielab.jcore.types.Sentence;
import de.julielab.jcore.types.Token;
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


import static org.junit.Assert.assertEquals;

public class Opennlp_Token {
    /**
     * Logger for this class
     */

    String[] Text = {"CD44, at any stage, is a XYZ"};

    String[] Offsets = {"0-4;4-5;6-8;9-12;13-18;18-19;20-22;23-24;25-28;"};

    @Test
    public void testProcess() throws IOException, UIMAException {

        XMLInputSource token_XML = new XMLInputSource(
                "src/test/resources/Opennlp_Token/TokenAnnotatorTest.xml");
        ResourceSpecifier token_Spec = UIMAFramework.getXMLParser().parseResourceSpecifier(
                token_XML);
        AnalysisEngine token_Annotator = UIMAFramework
                .produceAnalysisEngine(token_Spec);

        for (int i=0; i<Text.length; i++){
            // initialize jcas
            JCas jcas = JCasFactory.createText(Text[i]);
            //input
            Sentence sentence = new Sentence(jcas);
            sentence.setBegin(0);
            sentence.setEnd(Text[i].length());
            sentence.addToIndexes();

            token_Annotator.process(jcas);

            // get the parsing
            String predicted_offset = "";
            for (Token token : JCasUtil.select(jcas, Token.class))
            {
                predicted_offset = predicted_offset + token.getBegin() + "-" + token.getEnd() + ";" ;
            }
            String correct_offset = Offsets[i];

            //compare the result
            assertEquals(correct_offset, predicted_offset);
        }

    }
}

