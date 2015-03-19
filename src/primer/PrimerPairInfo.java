package primer;

import seq.AnnotatedSequence;
import seq.Orientation;
import seq.SeqAnnotation;
import seq.SequenceRange;

/**
 * 
 * Information about a pair of two primers
 * 
 * @author Johan Henriksson
 *
 */
public class PrimerPairInfo
	{
	public Primer primerA, primerB;
	public int productsize;
	
	public Primer getFwd()
		{
		if(primerA.orientation==Orientation.FORWARD)
			return primerA;
		else
			return primerB;
		}
	
	public Primer getRev()
		{
		if(primerA.orientation==Orientation.FORWARD)
			return primerB;
		else
			return primerA;
		}
	
	
	/**
	 * Perform a PCR and get the product
	 */
	public AnnotatedSequence dopcr(AnnotatedSequence seq)
		{
		Primer fwd=getFwd(),rev=getRev();

		AnnotatedSequence seqnew=new AnnotatedSequence();
		seqnew.isCircular=false;			
		
		//Cut out
		String s=fwd.sequence + seq.getSequence(new SequenceRange(fwd.targetPosition,rev.targetPosition)) + rev.sequence;
		seqnew.setSequence(s);
		
		//Transfer features
		int shift=-fwd.targetPosition+fwd.sequence.length();
		for(SeqAnnotation a:seq.annotations)
			{
			SeqAnnotation newa=new SeqAnnotation(a);                       //TODO PCR over boundaries. how does that affect this?
			System.out.println("was range "+newa.range);
			newa.range=newa.range.toUnwrappedRange(seq);
			newa.range.shift(shift);
			System.out.println("here is a range "+newa.range);
			if(newa.getFrom()>=0 && newa.getTo()<seqnew.getLength())
				{
				newa.range=newa.range.toNormalizedRange(seqnew);
				seqnew.addAnnotation(newa);
				}
			}
		
		//TODO PCR primers etc. better to set new 0 (let it move annotation). cut out everything beyond last primer. then set new sequence
		
		return seqnew;
		}

	}
