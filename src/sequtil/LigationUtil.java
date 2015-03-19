package sequtil;

import java.util.LinkedList;

import seq.AnnotatedSequence;
import seq.SeqAnnotation;
import seq.SequenceRange;

/**
 * 
 * From a set of sequences, figure out how they can be ligated
 * 
 * @author Johan Henriksson
 *
 */
public class LigationUtil
	{

	/**
	 * Check if A can be ligated to B
	 */
	public static boolean canLigateAtoB(AnnotatedSequence seqA, AnnotatedSequence seqB)
		{
		if(isBluntEnd(seqA))
			return isBluntBegin(seqB);
		else
			{
			//Check if forward is fine
			AnnotatedSequence stickyEndA=extractStickyEnd(seqA);
			AnnotatedSequence stickyBeginB=extractStickyBegin(seqB);
			return isComplementary(stickyEndA, stickyBeginB);
			}
		}
	
	/**
	 * Check if A can be ligated to rotated B
	 */
	public static boolean canLigateAtoRotB(AnnotatedSequence seqA, AnnotatedSequence seqB)
		{
		if(isBluntEnd(seqA))
			return isBluntEnd(seqB);
		else
			{
			AnnotatedSequence stickyEndA=extractStickyEnd(seqA);
			AnnotatedSequence stickyBeginRotB=extractStickyEnd(seqB);
			stickyBeginRotB.reverseSequence();
			return isComplementary(stickyEndA, stickyBeginRotB);
			}
		}
	
	/**
	 * Helper: check if two sticky ends are complementary
	 */
	private static boolean isComplementary(AnnotatedSequence stickyA, AnnotatedSequence stickyB)
		{
		if(stickyA.getLength()!=stickyB.getLength())
			return false;
		else if(stickyA.getLength()==0)
			return true;
		else
			{
			//Figure out which strand to compare
			String seqA, seqB;
			seqA=stickyA.getSequence();
			if(seqA.charAt(0)!=' ')
				seqB=NucleotideUtil.complement(stickyB.getSequenceLower());
			else
				{
				seqA=stickyA.getSequenceLower();
				seqB=NucleotideUtil.complement(stickyB.getSequence());
				}
			
			//Compare all characters
			for(int i=0;i<seqA.length();i++)
				if(seqA.charAt(i)!=seqB.charAt(i))
					return false;
			return true;
			}
		}
	
	
	/**
	 * Extract the sticky beginning part of a sequence
	 */
	private static AnnotatedSequence extractStickyBegin(AnnotatedSequence seq)
		{
		AnnotatedSequence sticky=new AnnotatedSequence();
		String upr=seq.getSequence();
		String lwr=seq.getSequenceLower();
		int i=0;
		for(;i<seq.getLength();i++)
			if(upr.charAt(i)!=' ' && lwr.charAt(i)!=' ')
				break;
		System.out.println(i);
		SequenceRange r=new SequenceRange(0,i);
		sticky.setSequence(seq.getSequence(r), seq.getSequenceLower(r));
		return sticky;
		}
	
	/**
	 * Extract the sticky end part of a sequence
	 */
	private static AnnotatedSequence extractStickyEnd(AnnotatedSequence seq)
		{
		AnnotatedSequence sticky=new AnnotatedSequence();
		String upr=seq.getSequence();
		String lowr=seq.getSequenceLower();
		int i=seq.getLength()-1;
		for(;i>=0;i--)
			if(upr.charAt(i)!=' ' && lowr.charAt(i)!=' ')
				{
				i++;
				break;
				}
		SequenceRange r=new SequenceRange(i,0);
		sticky.setSequence(seq.getSequence(r), seq.getSequenceLower(r));
		return sticky;
		}
	
	/**
	 * Check if a sequence is blunt-ended, at end
	 */
	private static boolean isBluntEnd(AnnotatedSequence seq)
		{
		int length=seq.getLength();
		SequenceRange r=new SequenceRange(length-1, 0);
		return !(seq.getSequence(r).equals(" ") || seq.getSequenceLower(r).equals(" "));
		}
	
	
	/**
	 * Check if a sequence is blunt-ended, at beginning
	 */
	private static boolean isBluntBegin(AnnotatedSequence seq)
		{
		SequenceRange r=new SequenceRange(0,1);
		return !(seq.getSequence(r).equals(" ") || seq.getSequenceLower(r).equals(" "));
		}

	

	/**
	 * Self-ligate plasmid to make it circular (not checking if this is valid)
	 */
	public static void selfCircularize(AnnotatedSequence seq)
		{
		//Fill in the first gap. This way, no annotation need be moved
		String upr=seq.getSequence();
		String lwr=seq.getSequenceLower();
		int len=upr.length();
		if(upr.charAt(0)==' ')
			{
			//Lower overhang
			int i=0;
			for(;;i++)
				if(upr.charAt(i)!=' ')
					break;
			upr=upr.substring(i);
			lwr=lwr.substring(0, len-i);
			}
		else
			{
			//Upper overhang
			int i=0;
			for(;;i++)
				if(lwr.charAt(i)!=' ')
					break;

			upr=upr.substring(0, len-i);
			lwr=lwr.substring(i);
			}
		seq.setSequence(upr,lwr);
		seq.isCircular=true;
		
		//Normalize position of annotation
		for(SeqAnnotation a:seq.annotations)
			a.range=a.range.toNormalizedRange(seq);
		}


	/**
	 * Ligate sequence A with B
	 */
	public static AnnotatedSequence ligate(AnnotatedSequence seqA, AnnotatedSequence seqB)
		{
		//Remove last " " in first sequence
		String upr=removeTrailingSpace(seqA.getSequence());
		String lwr=removeTrailingSpace(seqA.getSequenceLower());
		
		//Count how much to remove from the beginning in the second
		int cnt=0;
		while(seqB.getSequence().charAt(cnt)==' ' || seqB.getSequenceLower().charAt(cnt)==' ')
			cnt++;
		
		//Attach second sequence
		upr+=removeStartingSpace(seqB.getSequence());
		lwr+=removeStartingSpace(seqB.getSequenceLower());

		//Assemble
		AnnotatedSequence newseq=new AnnotatedSequence();
		newseq.name=seqA.name + "+" + seqB.name;
		newseq.setSequence(upr,lwr);
		seqA.copyFeaturesTo(newseq, 0);
		seqB.copyFeaturesTo(newseq, seqA.getLength()-cnt);
		newseq.normalizeFeaturePos();
		
		System.out.println("fa "+seqA.annotations);
		System.out.println("fb "+seqB.annotations);
		System.out.println("features "+newseq.annotations);

		return newseq;
		}
	

	/**
	 * Get all the ways in which these two sequences can be ligated
	 */
	public static LinkedList<LigationCandidate> getLigationCombinations(AnnotatedSequence seqA, AnnotatedSequence seqB)
		{
		LinkedList<LigationCandidate> list=new LinkedList<LigationCandidate>();
		AnnotatedSequence rotA=copySequenceOnly(seqA);
		AnnotatedSequence rotB=copySequenceOnly(seqB);
		rotA.reverseSequence();
		rotB.reverseSequence();
		
		LigationCandidate cand1=null, cand2=null;
				
		if(canLigateAtoB(seqA, seqB))
			{
			LigationCandidate cand=new LigationCandidate(seqA, seqB);
			cand.rotateA=false;
			cand.rotateB=false;
			cand1=cand;
			list.add(cand);
			}
		if(canLigateAtoB(seqA, rotB))
			{
			LigationCandidate cand=new LigationCandidate(seqA, seqB);
			cand.rotateA=false;
			cand.rotateB=true;
			cand2=cand;
			list.add(cand);
			}
		if(cand2!=null && canLigateAtoB(rotA, seqB))
			{
			LigationCandidate cand=new LigationCandidate(seqA, seqB);
			cand.rotateA=true;
			cand.rotateB=false;
			list.add(cand);
			}
		if(cand1==null && canLigateAtoB(rotA, rotB))
			{
			LigationCandidate cand=new LigationCandidate(seqA, seqB);
			cand.rotateA=true;
			cand.rotateB=true;
			list.add(cand);
			}
		return list;
		}
	
	
	/**
	 * Copy sequence only, no annotation
	 */
	private static AnnotatedSequence copySequenceOnly(AnnotatedSequence seq)
		{
		AnnotatedSequence newseq=new AnnotatedSequence();
		newseq.setSequence(seq.getSequence(), seq.getSequenceLower());
		return newseq;
		}

	/**
	 * "abc  " => "abc"
	 */
	private static String removeTrailingSpace(String s)
		{
		int i=s.length()-1;
		for(;s.charAt(i)==' ';i--);
		return s.substring(0,i+1);
		}

	/**
	 * "  abc" => "abc"
	 */
	private static String removeStartingSpace(String s)
		{
		int i=0;
		for(;s.charAt(i)==' ';i++);
		return s.substring(i);
		}

	
	/**
	 * Check if a sequence can self-ligate
	 */
	public static boolean canCircularize(AnnotatedSequence seq)
		{
		return canLigateAtoB(seq, seq);
		}
	
	
	}
