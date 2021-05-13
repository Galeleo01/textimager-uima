package org.hucompute.textimager.uima.polyglot;

import static org.apache.uima.fit.util.JCasUtil.select;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;

import jep.JepException;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
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
import java.util.List;
import java.util.Map;

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
* @date 20.09.2017
*
* @author Alexander Sang
* @version 1.2
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

		// TODO defaults for de (stts) and en (ptb) are ok, add own language mapping later
		//mappingProvider = MappingProviderFactory.createPosMappingProvider(aContext, posMappingLocation, variant, language);

		try {
			System.out.println("initializing polyplot models...");
			interpreter.exec("nlps = {}");
		} catch (JepException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		/// DEBUG START
		// remove all token, sentences, pos, ner, dep
		//for (Sentence sentence : JCasUtil.select(aJCas, Sentence.class)) {
		//	sentence.removeFromIndexes();
		//}
		//for (NamedEntity ne : JCasUtil.select(aJCas, NamedEntity.class)) {
		//	ne.removeFromIndexes();
		//}
		//for (POS pos : JCasUtil.select(aJCas, POS.class)) {
		//	pos.removeFromIndexes();
		//}
		//for (Dependency dep : JCasUtil.select(aJCas, Dependency.class)) {
		//	dep.removeFromIndexes();
		//}
		//for (ROOT dep : JCasUtil.select(aJCas, ROOT.class)) {
		//	dep.removeFromIndexes();
		//}
		//for (Token token : JCasUtil.select(aJCas, Token.class)) {
		//	token.removeFromIndexes();
		//}
		/// DEBUG END

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
					interpreter.set("text", (Object) text);
					interpreter.exec("doc = Text(text)");

					interpreter.exec("transliterate = [x for x in doc.transliterate('ar')]");
				} catch (JepException jepException) {
					jepException.printStackTrace();
				}
				// Sentences
						//processSentences(aJCas, beginOffset);

				// Tokenizer
				//Map<Integer, Map<Integer, Token>> tokensMap = processToken(aJCas, beginOffset);

				// Tagger
				//processPOS(aJCas, beginOffset, tokensMap);

				// PARSER
				//processDep(aJCas, beginOffset, tokensMap);

				// NER
				//processNER(aJCas, beginOffset);

				beginOffset += text.length();
			}

	}
}
