import de.julielab.jcore.ae.biosem.BioSemEventAnnotator;
import de.julielab.jcore.ae.biosem.DBUtilsProviderImpl;
import org.apache.commons.io.IOUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ExternalResourceDescription;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class Biosem1 {
//    JCas jcas = JCasFactory.createJCas();
//    CollectionReader bioNlpSTReader = CollectionReaderFactory.createReader(BioEventReader.class,
//            BioEventReader.DIRECTORY_PARAM, "src/test/resources/st09-traindoc/",
//            BioEventReader.BIOEVENT_SERVICE_MODE_PARAM, false);
//    ExternalResourceDescription dbResourceDescription = ExternalResourceFactory.createExternalResourceDescription(
//            DBUtilsProviderImpl.class,
//            "file:src/test/resources/de/julielab/jcore/ae/biosemannotator.test.properties");
//    AnalysisEngine engine = AnalysisEngineFactory.createEngine(BioSemEventAnnotator.class,
//            BioSemEventAnnotator.RESOURCE_TRAINED_DB, dbResourceDescription);
//    AnalysisEngine bioNlpSTWriter = AnalysisEngineFactory.createEngine(BioEventConsumer.class,
//            BioEventConsumer.DIRECTORY_PARAM, "src/test/resources/test-predict-out",
//            BioEventConsumer.BIOEVENT_SERVICE_MODE_PARAM, false);
//
//    // first, delete the possibly already existing old test output file
//    File testOutputFile = new File("src/test/resources/test-predict-out/1313226.a2");
//		if (testOutputFile.exists())
//                testOutputFile.delete();
//
//    assertTrue("Test document was not found by the BioNLP ST reader.", bioNlpSTReader.hasNext());
//		bioNlpSTReader.getNext(jCas.getCas());
//		engine.process(jCas);
//		bioNlpSTWriter.process(jCas);
//
//    // now load the predicted file we created by hand (seen as 'correct' in
//    // this context, although there actually might be tagging errors in it)
//    // and the file we just created and compare them
//    List<String> expectedLines = IOUtils
//            .readLines(new FileInputStream("src/test/resources/st09-predicted/1313226.a2"));
//    List<String> actualLines = IOUtils
//            .readLines(new FileInputStream("src/test/resources/test-predict-out/1313226.a2"));
//    // sort the lines because the order varies from run to run since the
//    // results are internally stored in a set
//		Collections.sort(expectedLines);
//		Collections.sort(actualLines);
//
//    assertEquals(expectedLines, actualLines);
//
//    public Biosem1() throws UIMAException, IOException {
//    }
}
