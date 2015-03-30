package collagene.io.trace;

/**
 * One called base
 * 
 * @author Johan Henriksson
 */
public class SequenceTraceBaseCall
	{
	//Probabilities: 0-8, with 9 being manually edited. Higher is better
	public int peakIndex;
	public int pA, pC, pG, pT;
	public char base;
	
	public int getProbLetter()
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