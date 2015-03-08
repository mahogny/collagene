package seq;

/**
 * A selection range over a sequence
 * 
 * TODO. range should always proceed forwards. get rid of upper and lower!
 * 
 * @author Johan Henriksson
 *
 */
public class SequenceRange
	{
	public int from, to;

	public SequenceRange()
		{
		}
	public SequenceRange(int from, int to)
		{
		this.from=from;
		this.to=to;
		}
	
	
	public int getLower()
		{
		return Math.min(from, to);
		}
	public int getUpper()
		{
		return Math.max(from,to);
		}
	public int getSize(AnnotatedSequence seq)
		{
		if(seq.isCircular && to<from)
			return seq.getLength()-from+to;
		else
			return to-from;
		}

	}
