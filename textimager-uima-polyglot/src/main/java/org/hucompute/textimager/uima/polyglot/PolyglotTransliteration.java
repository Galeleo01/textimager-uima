package org.hucompute.textimager.uima.polyglot;

import static org.apache.uima.fit.util.JCasUtil.select;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.util.*;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import jep.JepException;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.SegmenterBase;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import tansliterationAnnotation.type.TransliterationAnnotation;

import static org.apache.uima.fit.util.JCasUtil.select;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import de.tudarmstadt.ukp.dkpro.core.api.resources.MappingProvider;
import de.tudarmstadt.ukp.dkpro.core.api.resources.MappingProviderFactory;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.SegmenterBase;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.uima.resource.ResourceInitializationException;
import tansliterationAnnotation.type.TransliterationAnnotation;

/**
* PolyglotTransliteration
*
* @date 20.05.2021
*
* @author Grzegorz Siwiecki
* @version 1.0
*
* This class provide Transliteration for 69 languages. (http://polyglot.readthedocs.io/en/latest/Transliteration.html) 
* UIMA-Token are needed as input to create Transliteration.
* UIMA-Standard is used to represent the final Transliteration.*/
@TypeCapability(
		inputs = {"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token"},
		outputs = {"tansliterationAnnotation.type.TransliterationAnnotation"})
public class PolyglotTransliteration  extends PolyglotBase {

	private MappingProvider mappingProvider;

	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException, ResourceInitializationException {
		super.initialize(aContext);

	}


	private void processSentences(JCas aJCas, int beginOffset) throws JepException {
		@SuppressWarnings("unchecked")
		ArrayList<HashMap<String, Object>> sents = (ArrayList<HashMap<String, Object>>) interpreter.getValue("sentiments");
		sents.forEach(p -> {
			int begin = ((Long) p.get("begin")).intValue();
			int end = ((Long) p.get("end")).intValue();
			Sentence sentAnno = new Sentence(aJCas, begin, end);
			sentAnno.addToIndexes();
		});
	}

	private Map<Integer, Map<Integer, Token>> processToken(JCas aJCas, int beginOffset) throws JepException {
		Map<Integer, Map<Integer, Token>> tokensMap = new HashMap<>();
		ArrayList<HashMap<String, Object>> output = (ArrayList<HashMap<String, Object>>) interpreter.getValue("tokens");
		Integer counter = 0;

		for (HashMap<String, Object> token : output) {
				int begin = counter;
				int end = begin + ((Long) token.get("length")).intValue();

				Token casToken = new Token(aJCas, begin, end);
				casToken.addToIndexes();
				if(((Long) token.get("length")).intValue()>1)
				{
					counter = end + 1;
					TransliterationAnnotation trans = new TransliterationAnnotation(aJCas, begin, end);
					trans.setValue((String)token.get("transliterate"));
					trans.addToIndexes();
				}
				else
				{
					counter = end;
				}
		}
		return tokensMap;
	}


	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {

		long textLength = aJCas.getDocumentText().length();
		System.out.println("text length: " + textLength);
		// abort on empty
		if (textLength < 1) {
			System.out.println("skipping spacy due to text length < 1");
			return;
		}


			List<String> texts = new ArrayList<>();
				// split text on "." near "nlp.max_length (= " characters
				StringBuilder sb = new StringBuilder();
				String[] textParts = aJCas.getDocumentText().split("\\.", 0);
				for (String textPart : textParts) {
					sb.append(textPart).append(".");
				}
				// handle rest
				if (sb.length() > 0) {
					if(!aJCas.getDocumentText().endsWith("."))
						sb.setLength(sb.length()-1);
					texts.add(sb.toString());
				}

				else {
					texts.add(aJCas.getDocumentText());
				}

			int beginOffset = 0;
			int counter = 0;
			for (String text : texts) {
				counter++;
				System.out.println("processing text part " + counter + "/" + texts.size());

				// text to python interpreter

				try {
					interpreter.exec("from polyglot.text import Text");
					interpreter.set("text", (Object) text);
					interpreter.exec("doc = Text(text)");

					interpreter.exec("tokens = [{'length': len(token),'token_text':token, 'language': token.language, 'transliterate': token.transliterate('ar')  } for token in doc.words]");
					interpreter.exec("sentiments = [{'begin': sent.start, 'end': sent.end} for sent in doc.sentences]");
					interpreter.exec("pos = [{'tag': token.pos_tag,'idx': token.index,'length': len(token),'text': token } for token in doc.words]");
					interpreter.exec("entities = [{'text': ent[0],'label': ent.post_tag}for ent in doc.entities]");
					//interpreter.exec("transliterate = [{'text': token,'transliterate': token.transliterate('ar') } for token in doc.words]");
				} catch (JepException jepException) {
					jepException.printStackTrace();
				}


				try {

					// Sentences
					processSentences(aJCas, beginOffset);

					// Tokenizer and Transliterizer
					Map<Integer, Map<Integer, Token>> tokensMap = processToken(aJCas, beginOffset);

					/*for (Token t : JCasUtil.select(aJCas, Token.class)) {
						System.out.println("!~" + t.getCoveredText() + "!~");
						System.out.println(t);
					}*/

					/*for (TransliterationAnnotation t : JCasUtil.select(aJCas, TransliterationAnnotation.class)) {
						System.out.println("!~" + t.getCoveredText() + "!~");
						System.out.println(t);
					}*/

					//processPOS(aJCas, beginOffset, tokensMap);

					// NER TODO
					//processNER(aJCas, beginOffset);


				} catch (JepException e) {
					e.printStackTrace();
				}
			}

	}
}
