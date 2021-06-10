import de.julielab.jcore.types.Abbreviation;
import de.julielab.jcore.types.Sentence;
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

public class Acronym {
    String[] Text = {"[TAZ]Die Firma Kohl-kopf (FK-K) hat für die Straßenverkehrsordnung (StVO) "
            + "in der Bundesrepublik Deutschland(BRD)  einen hochintelligenten Manager für die Chefetage "
            + "(HIMFDC) eingestellt und dabei jede Menge Quatsch (jMQ) gemacht  "
            + "Hydrogenovibrio marinus MH-110 possesses three different sets of genes "
            + "for ribulose 1,5-bisphosphate carboxylase/oxygenase (RubisCO)  "
            + "two form I (cbbLS-1 and cbbLS-2) and one form II (cbbM)  "
            + "However, StVO does not interact with BRD. And intracranial aneurisms (IAs) suck a lot. We show that IA causes "
            + "Multiple myeloMa (MM). That MM is really nasty stuff! "
            + "dumm-dämliches Tandemkarzinom (ddTDK) ist generell gefährlich."
            + "The proton affinity (PA) of the methyl esters increases continually. PAs are ..."};

    @Test
    public void testProcess() throws IOException, UIMAException {

        XMLInputSource acronym_XML = new XMLInputSource(
                "src/test/resources/Acronym/jcore-acronym-ae.xml");
        ResourceSpecifier acronym_Spec = UIMAFramework.getXMLParser().parseResourceSpecifier(
                acronym_XML);
        AnalysisEngine acronym_Annotator = UIMAFramework
                .produceAnalysisEngine(acronym_Spec);

        for (int i=0; i<Text.length; i++){
            // initialize jcas
            JCas jcas = JCasFactory.createText(Text[i]);
            //input
            Sentence sentence = new Sentence(jcas);
            sentence.setBegin(0);
            sentence.setEnd(Text[i].length());
            sentence.addToIndexes();

            acronym_Annotator.process(jcas);

            // get the parsing
            String predicted_abbrev = "";
            for (Abbreviation abbreviate : JCasUtil.select(jcas, Abbreviation.class))
            {
                predicted_abbrev = predicted_abbrev + abbreviate.getCoveredText() + ";" ;
            }
            System.out.println(predicted_abbrev);

            //compare the result
            //assertEquals(correct_offset, predicted_offset);
        }

    }

}
