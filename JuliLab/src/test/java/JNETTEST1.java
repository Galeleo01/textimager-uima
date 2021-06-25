import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.XMLInputSource;
import org.junit.Test;
import de.julielab.jcore.types.Sentence;
import de.julielab.jcore.types.Token;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class JNETTEST1 {

    String[] Text = {"Identification of cDNAs encoding two human alpha class glutathione transferases ( GSTA3 and GSTA4 ) and the heterologous expression of GSTA4E - 4 ."};
    String[] NER = {"NN;IN;NNS;VBG;CD;JJ;SYM;NN;NN;NNS;-LRB-;NN;CC;NN;-RRB-;CC;DT;JJ;NN;IN;NN;HYPH;CD;.;"};

    public void init_jcas(JCas jcas, String text) {
        //split sentence to tokens
        String[] words = text.split(" ");

        //create Sentence
        Sentence sentence = new Sentence(jcas);
        sentence.setBegin(0);
        sentence.setEnd(text.length());
        sentence.addToIndexes();

        //initialize index
        int index_start = 0;
        int index_end = 0;

        //loop for all words
        for (String word : words) {
            index_end = index_start + word.length();
            Token token = new Token(jcas);
            token.setBegin(index_start);
            token.setEnd(index_end);
            token.addToIndexes();
            index_start = index_end + 1;
        }
    }

    @Test
    public void train() throws IOException, UIMAException {
        XMLInputSource jnet_xml = new XMLInputSource("src/test/resources/jnet/jcore-jnet-ae.xml");
        ResourceSpecifier jnet_spec = UIMAFramework.getXMLParser().parseResourceSpecifier(jnet_xml);
        AnalysisEngine jnet = UIMAFramework.produceAnalysisEngine(jnet_spec);

        for (int i = 0; i<Text.length; i++) {
            JCas jcas = JCasFactory.createText(Text[i]);
            init_jcas(jcas, Text[i]);

            jnet.process(jcas);

            // get the parsing
            String predicted_ner = "";

//            for (Token token : JCasUtil.select(jcas, Token.class))
//            {
//                predicted_ner = predicted_ner + token. + ";" ;
//            }
//
//            String correct_lemma = NER[i];
//
//            //compare the result
//            assertEquals(correct_lemma, predicted_ner);
        }


    }

    //    String path = "src/test/resources/jnet/JNETTest.txt";
//
//    public ArrayList<Sentence> read_file(String path) throws IOException {
//        String line;
//        BufferedReader reader = null;
//        ArrayList<Sentence> sentences = new ArrayList<Sentence>();
//
//        reader = new BufferedReader(new FileReader(path));
//
//        while ((line = reader.readLine()) != null) {
//            Sentence sent = tokenizer(line);
//            sentences.add(sent);
//            System.out.println(line);
//        }
//        return sentences;
//    }


}
