package collagene.io;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import collagene.seq.AnnotatedSequence;

/**
 * 
 * Exporter of sequences
 * 
 * @author Johan Henriksson
 *
 */
public interface SequenceExporter
	{
	public boolean isType(File f);
	public void save(OutputStream os, List<AnnotatedSequence> list) throws IOException;
	}
