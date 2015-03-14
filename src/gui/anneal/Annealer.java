package gui.anneal;

import melting.CalcTm;
import melting.CalcTmSanta98;
import seq.AnnotatedSequence;
import sequtil.NucleotideUtil;


/**
 * 
 * Anneal two oligos together
 * 
 * @author Johan Henriksson
 *
 */
public class Annealer
	{
	public CalcTm calctm=new CalcTmSanta98();
	
	public AnnotatedSequence anneal(String upper, String lower)
		{
		upper=upper.replace(" ", "");
		lower=lower.replace(" ", "");
		
		lower=NucleotideUtil.reverse(lower);
		String lowerComp=NucleotideUtil.complement(lower);  ///hm


		double temp[]=new double[upper.length()+lower.length()];

		for(int dx=-lower.length();dx<upper.length();dx++)
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
			String subLower=lowerComp.substring(fromLower, toLower);
			
			int t=0;
			for(int i=0;i<subUpper.length();i++)
				if(subUpper.charAt(i)==subLower.charAt(i))
					t++;
			temp[dx+lower.length()]=t/(double)Math.max(5, commonLength);
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
		
		AnnotatedSequence seq=new AnnotatedSequence();
		seq.setSequence(sb1.toString(), sb2.toString());
		return seq;
		}
	
	
	public static void main(String[] args)
		{
		Annealer a=new Annealer();
/*		a.anneal("CACCGAGAGACAGCTTGTACGCCGGT",
				     "TAAAACCGGCGTACAAGCTGTCTCTC");*/
//		a.anneal("ABC","abc");
		a.anneal(
				"GAAATTAATACGACTCACTATAGGACCTAGAGACATGGGGAGTCGTTTTAGAGCTAGAAATAGCAAGTTAAAATAAGGCTAGTCCG", 
				"AAAAAAGCACCGACTCGGTGCCACTTTTTCAAGTTGATAACGGACTAGCCTTATTTTAACTTGC");
		}
	}
