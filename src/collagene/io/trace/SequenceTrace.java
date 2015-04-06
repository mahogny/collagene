package collagene.io.trace;

import java.util.ArrayList;
import java.util.HashMap;

import collagene.sequtil.NucleotideUtil;


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


	/**
	 *
	 * Returns a "rotated" trace, with bases complemented
	 * 
	 */
	public SequenceTrace rotated()
		{
		SequenceTrace newtrace=new SequenceTrace();

		newtrace.levelA=new int[getLevelLength()];
		newtrace.levelT=new int[getLevelLength()];
		newtrace.levelC=new int[getLevelLength()];
		newtrace.levelG=new int[getLevelLength()];
		newtrace.properties.putAll(properties);
		
		for(int i=basecalls.size()-1;i>=0;i--)
			{
			SequenceTraceBaseCall oldbc=basecalls.get(i);
			SequenceTraceBaseCall newbc=new SequenceTraceBaseCall();
			newtrace.basecalls.add(newbc);
			
			newbc.base=NucleotideUtil.complement(oldbc.base);
			newbc.pA=oldbc.pT;
			newbc.pT=oldbc.pA;

			newbc.pG=oldbc.pC;
			newbc.pC=oldbc.pG;
			newbc.peakIndex=getNumBases()-oldbc.peakIndex-1;
			}
		for(int i=0;i<getLevelLength();i++)
			{
			int oldi=getLevelLength()-i-1;
			newtrace.levelA[i]=levelT[oldi];
			newtrace.levelT[i]=levelA[oldi];
			newtrace.levelG[i]=levelC[oldi];
			newtrace.levelC[i]=levelG[oldi];
			}
		return newtrace;
		}


	public String getTrustedSequence()
		{
		int maxphred=0;
		for(int i=0;i<getNumBases();i++)
			maxphred=Math.max(maxphred,basecalls.get(i).getProb());
		int trustfrom=0,trustto=0;
		for(int i=0;i<getNumBases();i++)
			if(basecalls.get(i).getProb()>maxphred*0.6)
				{
				trustfrom=i;
				break;
				}
		for(int i=0;i<getNumBases();i++)
			if(basecalls.get(i).getProb()>maxphred*0.6)
				trustto=i;
		System.out.println("TRUST "+trustfrom+"\t"+trustto);
		StringBuilder sb=new StringBuilder();
		for(int i=trustfrom;i<=trustto;i++)
			sb.append(basecalls.get(i).base);
		return sb.toString();
		}

	}
