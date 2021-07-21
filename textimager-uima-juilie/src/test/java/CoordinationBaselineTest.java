import de.julielab.jcore.types.Entity;
import de.julielab.jcore.types.POSTag;
import de.julielab.jcore.types.Token;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.junit.Test;

import java.io.IOException;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

public class CoordinationBaselineTest {
    String Text = "Almost all of these mutations occur in X , Y , and Z cells ; simple upstream and downstream sequence elements are indicated by negative elements .";
    String PosTags = "RB DT IN DT NNS VBP IN NN , NN , CC NN NNS ; JJ JJ CC JJ NN NNS VBP VBD IN JJ NNS .";
    String Entity = "variation-event variation-location DNA";
    String Entity_Begin = "20 39 61";
    String Entity_End = "29 58 109";
    String EEE = "X , Y , and Z cells;simple upstream and downstream sequence elements;";
    String ellipsis = "X cells, Y cells, and Z cells;simple upstream sequence elements and simple downstream sequence elements;";
    String coordination_labels = "conjunct;conjunction;conjunct;conjunction;conjunction;conjunct;antecedent;antecedent;conjunct;conjunction;conjunct;antecedent;antecedent;";
    public void init_jcas(JCas jcas, String sentences, String postags, String entities, String entities_begin, String entities_end) {
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
            de.julielab.jcore.types.Entity ent = new Entity(jcas);
            ent.setBegin(Integer.parseInt(entity_begin[i]));
            ent.setEnd(Integer.parseInt(entity_end[i]));
            ent.setSpecificType(entity[i]);
            ent.addToIndexes();
        }
    }
    @Test
    public void testProcess() throws IOException, UIMAException {
        JCas jCas = JCasFactory.createText(Text);
        init_jcas(jCas, Text, PosTags, Entity, Entity_Begin, Entity_End);

        AnalysisEngineDescription engine = createEngineDescription(CoordinationBaseline.class, CoordinationBaseline.PARAM_REST_ENDPOINT, "http://localhost:8080");

        SimplePipeline.runPipeline(jCas, engine);

        //String stop = "";
    }
}
