package collagene.primer;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import collagene.seq.AnnotatedSequence;
import collagene.seq.Orientation;
import collagene.seq.SeqColor;
import collagene.seq.SequenceRange;
import collagene.sequtil.NucleotideUtil;

/**
 * 
 * A primer attached to a sequence
 * 
 * @author Johan Henriksson
 *
 */
public class Primer
	{
	public String name="";
	public String sequence="";
	public Orientation orientation=Orientation.FORWARD;
	public int targetPosition;
	public SeqColor color;  //TODO. keep color?

	public Primer()
		{
		}
	public Primer(Primer p)
		{
		name=p.name;
		sequence=p.sequence;
		orientation=p.orientation;
		targetPosition=p.targetPosition;
		color=p.color; //copy?
		}
	
	public Integer getProductLength(AnnotatedSequence seq, Primer other)
		{
		if(other.orientation==orientation)
			return null;
		else
			{
			//Canonicalize orientation
			Primer fwd,rev;
			if(orientation==Orientation.FORWARD)
				{
				fwd=this;
				rev=other;
				}
			else
				{
				fwd=other;
				rev=this;
				}
			
			if(fwd.targetPosition<rev.targetPosition)
				{
				return rev.targetPosition-fwd.targetPosition
						+fwd.length()+rev.length();
				}
			else
				return seq.getLength()-fwd.targetPosition+fwd.length() + rev.targetPosition + rev.length();
			}
		
		}

	
	public int length()
		{
		return sequence.length();
		}
	
	
	public LinkedList<PrimerPairInfo> getPairInfo(AnnotatedSequence seq)
		{
		LinkedList<PrimerPairInfo> list=new LinkedList<PrimerPairInfo>();
		
		for(Primer other:seq.primers)
			{
			//could maybe sort here?
			Integer bp=getProductLength(seq, other);
			if(bp!=null)
				{
				PrimerPairInfo i=new PrimerPairInfo();
				i.primerA=this;
				i.primerB=other;
				i.productsize=bp;
				list.add(i);
				}
			}
		Collections.sort(list, new Comparator<PrimerPairInfo>()
			{
			public int compare(PrimerPairInfo o1, PrimerPairInfo o2)
				{
				return Integer.compare(o1.productsize, o2.productsize);
				}
			});
		return list;
		}

	
	/**
	 * Get the range the primer covers
	 */
	public SequenceRange getRange()
		{
		if(orientation==Orientation.FORWARD)
			return new SequenceRange(targetPosition-sequence.length(),targetPosition);
		else
			return new SequenceRange(targetPosition,targetPosition+sequence.length());
		}
	
	/**
	 * Move the 0-position
	 */
	public void setNew0(int pos, AnnotatedSequence seq)
		{
		targetPosition=seq.normalizePos(targetPosition-pos);
		}
	
	public void shift(int shift)
		{
		targetPosition+=shift;
		}
	public int getLength()
		{
		return sequence.length();
		}

	
	
	/**
	 * Get the sequence this primer overlaps, reoriented to fit primer
	 */
	public String getSequenceOnSeqence(AnnotatedSequence seq)
		{
		SequenceRange r2=getRange();
		if(orientation==Orientation.FORWARD)
			return seq.getSequence(r2);
		else if(orientation==Orientation.REVERSE)
			return NucleotideUtil.reverse(seq.getSequenceLower(r2));
		else
			throw new RuntimeException("no orientation");
		}

	
	
	/**
	 * Get the first matching part of the sequence vs what it overlaps. Used to compute the real Tm
	 */
	public String getFirstMatchingSequencePart(AnnotatedSequence seq)
		{
		String getseq=getSequenceOnSeqence(seq);
		int i=sequence.length();
		while(i>0 && getseq.charAt(i-1)==sequence.charAt(i-1))
			i--;
		return getseq.substring(i);
		}
	
	
	}
