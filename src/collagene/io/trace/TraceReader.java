package collagene.io.trace;

import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * Reader for sequence traces
 * 
 * @author Johan Henriksson
 *
 */
public interface TraceReader
	{
	public SequenceTrace readFile(InputStream is) throws IOException;
	}
