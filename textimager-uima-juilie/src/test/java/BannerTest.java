import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import java.io.IOException;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

public class BannerTest {
    String Text = "Ten out-patients with pustulosis palmaris et plantaris were examined with direct immunofluorescence (IF) technique for deposition of fibrinogen, fibrin or its degradation products (FR-antigen) in affected and unaffected skin, together with heparin-precipitable fraction (HPF), cryoglobulin and total plasma fibrinogen in the blood.";
    @Test
    public void testProcess() throws IOException, UIMAException {
        JCas jCas = JCasFactory.createText(Text);
        AnalysisEngineDescription engine = createEngineDescription(Banner.class, Banner.PARAM_REST_ENDPOINT, "http://localhost:8080");

        SimplePipeline.runPipeline(jCas, engine);

        //String stop = "";
    }
}
