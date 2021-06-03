import de.julielab.jcore.types.Chunk;
import de.julielab.jcore.types.PennBioIEPOSTag;
import de.julielab.jcore.types.Sentence;
import de.julielab.jcore.types.Token;
import junit.framework.TestCase;
import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JFSIndexRepository;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.XMLInputSource;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class Opennlp_chunk {
    String[] Text = {"A study on the Prethcamide"};
    String[] Postags = {"DT;NN;IN;DT;NN;"};
    String[] Chunks = {"ChunkNP,ChunkPP,ChunkNP,ChunkPP,ChunkNP,"};

    public void get_cas(JCas jcas, String text) {
        //split sentence to tokens
        String[] words = text.split(" ");

        //initialize index
        int index_start = 0;
        int index_end = 0;

        //loop for all words
        for (String word : words) {
            Token token = new Token(jcas);
            index_end = index_start + word.length();
            token.setBegin(index_start);
            token.setEnd(index_end);
            token.addToIndexes();
            index_start = index_end + 1;
        }
    }

    @Test
    public void test() throws IOException, UIMAException {

        XMLInputSource posXML = new XMLInputSource("src/test/resources/Opennlp_Chunk/ChunkAnnotatorTest.xml");
        ResourceSpecifier posSpec = UIMAFramework.getXMLParser().parseResourceSpecifier(posXML);
        AnalysisEngine open_nlp_chunk = UIMAFramework.produceAnalysisEngine(posSpec);

        for (int i=0; i<Text.length; i++){
            // initialize jcas
            JCas jcas = JCasFactory.createText(Text[i]);

            Sentence sentence = new Sentence(jcas);
            sentence.setBegin(0);
            sentence.setEnd(Text[i].length());
            sentence.addToIndexes();

            get_cas(jcas, Text[i]);

            open_nlp_chunk.process(jcas);

            // get the parsing
            String predicted_chunk = "";
            for (Chunk chunk : JCasUtil.select(jcas, Chunk.class))
            {
                predicted_chunk = predicted_chunk + chunk.getType().getShortName() + ";";
            }
            String correct_chunk = Chunks[i];

            //compare the result
            assertEquals(correct_chunk, predicted_chunk);
        }
    }
}
