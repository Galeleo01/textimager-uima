import de.julielab.jcore.types.*;
import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.XMLInputSource;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import java.io.IOException;

public class Stanford_Lemmatizer {
    String[] Text = {"Plectranthus barbatus is a medicinal plant used to treat a wide range of disorders including seizure ."};
    String[] Postag = {"NN NN VBZ DT JJ NN VBN TO VB DT JJ NN IN NNS VBG NN ."};
    String[] Lemma = {"plectranthus;barbatus;be;a;medicinal;plant;use;to;treat;a;wide;range;of;disorder;include;seizure;.;"};

    public void init_jcas(JCas jcas, String text, String POSTAG) {
        //split sentence to tokens
        String[] words = text.split(" ");
        String[] postags = POSTAG.split(" ");

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

            POSTag pos = new POSTag(jcas);
            pos.setValue(postags[i]);
            pos.addToIndexes();

            FSArray postagss = new FSArray(jcas, 5);
            postagss.set(0, pos);
            postagss.addToIndexes();
            token.setPosTag(postagss);

            index_start = index_end + 1;
        }
    }

    @Test
    public void testProcess() throws IOException, UIMAException {

        XMLInputSource stanford_lemmatizer_XML = new XMLInputSource(
                "src/test/resources/Stanford_Lemmatizer/jcore-stanford-lemmatizer.xml");
        ResourceSpecifier stanford_lemmatizer_Spec = UIMAFramework.getXMLParser().parseResourceSpecifier(
                stanford_lemmatizer_XML);
        AnalysisEngine stanford_lemmatizer_Annotator = UIMAFramework
                .produceAnalysisEngine(stanford_lemmatizer_Spec);

        for (int i=0; i<Text.length; i++){
            // initialize jcas
            JCas jcas = JCasFactory.createText(Text[i]);

            init_jcas(jcas, Text[i], Postag[i]);
            stanford_lemmatizer_Annotator.process(jcas);

            // get the parsing
            String predicted_standford_lemma = "";

            for (Token token : JCasUtil.select(jcas, Token.class))
            {
                predicted_standford_lemma = predicted_standford_lemma + token.getLemma().getValue() + ";" ;
            }
            String correct_lemma = Lemma[i];

            //compare the result
            assertEquals(correct_lemma, predicted_standford_lemma);
        }

    }
}
