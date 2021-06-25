import de.julielab.jcore.types.POSTag;
import de.julielab.jcore.types.Sentence;
import de.julielab.jcore.types.Token;
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

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class Flair_token_embeddings {
    String[] Text = {"Dysregulated inflammation leads to morbidity and mortality in neonates . 97 healthy subjects were enrolled in the present study ."};
    String[] Postag = {"NN NN VBZ DT JJ NN VBN TO VB DT JJ NN IN NNS VBG NN ."};
    String[] Lemma = {"plectranthus;barbatus;be;a;medicinal;plant;use;to;treat;a;wide;range;of;disorder;include;seizure;.;"};

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

        XMLInputSource flair_token_embedding_XML = new XMLInputSource(
                "src/test/resources/Flair_token_embedding/jcore-flair-token-embedding-ae.xml");
        ResourceSpecifier flair_token_embedding_Spec = UIMAFramework.getXMLParser().parseResourceSpecifier(
                flair_token_embedding_XML);
        AnalysisEngine flair_token_embedding_Annotator = UIMAFramework
                .produceAnalysisEngine(flair_token_embedding_Spec);

        for (int i=0; i<Text.length; i++){
            // initialize jcas
            JCas jcas = JCasFactory.createText(Text[i]);

            Sentence sentence = new Sentence(jcas);
            sentence.setBegin(0);
            sentence.setEnd(Text[i].length());
            sentence.addToIndexes();

            init_jcas(jcas, Text[i]);
            flair_token_embedding_Annotator.process(jcas);

            // get the parsing
            String predicted_flair_token_embedding = "";

            for (Token token : JCasUtil.select(jcas, Token.class))
            {
                predicted_flair_token_embedding = predicted_flair_token_embedding + token.getEmbeddingVectors(0).getSource() + ";" ;
            }
            System.out.println(predicted_flair_token_embedding);
//            String correct_lemma = Lemma[i];

            //compare the result
//            assertEquals(correct_lemma, predicted_standford_lemma);
        }

    }
}
