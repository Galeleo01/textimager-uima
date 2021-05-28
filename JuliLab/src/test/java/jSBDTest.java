import de.julielab.jcore.ae.jsbd.*;
import de.julielab.jcore.ae.jsbd.main.SentenceAnnotator;
import de.julielab.jcore.types.*;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JFSIndexRepository;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.XMLInputSource;
import org.junit.Test;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.junit.Assert.*;

public class jSBDTest {

    @Test
    public void fitIntegrationTest() throws Exception
    {
        XMLInputSource senXML = new XMLInputSource("src/test/resources/BSD/SentenceAnnotatorTest.xml");
        ResourceSpecifier senSpec = UIMAFramework.getXMLParser().parseResourceSpecifier(senXML);
        AnalysisEngine senAnnotator = UIMAFramework.produceAnalysisEngine(senSpec);


        JCas cas = JCasFactory.createJCas("de.julielab.jcore.types.jcore-morpho-syntax-types");
        cas.setDocumentText("A simple sentence.\n" + "Another one.");

        senAnnotator.process(cas);

        Collection<Sentence> sentences = JCasUtil.select(cas, Sentence.class);
        for (Sentence sentence : sentences) {
            System.out.println(sentence.getCoveredText());
        }

        assertEquals(2, sentences.size());
    }

    @Test
    public void newLineTest() throws Exception
    {
        XMLInputSource senXML = new XMLInputSource("src/test/resources/BSD/SentenceAnnotatorTest.xml");
        ResourceSpecifier senSpec = UIMAFramework.getXMLParser().parseResourceSpecifier(senXML);
        AnalysisEngine senAnnotator = UIMAFramework.produceAnalysisEngine(senSpec);


        JCas cas = JCasFactory.createJCas("de.julielab.jcore.types.jcore-morpho-syntax-types");
        cas.setDocumentText("A simple sentence.\n" + "Another one.");

        senAnnotator.process(cas);

        Collection<String> sentences = JCasUtil.select(cas, Sentence.class).stream().map(Annotation::getCoveredText).collect(Collectors.toList());

        //assertThat(sentences).containsExactly("line1", "line2", "line3");
        assertTrue(sentences.contains("A simple sentence."));
        assertTrue(sentences.contains("Another one."));


    }
}
