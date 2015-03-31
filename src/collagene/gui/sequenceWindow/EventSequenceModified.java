package collagene.gui.sequenceWindow;

import collagene.seq.AnnotatedSequence;

/**
 * 
 * 
 * @author Johan Henriksson
 *
 */
public class EventSequenceModified extends CollageneEvent
	{
	public EventSequenceModified(AnnotatedSequence seq)
		{
		super(seq);
		}

	}
