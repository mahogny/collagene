package gui.sequenceWindow;

import java.util.TreeMap;

import seq.AnnotatedSequence;
import seq.Orientation;
import seq.Primer;
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
	private TreeMap<Double, Primer> candidates=new TreeMap<Double, Primer>();
	private String pname;

	/**
	 * Anneal, in one orientation
	 */
	private void anneal(String upper, String lower, Orientation orientation)
		{
		double temp[]=new double[upper.length()];

		for(int dx=-lower.length();dx<upper.length()-lower.length();dx++)
			{
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
			
			String subUpper=upper.substring(fromUpper, toUpper);
			String subLower=lower.substring(fromLower, toLower);
			
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
			//System.out.println(perfectmatchlength);
			
			temp[dx+lower.length()]=t + perfectmatchlength;///(double)Math.max(5, commonLength);
			}


		int imax=0;
		double prevmax=temp[0];
		for(int i=0;i<temp.length;i++)
			{
			double d=temp[i];
			if(d>prevmax)
				{
				imax=i;
				prevmax=d;
				}
			}

		//Build up the alignment
		StringBuilder sb1=new StringBuilder();
		for(int i=0;i<Math.max(0,-(imax-lower.length()));i++)
			sb1.append(" ");
		sb1.append(upper);
		for(int i=0;i<Math.max(0,imax-lower.length());i++)
			sb1.append(" ");
		
		StringBuilder sb2=new StringBuilder();
		for(int i=0;i<Math.max(0,imax-lower.length());i++)
			sb2.append(" ");
		sb2.append(lower);
		for(int i=0;i<Math.max(0,-(imax-lower.length()));i++)
			sb2.append(" ");

		System.out.println(sb1.toString());
		System.out.println(sb2.toString());
		
		//this.imax=imax-lower.length();
//		this.score=prevmax;
		
		/*
		AnnotatedSequence seq=new AnnotatedSequence();
		seq.setSequence(sb1.toString(), sb2.toString());
		return seq;*/
		
		//Pick best orientation
		Primer p=new Primer();
		p.name=pname;
		p.sequence=pseq;
		
		p.orientation=orientation;
		if(orientation==Orientation.FORWARD)
			p.targetPosition=imax;//+pseq.length();
		else
			p.targetPosition=imax;
		
		candidates.put(prevmax, p);
		}

	
	public PrimerFitter(AnnotatedSequence seq, String pname, String pseq)
		{
		this.pseq=pseq;
		this.pname=pname;
		String s=seq.getSequence();
		
		//Handle circularity
		if(seq.isCircular)
			s=s+s; //Can reduce length here
		
		//At every position, check the overlap and see how well it fits. Double score+ for fits at the end of the sequence
		anneal(s, pseq, Orientation.FORWARD);

		anneal(NucleotideUtil.revcomplement(s), pseq, Orientation.REVERSE);
		}
	
	public Primer getBestPrimer()
		{
		if(candidates.isEmpty())
			return null;
		else
			return candidates.get(candidates.lastKey());
		}

/*	
	public static void main(String[] args)
		{
		
		//TODO handle circularity, if there is!
		
		String s="GCAGAGATCCAGTTTGGTTAGTACCGGGCCCTACGCGTTACTCGAGCCAAGGTCGGGCAGGAAGAGGGCCTATTTCCCATGATTCCTTCATATTTGCATATACGATACAAGGCTGTTA";
		
		String tofit="TTATTTGTTAACAAGGTCGGGCAGGAAGAGGG";
		
		PrimerFitter f=new PrimerFitter();
		f.anneal(s, tofit, Orientation.FORWARD);

		PrimerFitter r=new PrimerFitter();
		r.anneal(NucleotideUtil.revcomplement(s), tofit, Orientation.REVERSE);
		
		//At every position, check the overlap and see how well it fits. Double score+ for fits at the end of the sequence
		Primer p=new Primer();
		p.name="foo";
		p.sequence=tofit;
		
		if(f.score>r.score)
			{
			//Forward
			p.orientation=Orientation.FORWARD;
			p.targetPosition=f.imax+tofit.length();
			}
		else
			{
			//Reverse
			p.orientation=Orientation.REVERSE;
			p.targetPosition=f.imax;
			}
		
		
		}
	*/
	}
