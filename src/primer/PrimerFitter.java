package primer;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import seq.AnnotatedSequence;
import seq.Orientation;
import sequtil.NucleotideUtil;

/**
 * Automatically fit a primer to the sequence
 * 
 * @author Johan Henriksson
 *
 */
public class PrimerFitter
	{
	private String pseq;
	LinkedList<FittedPrimer> candidates=new LinkedList<PrimerFitter.FittedPrimer>();
	private String pname;

	
	public static class FittedPrimer
		{
		Primer p;
		double score;
		}
	
	
	public int scorePerfectLength=1;
	
	/**
	 * Anneal, in one orientation
	 */
	private void anneal(String upper, String lower, Orientation orientation, boolean isCircular)
		{
		for(int dx=-lower.length();dx<upper.length()-lower.length();dx++)
			{
			String subUpper;
			String subLower;
			if(isCircular)
				{
				String left=upper.substring(Math.min(upper.length(),upper.length()+dx));
				String right=upper.substring(Math.max(dx,0), dx+lower.length());
				subUpper=
						left+
						right;
				subLower=lower;
				}
			else
				{
				//Need to cut sequences properly
				int ll=lower.length();
				int lu=upper.length();
				int minL1=Math.min(lu,ll);
				int minL2=lu-dx;
				int minL3=Math.min(ll+dx,lu);
				int commonLength=Math.min(Math.min(minL1,minL2),minL3);
				
				int fromUpper=Math.max(0, dx);
				int fromLower=Math.max(-dx, 0);
				
				int toUpper  =fromUpper+commonLength;
				int toLower  =fromLower+commonLength;
				
				subUpper=upper.substring(fromUpper, toUpper);
				subLower=lower.substring(fromLower, toLower);
				System.out.println("???????????");
				}
			
			
			//Compute score of alignment
			int t=0;
			int lastnonmatch=-1;
			for(int i=0;i<subUpper.length();i++)
				{
				if(subUpper.charAt(i)==subLower.charAt(i))
					t++;
				else
					lastnonmatch=i;
				}
			int perfectmatchlength=subLower.length()-lastnonmatch;
			
			double temp=t + perfectmatchlength*scorePerfectLength;
			
			//Figure out position
			int targetpos=dx+lower.length();
			if(orientation==Orientation.REVERSE)
				targetpos=upper.length()-targetpos;
			
			//Pick best orientation
			Primer p=new Primer();
			p.name=pname;
			p.sequence=pseq;
			p.orientation=orientation;
			p.targetPosition=targetpos;
			
			
			FittedPrimer f=new FittedPrimer();
			f.p=p;
			f.score=temp;
			candidates.add(f);
			}


		
		}

	
	public void run(AnnotatedSequence seq, String pname, String pseq)
		{
		candidates.clear();
		this.pseq=pseq;
		this.pname=pname;
		String s=seq.getSequence();

		System.out.println(s);
		
		//At every position, check the overlap and see how well it fits. Double score+ for fits at the end of the sequence
		anneal(s, pseq, Orientation.FORWARD, seq.isCircular);
		anneal(NucleotideUtil.revcomplement(s), pseq, Orientation.REVERSE, seq.isCircular);
		
		//Sort by score
		Collections.sort(candidates, new Comparator<FittedPrimer>()
			{
			public int compare(FittedPrimer o1, FittedPrimer o2)
				{
				return -Double.compare(o1.score, o2.score);
				}
			});
		}

	
	/**
	 * Get the best primer
	 */
	public Primer getBestPrimer()
		{
		if(candidates.isEmpty())
			return null;
		else
			return candidates.get(0).p;
		}
	}
