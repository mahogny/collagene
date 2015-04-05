package collagene.gui;

import java.util.HashMap;

import collagene.alignment.PairwiseAlignment;
import collagene.seq.AnnotatedSequence;
import collagene.seq.Orientation;
import collagene.seq.SeqAnnotation;
import collagene.sequtil.NucleotideUtil;

/**
 * 
 * Automatically fit a feature onto a reference sequence
 * 
 * @author Johan Henriksson
 *
 */
public class Autofit
	{
	
	public SeqAnnotation autofit(AnnotatedSequence reference, AnnotatedSequence seqtofit)
		{
		//If circular then repeat the sequence for the search
		//Actually, only need to add as much as the length of the second sequence. saves half the time almost
		String seqA=reference.getSequence();
		if(reference.isCircular)
			seqA=seqA+seqA;
		
		//Try all variations of the sequence (or? maybe only 2?)
		PairwiseAlignment al1=getal(reference);
		PairwiseAlignment al2=getal(reference);
		PairwiseAlignment al3=getal(reference);
		PairwiseAlignment al4=getal(reference);
		al1.align(seqA, seqtofit.getSequence());
		al2.align(seqA, NucleotideUtil.reverse(seqtofit.getSequence()));
		al3.align(seqA, NucleotideUtil.complement(seqtofit.getSequence()));
		al4.align(seqA, NucleotideUtil.revcomplement(seqtofit.getSequence()));
		
		HashMap<Integer, PairwiseAlignment> scores=new HashMap<Integer, PairwiseAlignment>();
		scores.put(1,al1);
		scores.put(2,al2);
		scores.put(3,al3);
		scores.put(4,al4);

		int maxlength=-1;
		int maxind=-1;
		for(int ind:scores.keySet())
			if(scores.get(ind).getMatchLength()>maxlength)
				{
				maxlength=scores.get(ind).getMatchLength();
				maxind=ind; 
				}
		
		SeqAnnotation ann=new SeqAnnotation();
		PairwiseAlignment bestal=scores.get(maxind);
		ann.range.from=bestal.getStartOfA();
		ann.range.to=bestal.getEndOfA();
		if(maxind==1 || maxind==3) //??
			ann.orientation=Orientation.FORWARD;
		else
			ann.orientation=Orientation.REVERSE;
		
		
		return ann;
		}

	private PairwiseAlignment getal(AnnotatedSequence reference)
		{
		PairwiseAlignment al=new PairwiseAlignment();
		al.isLocalAlignment=true; //Later: try to fit all of B? or?
		return al;
		}
	
	}
