package collagene.gui.sequenceWindow;

import collagene.seq.AnnotatedSequence;

/**
 * 
 * One event for the Collagene GUI
 * 
 * @author Johan Henriksson
 *
 */
public class CollageneEvent
	{
	public AnnotatedSequence seq;

	public CollageneEvent(AnnotatedSequence seq)
		{
		this.seq = seq;
		}
	
	}
