package seq;

import java.util.LinkedList;

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
	public boolean isNoRange=false;
	
	public int from, to;

	public SequenceRange()
		{
		}
	public SequenceRange(int from, int to)
		{
		this.from=from;
		this.to=to;
		}
	
	public SequenceRange(SequenceRange r)
		{
		this.from=r.from;
		this.to=r.to;
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
			//if(to<0)
			//	to=seq.getLength()-to;
			}
		if(to>seq.getLength())
			to-=seq.getLength();
		else if(to<0)
			to+=seq.getLength();
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
	
	public int getSize(AnnotatedSequence seq)
		{
		SequenceRange n=toNormalizedRange(seq);
		
		if(/*seq.isCircular && */n.to<n.from)
			return seq.getLength()-n.from+n.to;
		else
			return n.to-n.from;
		}

	@Override
	public String toString()
		{
		return "("+from+","+to+")";
		}
	
	
	/**
	 * Segment range into blocks over lines
	 */
	public LinkedList<SequenceRange> segmentRanges(AnnotatedSequence seq, int charsPerLine)
		{
		LinkedList<SequenceRange> segments=new LinkedList<SequenceRange>();
		SequenceRange r=toNormalizedRange(seq);
		if(r.from>r.to)
			{
			//"from" to end of sequence
			for(int i=r.from/charsPerLine;i<=seq.getLength()/charsPerLine;i++)
				{
				segments.add(
				new SequenceRange(
						Math.max(i*charsPerLine, r.from),
						Math.min((i+1)*charsPerLine, seq.getLength())));
				}
			//0 to "to"
			for(int i=0;i<=r.to/charsPerLine;i++)
				{
				segments.add(
					new SequenceRange(
							Math.max(i*charsPerLine, r.from),
							Math.min((i+1)*charsPerLine, r.to)));
				}
			}
		else
			{
			int ilow=r.from/charsPerLine;
			int ihigh=r.to/charsPerLine;
			for(int i=ilow;i<=ihigh;i++)
				{
				segments.add(
				new SequenceRange(
						Math.max(i*charsPerLine, r.from),
						Math.min((i+1)*charsPerLine, r.to)));
				}
			}
		return segments;
		}
	
	public static SequenceRange getNoRange()
		{
		SequenceRange r=new SequenceRange();
		r.isNoRange=true;
		return r;
		}
	
	
	public void shift(int featureshift)
		{
		from-=featureshift;
		to-=featureshift;
		}
	}
