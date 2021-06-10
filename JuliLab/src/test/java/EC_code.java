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
import de.julielab.jcore.types.Enzyme;

import java.io.IOException;

public class EC_code {
    @Test
    public void testProcess() throws IOException, UIMAException {
        String[] Text = {"Acetylesterase has number EC 3.1.1.6"};
        XMLInputSource EC_code_XML = new XMLInputSource(
                "src/test/resources/EC-Code/jcore-ecn-ae.xml");
        ResourceSpecifier EC_code_Spec = UIMAFramework.getXMLParser().parseResourceSpecifier(
                EC_code_XML);
        AnalysisEngine EC_code_Annotator = UIMAFramework
                .produceAnalysisEngine(EC_code_Spec);

        for (int i=0; i<Text.length; i++){
            // initialize jcas
            JCas jcas = JCasFactory.createText(Text[i]);

            EC_code_Annotator.process(jcas);

            // get the parsing
            String predicted_EC = "";
            for (Enzyme enzyme : JCasUtil.select(jcas, Enzyme.class))
            {
                predicted_EC = predicted_EC + enzyme.getSpecificType();
            }
            System.out.println(predicted_EC);

            //compare the result
            //assertEquals(correct_offset, predicted_offset);
        }

    }


}
