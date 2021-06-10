import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;
import org.junit.Test;
import de.julielab.jcore.types.Sentence;
import de.julielab.jcore.types.Token;

import java.io.IOException;

public class JNETTEST {
    String[] Text = {"Small ell carcinoma of the gallbladder : a clinicopathologic , immunohistochemical , and molecular pathology study of 12 cases ."};
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
//
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

        for (String s : Text) {
            JCas jcas = JCasFactory.createText(s);
            init_jcas(jcas, jcas.getDocumentText());

            jnet.process(jcas);
        }


    }

//    @Test
//    public void tagger_predict(){
//
//        Sentence sentence = tokenizer("Hello world");
//        NETagger netagger = new NETagger();
//        netagger.predict(sentence, true);
//
//        System.out.println(sentence.get(0).getLabel());
//    }



}
