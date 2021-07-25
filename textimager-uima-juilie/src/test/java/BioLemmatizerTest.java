import de.julielab.jcore.types.POSTag;
import de.julielab.jcore.types.Token;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import java.io.IOException;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.junit.Assert.assertArrayEquals;

public class BioLemmatizerTest {
    String Text = "Three horses were going contemplatively around bushy bushes .";
    public void init_jcas(JCas jcas, String text, String[] POSTAG) {
        //split sentence to tokens
        String[] words = text.split(" ");

        //initialize index
        int index_start = 0;
        int index_end = 0;

        //loop for all words
        for (int i=0; i< words.length; i++) {
            index_end = index_start + words[i].length();
            //Token token = new Token(jcas);
            POSTag pos = new POSTag(jcas);

            /*token.setBegin(index_start);
            token.setEnd(index_end);
            token.addToIndexes();*/

            pos.setBegin(index_start);
            pos.setEnd(index_end);
            pos.setValue(POSTAG[i]);
            pos.addToIndexes();

            index_start = index_end + 1;
        }
    }
    @Test
    public void testProcess() throws IOException, UIMAException {
        JCas jCas = JCasFactory.createText(Text);
        // get postag
        AnalysisEngineDescription engine_postag = createEngineDescription(OpennlpPostag.class, OpennlpPostag.PARAM_REST_ENDPOINT, "http://localhost:8080");

        SimplePipeline.runPipeline(jCas, engine_postag);

        String[] casPostag = (String[]) JCasUtil.select(jCas, Token.class).stream().map(a -> a.getPosTag(0).getValue()).toArray(String[]::new);
        jCas.reset();
        jCas.setDocumentText(Text);

        init_jcas(jCas, Text, casPostag);
        AnalysisEngineDescription engine = createEngineDescription(BioLemmatizer.class, BioLemmatizer.PARAM_REST_ENDPOINT, "http://localhost:8080");

        SimplePipeline.runPipeline(jCas, engine);

        String[] casLemma = (String[]) JCasUtil.select(jCas, Token.class).stream().map(b -> b.getLemma().getValue()).toArray(String[]::new);
        String[] testLemma = new String[] {"three", "horse", "be", "go", "contemplative",
                                            "around", "bushy", "bush", "."};

        assertArrayEquals(testLemma, casLemma);
    }

}
