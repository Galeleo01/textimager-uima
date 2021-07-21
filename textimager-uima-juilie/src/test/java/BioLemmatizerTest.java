import de.julielab.jcore.types.POSTag;
import de.julielab.jcore.types.Token;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import java.io.IOException;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

public class BioLemmatizerTest {
    String Text = "Three horses were going contemplatively around bushy bushes.";
    String Postag = "DT NNS VBD VBG RB IN JJ NNS .";
    public void init_jcas(JCas jcas, String text, String POSTAG) {
        //split sentence to tokens
        String[] words = text.split(" ");
        String[] postags = POSTAG.split(" ");

        //initialize index
        int index_start = 0;
        int index_end = 0;

        //loop for all words
        for (int i=0; i< words.length; i++) {
            index_end = index_start + words[i].length();
            Token token = new Token(jcas);
            POSTag pos = new POSTag(jcas);

            token.setBegin(index_start);
            token.setEnd(index_end);
            token.addToIndexes();

            pos.setBegin(index_start);
            pos.setEnd(index_end);
            pos.setValue(postags[i]);
            pos.addToIndexes();

            index_start = index_end + 1;
        }
    }
    @Test
    public void testProcess() throws IOException, UIMAException {
        JCas jCas = JCasFactory.createText("Three horses were going contemplatively around bushy bushes.");
        init_jcas(jCas, Text, Postag);
        AnalysisEngineDescription engine = createEngineDescription(BioLemmatizer.class, BioLemmatizer.PARAM_REST_ENDPOINT, "http://localhost:8080");

        SimplePipeline.runPipeline(jCas, engine);

        //String stop = "";
    }

}
