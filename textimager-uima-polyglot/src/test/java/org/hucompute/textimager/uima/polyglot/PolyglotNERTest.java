package org.hucompute.textimager.uima.polyglot;

import static org.apache.uima.fit.util.JCasUtil.select;
import static org.junit.Assert.*;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import tansliterationAnnotation.type.TransliterationAnnotation;

import java.util.ArrayList;
import java.util.List;

/**
 * PolyglotTransliterationTest
 *
 * @date 20.05.2021
 *
 * @author Grzegorz Siwiecki
 * @version 1.0
 *
 * This class provide several test cases for different languages.
 */
public class PolyglotNERTest {

    /**
     * Test with JUnit if the transliteration is generated correctly and if the pipeline is working.
     * @throws Exception
     */
    @Test
    public void testTransliterationArabian() throws Exception {
        // Create a new Engine Description.
        //AnalysisEngineDescription languageAnnotator = createEngineDescription(PolyglotLanguage.class, PolyglotLanguage.PARAM_PYTHON_PATH, "/usr/bin/python");
        //AnalysisEngineDescription sentenceAnnotator = createEngineDescription(PolyglotSentenceBoundary.class, PolyglotSentenceBoundary.PARAM_PYTHON_PATH, "/usr/bin/python");
        //AnalysisEngineDescription tokenAnnotator = createEngineDescription(PolyglotTokenizer.class, PolyglotTokenizer.PARAM_PYTHON_PATH, "/usr/bin/python");
        //AnalysisEngineDescription transliterationAnnotator = createEngineDescription(PolyglotTransliteration.class, PolyglotTransliteration.PARAM_PYTHON_PATH, "/usr/bin/python", PolyglotTransliteration.PARAM_TO_LANGUAGE_CODE, "ar");

        // Create a new JCas - "Holder"-Class for Annotation.
        //JCas inputCas = JCasFactory.createJCas();

        // Input
        //inputCas.setDocumentText("Das Essen gestern Nacht hat großartig geschmeckt, obwohl der Fisch schlecht.");

        // Pipeline
        //SimplePipeline.runPipeline(inputCas, languageAnnotator, sentenceAnnotator, tokenAnnotator, transliterationAnnotator);
        //SimplePipeline.runPipeline(inputCas, transliterationAnnotator);


        JCas cas = JCasFactory.createText("Das Essen gestern Nacht hat großartig geschmeckt, obwohl der Fisch schlecht.", "de");

        AnalysisEngineDescription polyglotner = createEngineDescription(PolyglotNER.class);

        // Pipeline
        SimplePipeline.runPipeline(cas, polyglotner);

        // Sample Text
        String outputCorrectToken = "Deutschland | Mitteleuropa | Bundesrepublik | Deutschland | ";
        String outputCorrectValue = "I-LOC | I-LOC | I-LOC | I-LOC | ";
        String outputCorrectBegin = "0 | 35 | 154 | 169 | ";
        String outputCorrectEnd = "11 | 47 | 168 | 180 | ";


        // Generated text with library
        String outputTestToken = "";
        String outputTestValue = "";
        String outputTestBegin = "";
        String outputTestEnd = "";

        List<Integer> sen = new ArrayList<>();
        for (Sentence sentence : JCasUtil.select(cas, Sentence.class))
        {
            sen.add(sentence.getEnd());

        }
        // Loop over different TransliterationAnnotation-Tags and create the UIMA-Output.
        for (NamedEntity t : JCasUtil.select(cas, NamedEntity.class)) {
            outputTestToken = outputTestToken + t.getCoveredText() + " | ";
            if(sen.contains(t.getEnd()+1))
            {outputTestValue = outputTestValue + t.getValue();}
            else{outputTestValue = outputTestValue + t.getValue() + " | ";}
            outputTestBegin = outputTestBegin + t.getBegin() + " | ";
            outputTestEnd = outputTestEnd + t.getEnd() + " | ";
        }

        // JUnit-Test: CoveredText, Value, Begin, End
        assertEquals(outputCorrectToken, outputTestToken);
        assertEquals(outputCorrectBegin, outputTestBegin);
        assertEquals(outputCorrectEnd, outputTestEnd);
        assertEquals(outputCorrectValue, outputTestValue);
    }
}
