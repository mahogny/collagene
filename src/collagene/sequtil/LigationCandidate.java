package collagene.sequtil;

import collagene.seq.AnnotatedSequence;

/**
 * 
 * A possible way of ligating two sequences
 * 
 * @author Johan Henriksson
 *
 */
public class LigationCandidate
	{
	public AnnotatedSequence seqA, seqB;
	public boolean rotateA, rotateB;
	
	public LigationCandidate(AnnotatedSequence seqA, AnnotatedSequence seqB)
		{
		this.seqA = seqA;
		this.seqB = seqB;
		}


	/**
	 * Get product of ligation
	 */
	public AnnotatedSequence getProduct()
		{
		AnnotatedSequence seqA=new AnnotatedSequence(this.seqA);
		AnnotatedSequence seqB=new AnnotatedSequence(this.seqB);

		String nA=seqA.name;
		String nB=seqB.name;
		if(rotateA)
			{
			seqA.reverseSequence();
			nA+="/rot";
			}
		if(rotateB)
			{
			seqB.reverseSequence();
			nB+="/rot";
			}
		
		AnnotatedSequence seq=LigationUtil.ligate(seqA, seqB);
		seq.name=nA+"+"+nB;
		return seq;
		}
	}
