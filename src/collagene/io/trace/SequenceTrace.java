package collagene.io.trace;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * 
 * One sequence from a sequencer (that is, normally from Sanger sequencing)
 * 
 * @author Johan Henriksson
 *
 */
public class SequenceTrace
	{
	public int[] levelA, levelT, levelC, levelG;
	public HashMap<String, String > properties=new HashMap<String, String>();
	public ArrayList<SequenceTraceBaseCall> basecalls=new ArrayList<SequenceTraceBaseCall>();
	
	
	/**
	 * Get the length of the levels
	 */
	public int getLevelLength()
		{
		return levelA.length;
		}
	
	
	/**
	 * Get the called sequence
	 */
	public String getCalledSequence()
		{
		StringBuilder sb=new StringBuilder();
		for(SequenceTraceBaseCall bc:basecalls)
			sb.append(bc.base);
		return sb.toString();
		}


	public int getNumBases()
		{
		return basecalls.size();
		}


	public int[] getLevel(char c)
		{
		if(c=='A')
			return levelA;
		else if(c=='C')
			return levelC;
		else if(c=='G')
			return levelG;
		else if(c=='T')
			return levelT;
		else
			throw new RuntimeException("no such color "+c);
		}

	public int getMaxLevel(int pos)
		{
		return Math.max(
				Math.max(levelA[pos], levelC[pos]),
				Math.max(levelT[pos], levelG[pos]));
		}

	}
