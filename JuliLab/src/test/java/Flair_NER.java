import de.julielab.jcore.types.*;
import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.XMLInputSource;
import org.junit.Test;

import java.io.IOException;
import static org.junit.Assert.assertEquals;

public class Flair_NER {
    String[] Text = {"Knockdown of SUB1 homolog by siRNA inhibits the early stages of HIV-1 replication in 293T cells infected with VSV-G pseudotyped HIV-1 ."};
    String[] Stemmer = {"Three;hors;were;go;contempl;around;bushi;bush;.;"};

    public void init_jcas(JCas jcas, String text) {
        //split sentence to tokens
        String[] words = text.split(" ");

        //initialize index
        int index_start = 0;
        int index_end = 0;

        //loop for all words
        for (int i=0; i< words.length; i++) {
            index_end = index_start + words[i].length();
            Token token = new Token(jcas);

            token.setBegin(index_start);
            token.setEnd(index_end);
            token.addToIndexes();

            index_start = index_end + 1;
        }
    }

    @Test
    public void testProcess() throws IOException, UIMAException {

        XMLInputSource flair_ner_XML = new XMLInputSource(
                "src/test/resources/Lingpipe_Porterstemmer/jcore-lingpipe-porterstemmer-ae.xml");
        ResourceSpecifier lingpipe_porterstemmer_Spec = UIMAFramework.getXMLParser().parseResourceSpecifier(
                flair_ner_XML);
        AnalysisEngine flair_ner_Annotator = UIMAFramework
                .produceAnalysisEngine(lingpipe_porterstemmer_Spec);

        for (int i=0; i<Text.length; i++){
            // initialize jcas
            JCas jcas = JCasFactory.createText(Text[i]);

            Sentence sentence = new Sentence(jcas);
            sentence.setBegin(0);
            sentence.setEnd(Text[i].length());
            sentence.addToIndexes();


            init_jcas(jcas, Text[i]);
            flair_ner_Annotator.process(jcas);

            // get the parsing
            String predicted_stemmer = "";
            for (Token token : JCasUtil.select(jcas, Token.class))
            {
                predicted_stemmer = predicted_stemmer + token.getStemmedForm().getValue() + ";" ;
            }
            String correct_stemmer = Stemmer[i];

            //compare the result
            assertEquals(correct_stemmer, predicted_stemmer);
        }

    }
}
