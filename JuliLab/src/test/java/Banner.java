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
import static org.junit.Assert.assertEquals;
public class Banner {
    String[] Text = {"Ten out-patients with pustulosis palmaris et plantaris were examined with direct immunofluorescence (IF) technique for deposition of fibrinogen, fibrin or its degradation products (FR-antigen) in affected and unaffected skin, together with heparin-precipitable fraction (HPF), cryoglobulin and total plasma fibrinogen in the blood."};
    String[] Gene = {"fibrinogen;fibrin;FR-antigen;cryoglobulin;fibrinogen;"};
    @Test
    public void testProcess() throws IOException, UIMAException {

        XMLInputSource banner_XML = new XMLInputSource(
                "src/test/resources/Banner/jcore-banner-ae.xml");
        ResourceSpecifier banner_Spec = UIMAFramework.getXMLParser().parseResourceSpecifier(
                banner_XML);
        AnalysisEngine banner_Annotator = UIMAFramework
                .produceAnalysisEngine(banner_Spec);

        for (int i=0; i<Text.length; i++){
            // initialize jcas
            JCas jcas = JCasFactory.createText(Text[i]);

            Sentence sentence = new Sentence(jcas);
            sentence.setBegin(0);
            sentence.setEnd(Text[i].length());
            sentence.addToIndexes();

            banner_Annotator.process(jcas);

            // get the parsing
            String predicted_banner = "";
            for (Gene gene : JCasUtil.select(jcas, Gene.class))
            {
                predicted_banner = predicted_banner + gene.getCoveredText() + ";" ;
            }
            String correct_banner = Gene[i];


            //compare the result
            assertEquals(correct_banner, predicted_banner);
        }

    }

}
