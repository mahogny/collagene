package seq;

/**
 * One annotation (feature) of a sequence
 * 
 * @author Johan Henriksson
 *
 */
public class SeqAnnotation
	{
	public String name;
	public String note;
	
	public int from, to; //starting from 0, in format [from,to)
	
	public Orientation orientation=Orientation.FORWARD;
	
	public SeqColor col=new SeqColor(1,0,0);

	
	
	public SeqAnnotation()
		{
		}
	
	public SeqAnnotation(SeqAnnotation annot)
		{
		name=annot.name;
		note=annot.note;
		from=annot.from;
		to=annot.to;
		orientation=annot.orientation;
		col=new SeqColor(annot.col);
		}



	public boolean isOverlapping(SeqAnnotation o)
		{
		return from<=o.to && to>=o.from;  //TODO likely a >= here
		}




	@Override
	public String toString()
		{
		return "("+from+","+to+")";
		}

	public int length(AnnotatedSequence seq)
		{
		SequenceRange r=new SequenceRange();
		r.from=from;
		r.to=to;
		return r.getSize(seq);
		}
	
	public int getFrom()
		{
		return from;
		}
	
	public int getTo()
		{
		return to;
		}
	}
