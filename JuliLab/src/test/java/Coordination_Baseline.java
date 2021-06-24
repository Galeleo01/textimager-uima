import de.julielab.jcore.types.*;
import de.julielab.jcore.utility.JCoReTools;
import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.XMLInputSource;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class Coordination_Baseline {
    String[] Text = {"Almost all of these mutations occur in X , Y , and Z cells ; simple upstream and downstream sequence elements are indicated by negative elements ."};
    String[] PosTags = {"RB DT IN DT NNS VBP IN NN , NN , CC NN NNS ; JJ JJ CC JJ NN NNS VBP VBD IN JJ NNS ."};
    String[] Entity = {"variation-event variation-location DNA"};
    String[] Entity_Begin = {"20 39 61"};
    String[] Entity_End = {"29 58 109"};
    String[] EEE = {"X , Y , and Z cells;simple upstream and downstream sequence elements;"};
    String[] ellipsis = {"X cells, Y cells, and Z cells;simple upstream sequence elements and simple downstream sequence elements;"};
    String[] coordination_labels = {"conjunct;conjunction;conjunct;conjunction;conjunction;conjunct;antecedent;antecedent;conjunct;conjunction;conjunct;antecedent;antecedent;"};
    public void init_jcas(JCas jcas, String sentences,  String postags, String entities, String entities_begin, String entities_end) {
        //split sentence to tokens
        String[] tok = sentences.split(" ");
        String[] pos = postags.split(" ");
        String[] entity = entities.split(" ");
        String[] entity_begin = entities_begin.split(" ");
        String[] entity_end = entities_end.split(" ");

        //initialize index
        int index_start = 0;
        int index_end = 0;
        int len = tok.length;
        int len_entity = entity.length;

        //loop for all words
        for (int i=0; i < len; i++) {
            index_end = index_start + tok[i].length();

            Token token = new Token(jcas, index_start, index_end);

            POSTag postag = new POSTag(jcas, index_start, index_end);
            postag.setValue(pos[i]);

            FSArray postag_array = new FSArray(jcas, 10);
            postag_array.set(0, postag);
            postag_array.addToIndexes();

            token.setPosTag(postag_array);
            token.addToIndexes();
            index_start = index_end + 1;
        }

        for (int i=0; i < len_entity; i++) {
            Entity ent = new Entity(jcas);
            ent.setBegin(Integer.parseInt(entity_begin[i]));
            ent.setEnd(Integer.parseInt(entity_end[i]));
            ent.setSpecificType(entity[i]);
            ent.addToIndexes();
        }
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testProcess() throws UIMAException, IOException {

        XMLInputSource coordination_xml = new XMLInputSource("src/test/resources/Coordination_Baseline/CoordinationAnnotatorTest.xml");
        ResourceSpecifier coordination_spec = UIMAFramework.getXMLParser().parseResourceSpecifier(coordination_xml);
        AnalysisEngine coordination_AnalysisEngline = UIMAFramework.produceAnalysisEngine(coordination_spec);


        for (int i = 0; i<Text.length; i++) {
            JCas jcas = JCasFactory.createText(Text[i]);

            Sentence sentence = new Sentence(jcas);
            sentence.setBegin(0);
            sentence.setEnd(Text[i].length());
            sentence.addToIndexes();

            init_jcas(jcas, Text[i], PosTags[i], Entity[i], Entity_Begin[i], Entity_End[i]);

            coordination_AnalysisEngline.process(jcas);

            // get the parsing
            String predicted_ellipsis = "";
            String predicted_EEE= "";
            String predicted_coordinationelement = "";

            for (Coordination coordination : JCasUtil.select(jcas, Coordination.class)) {
                if (coordination.getElliptical())
                {
                predicted_ellipsis = predicted_ellipsis + coordination.getResolved() + ";";
                }
            }

            for (EEE eee : JCasUtil.select(jcas, EEE.class)) {
                predicted_EEE = predicted_EEE + eee.getCoveredText() + ";";
            }

            for (CoordinationElement coordinationelement : JCasUtil.select(jcas, CoordinationElement.class)) {
                predicted_coordinationelement = predicted_coordinationelement + coordinationelement.getCat() + ";";
            }

            String correct_predicted_EEE = EEE[i];
            String correct_predicted_ellipsis= ellipsis[i];
            String correct_predicted_coordination_labels = coordination_labels[i];

            //compare the result
            assertEquals(correct_predicted_EEE, predicted_EEE);
            assertEquals(correct_predicted_coordination_labels, predicted_coordinationelement);
            assertEquals(correct_predicted_ellipsis, predicted_ellipsis);

        }
    }
}
