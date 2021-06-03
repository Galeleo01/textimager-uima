import cc.mallet.pipe.Pipe;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import de.julielab.jcore.ae.jtbd.Unit;
import de.julielab.jcore.ae.jtbd.main.TokenAnnotator;
import de.julielab.jcore.types.Token;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import de.julielab.jcore.ae.jtbd.Tokenizer;


public class JTBDTest {

    public List<String> read_file(String path) throws IOException {
        String line;
        BufferedReader reader = null;
        List<String> sentence_list = new ArrayList<String>();

        reader = new BufferedReader(new FileReader(path));

        while ((line = reader.readLine()) != null) {
            sentence_list.add(line);
            System.out.println(line);
        }
        return sentence_list;
    }




//    @Test
//    public void train() throws IOException {
//        Tokenizer tokenizer = new Tokenizer();
//        List<String> path_list = Arrays.asList("src/test/resources/JTBD/testdata/train/train.sent", "src/test/resources/JTBD/testdata/train/train.tok");
//
//        List<String> org_sentences = read_file(path_list.get(0));
//        List<String> tok_sentences = read_file(path_list.get(1));
//
//        InstanceList trainData = tokenizer.makeTrainingData(org_sentences, tok_sentences);
//        Pipe trainPipe = trainData.getPipe();
//        tokenizer.train(trainData, trainPipe);

//    }

}
