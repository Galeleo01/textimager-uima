import de.julielab.jcore.types.Token;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import de.julielab.jcore.ae.jnet.tagger.NETagger;
import de.julielab.jcore.ae.jnet.tagger.Sentence;
import de.julielab.jcore.ae.jnet.tagger.Unit;

public class JNETTEST {
    static String path = "src/test/resources/jnet/JNETTest.txt";

    @Test
    public void tagger_train() throws IOException {
        ArrayList<Sentence> sentences = read_file(path);
        NETagger netagger = new NETagger();
        netagger.train(sentences);
    }


    public ArrayList<Sentence> read_file(String path) throws IOException {
        String line;
        BufferedReader reader = null;
        ArrayList<Sentence> sentences = new ArrayList<Sentence>();

        reader = new BufferedReader(new FileReader(path));

        while ((line = reader.readLine()) != null) {
            Sentence sent = tokenizer(line);
            sentences.add(sent);
            System.out.println(line);
        }
        return sentences;
    }

    public Sentence tokenizer(String sentence) {
        //split sentence to tokens
        String[] words = sentence.split(" ");
        Sentence sent = new Sentence();

        //initialize index
        int index_start = 0;
        int index_end = 0;

        //loop for all words
        for (String word : words) {
            index_end = index_start + word.length();
            Unit token = new Unit(index_start, index_end, word);
            sent.add(token);
            index_start = index_end + 1;
        }
        return sent;
    }

}
