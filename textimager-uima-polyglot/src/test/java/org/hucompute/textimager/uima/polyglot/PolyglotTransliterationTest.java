package org.hucompute.textimager.uima.polyglot;

import static org.apache.uima.fit.util.JCasUtil.select;
import static org.junit.Assert.*;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

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
public class PolyglotTransliterationTest {
	
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

		AnalysisEngineDescription polyglottransliteration = createEngineDescription(PolyglotTransliteration.class);

		SimplePipeline.runPipeline(cas, polyglottransliteration);
		// Sample Text Transliteration
		String outputCorrectToken = "Das | Essen | gestern | Nacht | hat | großartig | geschmeckt | obwohl | der | Fisch | schlecht | ";
		String outputCorrectValue ="داس | يسين | جتيرن | ناكهت | هات |  | جكهميكت | وبووهل | دير | فيش | شليكهت";
		String outputCorrectBegin = "0 | 4 | 10 | 18 | 24 | 28 | 38 | 50 | 57 | 61 | 67 | ";
		String outputCorrectEnd = "3 | 9 | 17 | 23 | 27 | 37 | 48 | 56 | 60 | 66 | 75 | ";

		//POS
		AnalysisEngineDescription polyglottpos = createEngineDescription(PolyglotPOS.class);
		SimplePipeline.runPipeline(cas, polyglottpos);
//
//		String outputCorrectToken = "Deutschland | ist | ein | Bundesstaat | in | Mitteleuropa | . | Er | besteht | aus | 16 | Ländern | und | ist | als | freiheitlich | - | demokratischer | und | sozialer | Rechtsstaat | verfasst | . | Die | Bundesrepublik | Deutschland | stellt | die | jüngste | Ausprägung | des | deutschen | Nationalstaates | dar | . | ";
//		String outputCorrectValue = "PROPN | VERB | DET | NOUN | ADP | PROPN | PUNCT | PRON | VERB | ADP | NUM | NOUN | CONJ | VERB | ADP | ADJ | PUNCT | ADJ | CONJ | ADJ | NOUN | VERB | PUNCT | DET | PROPN | PROPN | VERB | DET | ADJ | NOUN | DET | ADJ | NOUN | ADV | PUNCT | ";
//		String outputCorrectBegin = "0 | 12 | 16 | 20 | 32 | 35 | 47 | 49 | 52 | 60 | 64 | 67 | 75 | 79 | 83 | 87 | 99 | 100 | 115 | 119 | 128 | 140 | 148 | 150 | 154 | 169 | 181 | 188 | 192 | 200 | 211 | 215 | 225 | 241 | 244 | ";
//		String outputCorrectEnd = "11 | 15 | 19 | 31 | 34 | 47 | 48 | 51 | 59 | 63 | 66 | 74 | 78 | 82 | 86 | 99 | 100 | 114 | 118 | 127 | 139 | 148 | 149 | 153 | 168 | 180 | 187 | 191 | 199 | 210 | 214 | 224 | 240 | 244 | 245 | ";


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
		for (TransliterationAnnotation t : JCasUtil.select(cas, TransliterationAnnotation.class)) {
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
