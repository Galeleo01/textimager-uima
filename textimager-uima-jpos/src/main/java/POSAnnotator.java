/**
 *
 * Copyright (c) 2017, JULIE Lab.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD-2-Clause License
 *
 * Author:
 *
 * Description:
 **/

/**
 * POSAnnotator.java
 *
 * Copyright (c) 2015, JULIE Lab.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD-2-Clause License
 *
 * Author: hellrich
 *
 * Current version: 0.0.1
 *
 * Creation date: Sep 11, 2014
 *
 * This is an UIMA wrapper for the JULIE POSTagger.
 * Based on Katrin Tomanek's JNET
 **/

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;



public abstract class POSAnnotator extends JCasAnnotator_ImplBase {

    @Override
    public void initialize(final UimaContext aContext) throws ResourceInitializationException {

        // invoke default initialization
        super.initialize(aContext);

    }


    @Override
    public void process(final JCas aJCas) throws AnalysisEngineProcessException {


        }


}
