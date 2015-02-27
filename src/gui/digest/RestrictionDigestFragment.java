package gui.digest;

import seq.AnnotatedSequence;
import seq.SequenceRange;

/**
 * One fragment
 */
public class RestrictionDigestFragment
	{
	public AnnotatedSequence origseq;
	public SequenceRange upper=new SequenceRange();

	public int getUpperLength()
		{
		if(upper.from>upper.to)
			return origseq.getLength()-upper.to+upper.from;
		else
			return upper.to-upper.from;
		}

	public AnnotatedSequence getFragmentSequence()
		{
		return origseq;
		// TODO Auto-generated method stub
		}
	}