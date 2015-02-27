package io;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import seq.AnnotatedSequence;

/**
 * 
 * Importer of sequences
 * 
 * @author Johan Henriksson
 *
 */
public interface SequenceImporter
	{
	public boolean isType(InputStream is) throws IOException;
	public List<AnnotatedSequence> load(InputStream is) throws IOException;
	}
