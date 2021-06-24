import de.julielab.jcore.ae.linnaeus.LinnaeusSpeciesAnnotator;
import de.julielab.jcore.ae.topicindexing.TopicIndexer;
import de.julielab.jcore.ae.topicindexing.TopicModelProvider;
import de.julielab.jcore.types.*;
import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import de.julielab.jcore.ae.linnaeus.LinnaeusMatcherProviderImpl;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.XMLInputSource;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import static org.junit.Assert.assertEquals;

public class Linnaeus_Species {
    String[] Text = {"In this text we talk about humans and mice. Because a mouse is no killifish nor a caenorhabditis elegans. Thus, c. elegans is now abbreviated as well as n. furzeri ."};
    String[] Species = {"humans;mice;mouse;killifish;caenorhabditis;elegans;c. elegans;n. furzeri"};
    String[] Id = {"9606;10090;10090;34780;6239;6239;105023"};

    @Test
    public void testProcess() throws IOException, UIMAException {
        final ExternalResourceDescription linnaeus_species = ExternalResourceFactory.createExternalResourceDescription("TestConfiguration", LinnaeusMatcherProviderImpl.class, "src/test/resources/Linnaeus_Species/linnaeus-properties-test.conf");
        AnalysisEngine linnaeus_species_annotator = AnalysisEngineFactory.createEngine(LinnaeusSpeciesAnnotator.class, "LinnaeusMatcher", linnaeus_species);
//        XMLInputSource linnaeus_specie_xml = new XMLInputSource("src/test/resources/Linnaeus_Species/jcore-linnaeus-ae.xml");
//        ResourceSpecifier linnaeus_specie_spec = UIMAFramework.getXMLParser().parseResourceSpecifier(linnaeus_specie_xml);
//        AnalysisEngine linnaeus_specie_AnalysisEngline = UIMAFramework.produceAnalysisEngine(linnaeus_specie_spec);


        for (int i=0; i<Text.length; i++){
            // initialize jcas
            JCas jcas = JCasFactory.createText(Text[i]);

            linnaeus_species_annotator.process(jcas);

            // get the parsing
            String linnaeus_species_specie = "";
            String linnaeus_species_id = "";
            for (Organism org : JCasUtil.select(jcas, Organism.class))
            {
                linnaeus_species_specie = linnaeus_species_specie + org.getCoveredText() + ";" ;
                linnaeus_species_id = linnaeus_species_id + org.getId() + ";" ;
            }
            String correct_species = Species[i];
            String correct_species_id = Id[i];

            //compare the result
            assertEquals(correct_species, linnaeus_species_specie);
            assertEquals(correct_species_id, linnaeus_species_id);
        }

    }
}
