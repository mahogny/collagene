package collagene.io.trace;

import java.io.File;

/**
 * 
 * Manager for sequence trace I/O
 * 
 * @author Johan Henriksson
 *
 */
public class TraceIO
	{

	public static TraceReader getReader(File f)
		{
		if(f.getName().endsWith(".scf"))
			return new TraceReaderSCF();
		else
			return null;
		}
	
	
	
	}
