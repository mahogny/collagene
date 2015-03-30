package collagene.melting;

import collagene.sequtil.NucleotideUtil;

/**
 * Wallace RB et al. (1979) Nucleic Acids Res 6:3543-3557, PMID 158748
 * 
 * @author Johan Henriksson
 *
 */
public class CalcTmWallace implements CalcTm
	{
	public double calcTm(String seq1, String seq2)
		{
		//Should really throw error if sequences not complementary
		
		//	Tm =  64.9°C + 41°C x (number of G’s and C’s in the primer – 16.4)/N
		int numgc=0;
		for(char c:seq1.toCharArray())
			if(c=='C' || c=='G')
				numgc++;
			else if(c=='A' || c=='T')  //N generally problematic
				;
			else
				throw new RuntimeException("Unknown nucleotide: "+c);
		return 64.9 + 41*(numgc-16.4)/seq1.length();
		}
	
	@Override
	public double calcTm(String sequence) throws TmException
		{
		return calcTm(sequence, NucleotideUtil.complement(sequence));
		}

	}
