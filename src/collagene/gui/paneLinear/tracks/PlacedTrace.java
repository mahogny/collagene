package collagene.gui.paneLinear.tracks;

import collagene.io.trace.SequenceTrace;

/**
 * 
 * 
 * @author Johan Henriksson
 *
 */
public class PlacedTrace
	{
	public SequenceTrace trace;

	int from=0;

	public int getFrom()
		{
		return from;
		}

	public int getTo()
		{
		return from+trace.getNumBases();
		}
	
	
	}
