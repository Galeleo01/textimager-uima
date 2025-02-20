package org.hucompute.textimager.uima.geonames.gazetteer;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.core.languagetool.LanguageToolSegmenter;
import org.junit.jupiter.api.Test;
import org.texttechnologylab.annotation.GeoNamesEntity;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.fail;

public class TestGeonamesGazetteer {

	//private final String sourceLocation = "/media/gabrami/85ff0921-743b-48ce-8962-07a08a9db03e/Arbeit/geonames/test.txt";
	private final String sourceLocation = "/home/daniel/data/hiwi/geonames_sample.txt";

	@Test
	public void testGeonamesGazetteer() {
		try {
			final AnalysisEngine segmewnter = AnalysisEngineFactory.createEngine(LanguageToolSegmenter.class);

			final AnalysisEngine gazetterEngine = AnalysisEngineFactory.createEngine(AnalysisEngineFactory.createEngineDescription(
					GeonamesGazetteer.class,
					GeonamesGazetteer.PARAM_SOURCE_LOCATION, sourceLocation,
					GeonamesGazetteer.PARAM_TAGGING_TYPE_NAME, GeoNamesEntity.class.getName(),
					GeonamesGazetteer.PARAM_MAPPING_PROVIDER_LOCATION, "classpath:/org/hucompute/textimager/uima/geonames/gazetteer/lib/ner-default.map",
					GeonamesGazetteer.PARAM_USE_LOWERCASE, false,
					GeonamesGazetteer.PARAM_USE_STRING_TREE, true,
					GeonamesGazetteer.PARAM_USE_SENTECE_LEVEL_TAGGING, true,
					GeonamesGazetteer.PARAM_USE_LEMMATA, true,
					GeonamesGazetteer.PARAM_NO_SKIPGRAMS, true,
					GeonamesGazetteer.PARAM_ADD_ABBREVIATED_TAXA, false,
					GeonamesGazetteer.PARAM_GET_ALL_SKIPS, false,
					GeonamesGazetteer.PARAM_MIN_LENGTH, 1,
					GeonamesGazetteer.PARAM_SPLIT_HYPEN, false,
					GeonamesGazetteer.PARAM_ANNOTATION_COMMENTS, new String[]{ "ttlab_model", "ttlab_geonames_v_1.0.1" }
			));

			runTest(segmewnter, gazetterEngine);
		} catch (UIMAException e) {
			e.printStackTrace();
			fail();
		}
	}

	private void runTest(AnalysisEngine segmenter, AnalysisEngine gazetterEngine) throws UIMAException {
		JCas jCas = JCasFactory.createText("Svarthornbotnen liegt im Norden von Svarthornbotnen und Svarthornbotnen im Süden.");
		jCas.setDocumentLanguage("de");
		StopWatch stopWatch = StopWatch.createStarted();
		SimplePipeline.runPipeline(jCas, segmenter, gazetterEngine);
		System.out.printf("Finished tagging in %dms.\n", stopWatch.getTime(TimeUnit.MILLISECONDS));

		System.out.printf("Found %d GeoNames.\n", JCasUtil.select(jCas, GeoNamesEntity.class).size());
		System.out.println(JCasUtil.select(jCas, GeoNamesEntity.class).stream().map(element -> String.format("%s@(%d, %d): %s", element.getCoveredText(), element.getBegin(), element.getEnd(), element.getId())).collect(Collectors.joining("\n")));
	}

}
