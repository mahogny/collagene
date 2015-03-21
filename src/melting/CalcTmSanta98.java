package melting;

import java.util.HashMap;

import sequtil.NucleotideUtil;

/**
 * Santa lucia 98
 * 
 * http://www.ncbi.nlm.nih.gov/pmc/articles/PMC19045/pdf/pq001460.pdf
 * 
 * http://pubs.acs.org/doi/abs/10.1021/bi9825091
 * 
 * I think this might be great reading http://www.ebi.ac.uk/compneur-srv/melting/melting-doc/melting.html
 * 
 * how neb does it: http://tmcalculator.neb.com/#!/help
 * 
 * @author Johan Henriksson
 *
 */
public class CalcTmSanta98 implements CalcTm
	{
	private HashMap<String, Double> mapdH=new HashMap<String, Double>();
	private HashMap<String, Double> mapdS=new HashMap<String, Double>();

	private double initGCdH=0.1;
	private double initGCdS=-2.8;
	
	private double initATdH=2.1;
	private double initATdS=4.1;
	
	/*
	private double symdH=0;          not used... hm
	private double symdS=-1.4;
	*/
	
	
	public double concNa;   //[M] or other monovalent
	public double concMg2;  //[M] or other divalent
	public double concDNA;  //[M] total oligonucleotide concentration, C_T in paper
	public double concDntp; //[M]
	
	
	
	private void reg(String seq1,String seq2,double dH, double dS)
		{
		String revseq1=""+seq1.charAt(1)+seq1.charAt(0);
		String revseq2=""+seq2.charAt(1)+seq2.charAt(0);
		regs(seq1,seq2,dH,dS);
		regs(seq2,seq1,dH,dS);
		regs(revseq1,revseq2,dH,dS);
		regs(revseq2,revseq1,dH,dS);
		}

	private void regs(String seq1,String seq2,double dH, double dS)
		{
		mapdH.put(seq1+"/"+seq2, dH*1000);
		mapdS.put(seq1+"/"+seq2, dS);
		}
	
	public CalcTmSanta98()
		{
		reg("AA","TT",-7.9, -22.2);
		reg("AT","TA",-7.2, -20.4);
		reg("TA","AT",-7.2, -21.3);
		reg("CA","GT",-8.5, -22.7);
		reg("GT","CA",-8.4, -22.4);
		reg("CT","GA",-7.8, -21.0);
		reg("GA","CT",-8.2, -22.2);
		reg("CG","GC",-10.6,-27.2);
		reg("GC","CG",-9.8, -24.4);
		reg("GG","CC",-8.0, -19.9);
		
		setDefaultsPrimer3();
		}
	
	
	public double calcTm(String seq1, String seq2) throws TmException
		{
		double dH=0;
		double dS=0;

		//Starting penalty for helix initialization
		if(seq1.startsWith("AT") || seq1.startsWith("TA"))
			{
			dS+=initATdS;
			dH+=initATdH;
			}
		if(seq1.startsWith("GC") || seq1.startsWith("CG"))
			{
			dS+=initGCdS;
			dH+=initGCdH;
			}
		
		//Pair-wise contributions
		seq1=seq1.toUpperCase();
		seq2=seq2.toUpperCase();
		for(int i=0;i<seq1.length()-1;i++)
			{
			String part1=seq1.substring(i,i+2);
			String part2=seq2.substring(i,i+2);
			Double partdH=mapdH.get(part1+"/"+part2);
			Double partdS=mapdS.get(part1+"/"+part2);
			
			if(partdH==null)
				throw new TmException("Missing: "+part1+"/"+part2);
			
			dH+=partdH;
			dS+=partdS;
			}
		
	
		//Monovalent salt correction
		int N=seq1.length()-1;  
		double concMonovalent=concNa;
		concMonovalent += divalentToMonovalent(concMg2, concDntp);
		dS +=  0.368*N*Math.log(concMonovalent); 

		//Consider adding correction according to Owczarzy, see NEB
		
		//Correction for symmetry, and final calculation
		double R=1.987;
		double Tm;
		if(isSymmetric(seq1))
			Tm = dH / (dS + R*Math.log(concDNA)) - 273.15;
		else
			Tm = dH / (dS + R*Math.log(concDNA/4)) - 273.15;
		return Tm;
		}
	
	double divalentToMonovalent(double divalent, double dntp)
		{
		if(divalent==0) 
			dntp=0;
		if(divalent<0 || dntp<0)
			throw new RuntimeException("Cannot compute");
		if(divalent<dntp) 
			divalent=dntp;  
		return 120*(Math.sqrt(divalent-dntp));
		}
	
	/**
	 * Check for self-complementarity
	 */
	private boolean isSymmetric(String s)
		{
		for(int i=0;i<s.length()/2;i++)
			{
			char c=s.charAt(i);
			char b=s.charAt(s.length()-i-1);
			if(
					(c=='A' && b!='T') ||
					(c=='T' && b!='A') ||
					(c=='C' && b!='G') ||
					(c=='G' && b!='C'))
				return false;
			}
		return true;
		}
	
	public static void main(String[] args)
		{
		try
			{
			CalcTmSanta98 m=new CalcTmSanta98();
			System.out.println(m.calcTm("CGTTGA", "GCAACT"));
			System.out.println(m.calcTm(
					"CGTTGACGTTGACGTTGA", 
					"GCAACTGCAACTGCAACT"));
			}
		catch (TmException e)
			{
			e.printStackTrace();
			}
		}
	
	
	public void setDefaultsPrimer3()
		{
		concDNA=50e-9;
		concMg2=1.5e-3;
		concNa=50e-3;
		concDntp=0.6e-3;
		}
	
	public void setDefaultsBenchling()
		{
		concDNA=250e-9;
		concMg2=0e-3;
		concNa=50e-3;
		concDntp=0e-3;
		}

	@Override
	public double calcTm(String sequence) throws TmException
		{
		return calcTm(sequence, NucleotideUtil.complement(sequence));
		}
	}
