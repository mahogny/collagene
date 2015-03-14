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
	
	public SequenceRange range=new SequenceRange();
//	public int from, to; //starting from 0, in format [from,to)
	
	public Orientation orientation=Orientation.FORWARD;
	
	public SeqColor color=new SeqColor(1,0,0);
	public String desc;

	public SeqAnnotation()
		{
		}
	
	public SeqAnnotation(SeqAnnotation annot)
		{
		name=annot.name;
		note=annot.note;
		range=new SequenceRange(annot.range);
		orientation=annot.orientation;
		color=new SeqColor(annot.color);
		}



	public boolean isOverlapping(SeqAnnotation o)
		{
		//TODO better to delegate down. but what about wrapping ones?
		return range.from<=o.range.to && range.to>=o.range.from;  //TODO likely a >= here
		}




	@Override
	public String toString()
		{
		return "("+getFrom()+","+getTo()+")";
		}

	public int length(AnnotatedSequence seq)
		{
		return range.getSize(seq);
		}
	
	public int getFrom()
		{
		return range.from;
		}
	
	public int getTo()
		{
		return range.to;
		}

	public void setRange(SequenceRange r)
		{
		range=r;
		}

	public void setRange(int from, int to)
		{
		range=new SequenceRange(from,to);
		}
	}
