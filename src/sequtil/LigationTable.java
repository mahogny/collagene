package sequtil;

import java.util.HashMap;
import java.util.LinkedList;

import seq.AnnotatedSequence;
import seq.Orientation;

public class LigationTable
	{

	
	public LinkedList<AnnotatedSequence> sequences=new LinkedList<AnnotatedSequence>();
	public HashMap<Integer, HashMap<Integer, Orientation>> canLigate=new HashMap<Integer, HashMap<Integer,Orientation>>();

	/**
	 * Check how fragments can ligate: Either A-B, or A-rot(B)
	 */
	public void calculateTable()
		{
		for(int i=0;i<sequences.size();i++)
			for(int j=0;j<sequences.size();j++)
				{
				AnnotatedSequence seqA=sequences.get(i);
				AnnotatedSequence seqB=sequences.get(j);
				boolean checkAtoB=canLigateAtoB(seqA, seqB);
				boolean checkAtoRotB=canLigateAtoRotB(seqA, seqB);
				setCanLigate(i, j, toOrientation(checkAtoB, checkAtoRotB));
				}
		}

	/**
	 * Extract orientation from 2 checks
	 */
	private Orientation toOrientation(boolean fwd, boolean rev)
		{
		if(fwd)
			{
			if(rev)
				return Orientation.NOTORIENTED;
			else
				return Orientation.FORWARD;
			}
		else
			{
			if(rev)
				return Orientation.REVERSE;
			else
				return null;
			}
		}
	
	
	
	/**
	 * Set status of ligate:ability for fragment i to j
	 */
	public void setCanLigate(int i, int j, Orientation o)
		{
		HashMap<Integer, Orientation> m=canLigate.get(i);
		if(m==null)
			canLigate.put(i,m=new HashMap<Integer, Orientation>());
		m.put(j, o);
		}
	
	
	
	
	
	
	public static void main(String[] args)
		{
		LigationUtil lig=new LigationUtil();
		
		AnnotatedSequence seqA=new AnnotatedSequence();
		seqA.setSequence(
				"aaatttggg   ",
				"   tttgggttt");
		lig.addSequence(seqA);
		
		AnnotatedSequence seqB=new AnnotatedSequence();
		seqB.setSequence(
				"agatttggg   ",
				"   tttgggtct");
		lig.addSequence(seqB);

		AnnotatedSequence seqD=new AnnotatedSequence(seqB);
		seqD.reverseSequence();
		lig.addSequence(seqD);

		AnnotatedSequence seqC=new AnnotatedSequence();
		seqC.setSequence(
				"agatttggg",
				"   tttggg");
		lig.addSequence(seqC);
		
		
		lig.calculateTable();
		lig.showPairs();		
		}

	private void showPairs()
		{
		for(int i=0;i<sequences.size();i++)
			{
			for(int j=0;j<sequences.size();j++)
				System.out.print(canLigate.get(i).get(j)+"\t");
			System.out.println();
			}
		}

	private void addSequence(AnnotatedSequence seqA)
		{
		sequences.add(seqA);
		}

	}
