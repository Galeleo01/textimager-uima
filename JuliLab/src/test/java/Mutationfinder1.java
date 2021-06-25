import de.julielab.jcore.types.Sentence;
import de.julielab.jcore.types.Token;
import edu.uchsc.ccp.nlp.ei.mutation.Mutation;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.XMLInputSource;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Mutationfinder1 {
    @Test
    public void test_process() throws Exception {
        String[] Text = {"njin"};

        XMLInputSource mutationfinder_XML = new XMLInputSource("src/test/resources/Mutationfinder/jcore-mutationfinder-ae.xml");
        ResourceSpecifier mutationfinder_Spec = UIMAFramework.getXMLParser().parseResourceSpecifier(mutationfinder_XML);
        AnalysisEngine mutationfinder_Annotator = UIMAFramework.produceAnalysisEngine(mutationfinder_Spec);

        for (int i=0; i<Text.length; i++){
            // initialize jcas
            JCas jcas = JCasFactory.createText(Text[i]);

            Sentence sentence = new Sentence(jcas);
            sentence.setBegin(0);
            sentence.setEnd(Text[i].length());
            sentence.addToIndexes();

            //get_token(jcas, Text[i]);

            mutationfinder_Annotator.process(jcas);

//            String predicted_postag = "";
//            for (Mutation mutation : JCasUtil.select(jcas, Mutation.class))
//            {
//                predicted_postag = predicted_postag + mutation + ";";
//            }
//            String correct_postag = Postags[i];
//
//            //compare the result
//            assertEquals(correct_postag, predicted_postag);
        }

    }
}
