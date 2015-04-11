package collagene.gui.sequenceWindow;

import collagene.seq.AnnotatedSequence;
import collagene.seq.SequenceRange;

public class EventSelectedRegion extends CollageneEvent
	{
	public SequenceRange range;
	
	public EventSelectedRegion(AnnotatedSequence seq, SequenceRange range)
		{
		super(seq);
		if(range!=null)
			this.range = new SequenceRange(range);
		}
	}
