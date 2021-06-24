import de.julielab.jcore.ae.fvr.FeatureValueReplacementAnnotator;
import de.julielab.jcore.ae.fvr.FeatureValueReplacementsProvider;
import de.julielab.jcore.types.OntClassMention;
import de.julielab.jcore.types.POSTag;
import de.julielab.jcore.types.Token;
import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.XMLInputSource;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class Feature_Value_Replacement {
    String[] Text = {"This is an arbitrary document text long enough to hold a few fake-annotations ."};
    String[] Ontology = {"entry1 entry2 entry3 entry2 somethingelse"};
    String[] Ontology_begin = {"0 0 0 0 0"};
    String[] Ontology_end = {"2 2 2 2 2"};
    String[] Result = {"replacement1;replacement2;replacement3;replacement2;somethingelse;"};

    public void init_jcas(JCas jcas, String ontology, String ontology_begin, String ontology_end) {
        //split sentence to tokens
        String[] words = ontology.split(" ");
        String[] begin = ontology_begin.split(" ");
        String[] end = ontology_end.split(" ");

        //loop for all words
        for (int i=0; i< words.length; i++) {
            OntClassMention ocm = new OntClassMention(jcas, Integer.parseInt(begin[i]), Integer.parseInt(end[i]));
            ocm.setSourceOntology(words[i]);
            ocm.addToIndexes();
        }
    }

    @Test
    public void testProcess() throws IOException, UIMAException {
        ExternalResourceDescription extDesc = ExternalResourceFactory.createExternalResourceDescription(
                FeatureValueReplacementsProvider.class, new File("src/test/resources/Feature_Value_Replacement/testReplacementFile.map"));
        AnalysisEngine feature_value_replacement_Annotator = AnalysisEngineFactory.createEngine(FeatureValueReplacementAnnotator.class,
                FeatureValueReplacementAnnotator.PARAM_FEATURE_PATHS,
                new String[] { "de.julielab.jcore.types.OntClassMention=/sourceOntology" },
                FeatureValueReplacementAnnotator.RESOURCE_REPLACEMENTS, extDesc);

        for (int i=0; i<Text.length; i++){
            // initialize jcas
            JCas jcas = JCasFactory.createText(Text[i]);

            init_jcas(jcas, Ontology[i], Ontology_begin[i], Ontology_end[i]);
            feature_value_replacement_Annotator.process(jcas);

            String predicted_ocm = "";

            for (OntClassMention ocm : JCasUtil.select(jcas, OntClassMention.class))
            {
                predicted_ocm = predicted_ocm + ocm.getSourceOntology() + ";" ;
            }
            String correct_ocm = Result[i];
//
//            //compare the result
            assertEquals(correct_ocm, predicted_ocm);
        }

    }
}
