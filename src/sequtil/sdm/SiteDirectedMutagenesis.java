package sequtil.sdm;

import primer.Primer;
import seq.AnnotatedSequence;
import seq.Orientation;
import seq.SequenceRange;
import sequtil.NucleotideUtil;

/**
 * Utility to perform site directed mutagenesis
 * 
 * @author Johan Henriksson
 *
 */
public class SiteDirectedMutagenesis
	{
	/**
	 * Calculate Tm according to the QuikChange manual.
	 * 
	 * There is also another formula for introducing insertions and deletions
	 */
	public static double tmMismatch(String s, double percMismatch)
		{
		int gc=NucleotideUtil.countGC(s);
		double percGC=gc/(double)s.length();
		return 81.5 + 41*percGC - 675.0/s.length() - percMismatch*100;
		}
	
	
	
	
	public static class SiteDirectedMutagenesisCandidate
		{
		public Primer fwd, rev;
		public double tm;
		}

	/**
	 * Design primers to perform the SDM
	 */
	public static SiteDirectedMutagenesisCandidate designPrimer(AnnotatedSequence seq, SequenceRange region, String newmid, String name, SiteDirectedMutagenesisStrategy strategy)
		{
		if(strategy==SiteDirectedMutagenesisStrategy.QUIKCHANGE)
			return designPrimerQuikchange(seq, region, newmid, name);
		else
			throw new RuntimeException("no strategy");
		}
	
	
	/**
	 * Add 10-15 bases on each side
	 * Good with G or C on either side
	 * GC>40% recommended, and Tm>78C
	 */
	public static SiteDirectedMutagenesisCandidate designPrimerQuikchange(AnnotatedSequence seq, SequenceRange region, String newmid, String name)
		{
		newmid=newmid.replace(" ", "").toUpperCase();
		int lenbefore=15;
		int lenafter=15;
		
		for(;;)
			{
			SequenceRange rangeBefore=new SequenceRange(region.from-lenbefore, region.from);
			SequenceRange rangeTo=new SequenceRange(region.to, region.to+lenafter);

			String oldmid=seq.getSequence(new SequenceRange(region.from,region.to));
			int lenmid=oldmid.length();
			
			//Calculate mismatch %. An alignment would be more precise!
			int cntmismatch=0;
			for(int i=0;i<oldmid.length() && i<newmid.length();i++)
				if(newmid.charAt(i)!=oldmid.charAt(i))
					cntmismatch++;
			double percMismatch=cntmismatch/(double)(lenbefore+lenmid+lenafter);

			SiteDirectedMutagenesisCandidate cand=new SiteDirectedMutagenesisCandidate();

			String outputseq=seq.getSequence(rangeBefore) + newmid + seq.getSequence(rangeTo);
			cand.tm=tmMismatch(outputseq, percMismatch);
			System.out.println("The Tm would be "+cand.tm+", should be above 78");
			if(cand.tm<78)
				{
				lenbefore++;
				lenafter++;
				continue;
				}
			System.out.println("current len "+lenbefore);
			
			Primer pFwd=new Primer();
			pFwd.sequence=outputseq;
			pFwd.targetPosition=region.to+lenafter;
			pFwd.name=name+"_fwd";
			pFwd.orientation=Orientation.FORWARD;
			
			Primer pRev=new Primer();
			pRev.sequence=NucleotideUtil.revcomplement(outputseq);
			pRev.targetPosition=region.from-lenbefore;
			pRev.name=name+"_rev";
			pRev.orientation=Orientation.REVERSE;
			
			cand.fwd=pFwd;
			cand.rev=pRev;
			
			return cand;
			}
		}
	
	
	}
