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
	
	
	public AnnotatedSequence dopcr(AnnotatedSequence seq)
		{
		Primer fwd=getFwd(),rev=getRev();

		AnnotatedSequence seqnew=new AnnotatedSequence();
		seqnew.isCircular=false;			
		
		//Cut out
		String s=fwd.sequence + seq.getSubsequence(new SequenceRange(fwd.targetPosition,rev.targetPosition)) + rev.sequence;
		seqnew.setSequence(s);
		
		//Transfer features
		int shift=fwd.targetPosition;
		for(SeqAnnotation a:seq.annotations)
			{
			SeqAnnotation newa=new SeqAnnotation(a);
			newa.from-=shift;
			newa.to-=shift;
			if(newa.from>=0 && newa.to<seqnew.getLength())
				seqnew.addAnnotation(newa);
			}
		
		return seqnew;
		}

	}
