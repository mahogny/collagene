package collagene.gui.sequenceWindow;

import collagene.seq.AnnotatedSequence;
import collagene.seq.SeqAnnotation;

public class EventSelectedAnnotation extends CollageneEvent
	{
	public SeqAnnotation annot;

	public EventSelectedAnnotation(AnnotatedSequence seq, SeqAnnotation annot)
		{
		super(seq);
		this.annot = annot;
		}
	
	
	}
