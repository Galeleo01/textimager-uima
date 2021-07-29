import de.julielab.jcore.types.LikelihoodIndicator;
import de.julielab.jcore.types.Token;
import de.julielab.jcore.types.Lemma;
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

public class LikelihoodDetectionTest {
    public void init_jcas(JCas jcas, String[] lemmas) {
        //initialize index
        int index_start = 0;
        int index_end = 0;
        int len = lemmas.length;

        //loop for all words
        for (int i=0; i < len; i++) {
            index_end = index_start + lemmas[i].length();

            Lemma lemma = new Lemma(jcas);
            lemma.setBegin(index_start);
            lemma.setEnd(index_end);
            lemma.setValue(lemmas[i]);
            lemma.addToIndexes();

            index_start = index_end + 1;
        }

    }
    @Test
    public void likelihoodDetectionTest() throws IOException, UIMAException {
        String Text = "PML appears to be transcriptionally regulated by class I and II interferons , which raises the possibility that interferons modulate the function and growth and differentiation potential of normal myeloid cells and precursors .";
        JCas jCas = JCasFactory.createText(Text);
        jCas.setDocumentLanguage("en");

        //test zwecke
        //AnalysisEngineDescription segmenter = createEngineDescription(LanguageToolSegmenter.class);
        //SimplePipeline.runPipeline(jCas, segmenter);

        //get lemma from standfordlemmatizer
        AnalysisEngineDescription engine_lemma = createEngineDescription(StanfordLemmatizer.class, StanfordLemmatizer.PARAM_REST_ENDPOINT, "http://localhost:8080");

        SimplePipeline.runPipeline(jCas, engine_lemma);

        String[] casLemma = (String[]) JCasUtil.select(jCas, Token.class).stream().map(b -> b.getLemma().getValue()).toArray(String[]::new);
        //LikelihoodDectection
        init_jcas(jCas, casLemma);
        AnalysisEngineDescription engine = createEngineDescription(LikelihoodDetection.class, LikelihoodDetection.PARAM_REST_ENDPOINT, "http://localhost:8080");

        SimplePipeline.runPipeline(jCas, engine);

        // indicator
        String[] casLikelihoodIndicator = (String[]) JCasUtil.select(jCas, LikelihoodIndicator.class).stream().map(a -> a.getCoveredText()).toArray(String[]::new);

        String[] testcasLikelihoodIndicator= new String[] {
                "appears","raises the possibility",
        };
        // category
        String[] casLikelihoodCategory = (String[]) JCasUtil.select(jCas, LikelihoodIndicator.class).stream().map(b -> b.getLikelihood()).toArray(String[]::new);

        String[] testcasLikelihoodCategory= new String[] {
                "moderate","moderate"
        };
        assertArrayEquals(testcasLikelihoodIndicator, casLikelihoodIndicator);
        assertArrayEquals(testcasLikelihoodCategory, casLikelihoodCategory);
    }
}