import de.julielab.jcore.types.*;
import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.XMLInputSource;
import org.junit.Test;
import de.julielab.jcore.ae.opennlp.chunk.ChunkAnnotator;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class Opennlp_Chunk {
    String[] Text = {"\"A study on the Prethcamide hydroxylation system in rat hepatic microsomes ."};
    String[] Postags = {"DT NN IN DT NN NN NN IN NN JJ NNS ."};
    String[] Chunks = {"ChunkNP,ChunkPP,ChunkNP,ChunkPP,ChunkNP,"};

    public void get_cas(JCas jcas, String text, String postag) {
        //split sentence to tokens
        String[] words = text.split(" ");
        String[] postags = postag.split(" ");

        //initialize token index
        int index_start_token = 0;
        int index_end_token = 0;

        //loop for all words
        for (int i=0; i< words.length; i++) {
            Token token = new Token(jcas);
            index_end_token = index_start_token + words[i].length();
            token.setBegin(index_start_token);
            token.setEnd(index_end_token);
            token.addToIndexes();
            index_start_token = index_end_token + 1;

            POSTag pos = new POSTag(jcas);
            pos.setValue(postags[i]);
            pos.addToIndexes();

            FSArray postagss = new FSArray(jcas, 5);
            postagss.set(0, pos);
            postagss.addToIndexes();
            token.setPosTag(postagss);
            System.out.println(token.getPosTag(0));
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

            get_cas(jcas, Text[i], Postags[i]);

            open_nlp_chunk.process(jcas);

            // get the parsing
            String predicted_chunk = "";
            for (Chunk chunk : JCasUtil.select(jcas, Chunk.class))
            {
                predicted_chunk = predicted_chunk + chunk.getType().getShortName() + ",";
            }
            String correct_chunk = Chunks[i];

            //compare the result
            assertEquals(correct_chunk, predicted_chunk);
        }
    }
}
