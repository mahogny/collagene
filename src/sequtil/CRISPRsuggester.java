package sequtil;

/**
 * Find CRISPR sites and design oligos to order
 * 
 * @author Johan Henriksson
 *
 */
public class CRISPRsuggester
	{
	public static String suggestCrispr(String seq)
		{
		return suggestCrispr1(seq) +"------------------ Reverse ----------------------- \n"+suggestCrispr1(NucleotideUtil.revcomplement(seq));
		}
	
	private static String suggestCrispr1(String seq)
		{
		StringBuilder sb=new StringBuilder();
		
		
		//Find all GGs
		for(int i=20;i<seq.length()-1;i++)
			{
			if(seq.charAt(i)=='G' && seq.charAt(i+1)=='G')
				{
				//Traverse back a suitable distance
				
				int j=i-20;
				for(;j>=0;j--)
					{
					if(seq.charAt(j)=='G')
						{
						String tfull=seq.substring(j,i+2);
						String t=seq.substring(j,i-1);
						sb.append("Target: "+tfull+"\n");
						sb.append("Position: "+j+" to "+i+"\n");
						sb.append("_FWD, CACC "+t+" GT\n");
						sb.append("_REV, TAAAAC "+NucleotideUtil.revcomplement(t)+" \n\n");
						
						break;
						}
					}
				
				
				}
			}
		return sb.toString();
		}

	}
