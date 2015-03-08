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
	
	/**
	 * Returns a canonical form in which 0<=to<sequence length, and from is adjusted accordingly, within the same range
	 */
	public SequenceRange toNormalizedRange(AnnotatedSequence seq)
		{
		//Normalize range
		int from=this.from;
		int to=this.to;
		while(from<0)
			{
			from+=seq.getLength();
			to+=seq.getLength();
			}
		while(from>=seq.getLength())
			{
			from-=seq.getLength();
			to-=seq.getLength();
			}
		if(to>seq.getLength())
			to-=seq.getLength();
		return new SequenceRange(from,to);
		}
	
	
	/**
	 * Returns a canonical form in which 0<=to<sequence length, and from is adjusted accordingly, but always > from
	 */
	public SequenceRange toUnwrappedRange(AnnotatedSequence seq)
		{
		//Normalize range
		int from=this.from;
		int to=this.to;
		while(from<0)
			{
			from+=seq.getLength();
			to+=seq.getLength();
			}
		while(from>=seq.getLength())
			{
			from-=seq.getLength();
			to-=seq.getLength();
			}
		while(to>seq.getLength())
			to-=seq.getLength();
		while(to<from)
			to+=seq.getLength();
		return new SequenceRange(from,to);

		}
	/*
	public int getLower()
		{
		return Math.min(from, to);
		}
	public int getUpper()
		{
		return Math.max(from,to);
		}*/
	
	public int getSize(AnnotatedSequence seq)
		{
		SequenceRange n=toNormalizedRange(seq);
		
		if(/*seq.isCircular && */n.to<n.from)
			return seq.getLength()-n.from+n.to;
		else
			return n.to-n.from;
		}

	}
