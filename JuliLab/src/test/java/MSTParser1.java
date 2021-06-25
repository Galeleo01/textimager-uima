import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.junit.Test;
import org.xml.sax.SAXException;
import static org.junit.Assert.assertTrue;

import java.io.*;

public class MSTParser1 {
    @Test
    public void testProcess() throws IOException, InvalidXMLException, ResourceInitializationException, SAXException, CASException, AnalysisEngineProcessException {
    XMLInputSource mstparser_XML = new XMLInputSource("src/test/resources/MSTParser>/jcore-mstparser.xml");
    ResourceSpecifier mstparser_Spec = UIMAFramework.getXMLParser().parseResourceSpecifier(mstparser_XML);
    AnalysisEngine mstparser_AnalysisEngine = UIMAFramework.produceAnalysisEngine(mstparser_Spec);

    CAS cas = mstparser_AnalysisEngine.newCAS();
    FileInputStream fis = new FileInputStream("src/test/resources/MSTParser/de/julielab/jcore/ae/mstparser/data/input/news_text_stp.xmi");
        XmiCasDeserializer.deserialize(fis, cas);
    JCas jcas = cas.getJCas();
    mstparser_AnalysisEngine.process(jcas);

    FileOutputStream fos = new FileOutputStream("src/test/resources/MSTParser/de/julielab/jcore/ae/mstparser/data/output" + File.separator + "test.xmi");
        XmiCasSerializer.serialize(jcas.getCas(), fos);

//    assertTrue("Invalid JCas!", checkAnnotations(jcas, null);

    }
}
