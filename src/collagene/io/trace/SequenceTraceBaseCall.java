package collagene.io.trace;

/**
 * One called base
 * 
 * @author Johan Henriksson
 */
public class SequenceTraceBaseCall
	{
	public int peakIndex;
	public int pA, pC, pG, pT;
	public char base;
	
	public int getProb()
		{
		if(base=='A')
			return pA;
		else if(base=='C')
			return pC;
		else if(base=='G')
			return pG;
		else if(base=='T')
			return pT;
		else
			return 0;
		}

	
	}