import de.julielab.jcore.ae.topicindexing.TopicIndexer;
import de.julielab.jcore.ae.topicindexing.TopicModelProvider;
import de.julielab.jcore.types.*;
import de.julielab.topicmodeling.businessobjects.Model;
import de.julielab.topicmodeling.services.MalletTopicModeling;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static org.junit.Assert.*;

public class Topic_Indexing {

    public HashMap<Integer, String> get_id(String path) {
        MalletTopicModeling topic_modeling = new MalletTopicModeling();
        Model model =  topic_modeling.readModel(path);
        return model.ModelIdpubmedId;
    }

    @Test
    public void testProcess() throws IOException, UIMAException {
        String model_path = "src/test/resources/Topic_Indexing/test_topic_model.ser";
        AnalysisEngineDescription topic_indexing = AnalysisEngineFactory.createEngineDescriptionFromPath(
                "src/test/resources/Topic_Indexing/jcore-topic-indexing-ae.xml",
                TopicIndexer.PARAM_TOPIC_MODEL_CONFIG, "src/test/resources/Topic＿Indexing/config_template.xml",
                TopicIndexer.PARAM_NUM_DISPLAYED_TOPIC_WORDS, 5,
                TopicIndexer.PARAM_STORE_IN_MODEL_INDEX, false
        );
        ExternalResourceFactory.createDependencyAndBind(topic_indexing, TopicIndexer.RESOURCE_KEY_MODEL_FILE_NAME, TopicModelProvider.class, new File(model_path).toURI().toURL().toString());
        AnalysisEngine topic_indexing_AnalysisEngine = AnalysisEngineFactory.createEngine(topic_indexing);
        HashMap<Integer, String> model = get_id(model_path);

        for (String i : model.values()){
            // initialize jcas
            JCas jcas = JCasFactory.createJCas();

            Header header = new Header(jcas);
            header.setDocId(i);
            header.addToIndexes();

            topic_indexing_AnalysisEngine.process(jcas);

            // get the parsing
            String predicted_abbrev = "";
            for (AutoDescriptor autoDesc : JCasUtil.select(jcas, AutoDescriptor.class))
            {
                System.out.println(autoDesc.getDocumentTopics());
            }
            System.out.println(predicted_abbrev);

            //compare the result
            //assertEquals(correct_offset, predicted_offset);
        }

    }
}
