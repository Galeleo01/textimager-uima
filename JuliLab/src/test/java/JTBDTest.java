import cc.mallet.pipe.Pipe;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import de.julielab.jcore.ae.jtbd.Unit;
import de.julielab.jcore.ae.jtbd.main.TokenAnnotator;
import de.julielab.jcore.types.ace.Sentence;
import de.julielab.jcore.types.Token;
import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;
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
    String[] Text = {"X-inactivation, T-cells and CD44 are XYZ! CD44-related stuff is\t(not)."};
    String[] Offsets = {"0-1;1-2;2-14;14-15;16-23;24-27;28-32;33-36;37-40;40-41;42-46;46-47;47-54;55-60;61-63;64-65;65-68;68-69;69-70;"};

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

    @Test
    public void train() throws IOException, UIMAException {
        XMLInputSource jtbd_XML = new XMLInputSource(
                "src/test/resources/JTBD/desc/TokenAnnotatorTest.xml");
        ResourceSpecifier jtbd_Spec = UIMAFramework.getXMLParser().parseResourceSpecifier(
                jtbd_XML);
        AnalysisEngine jtbd_Annotator = UIMAFramework
                .produceAnalysisEngine(jtbd_Spec);

        for (int i=0; i<Text.length; i++){
            // initialize jcas
            JCas jcas = JCasFactory.createText(Text[i]);

            Sentence sentence = new Sentence(jcas);
            sentence.setBegin(0);
            sentence.setEnd(Text[i].length());
            sentence.addToIndexes();

            jtbd_Annotator.process(jcas);

            // get the parsing
            String predicted_offset = "";
            for (Token token : JCasUtil.select(jcas, Token.class))
            {
                predicted_offset = predicted_offset + token.getBegin() + "-" + token.getEnd() + ";" ;
            }
            String correct_offset = Offsets[i];
            //compare the result
            assertEquals(correct_offset, predicted_offset);
        }

    }

}
