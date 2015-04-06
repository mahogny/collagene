package collagene.alignment;

import java.util.SortedMap;
import java.util.TreeMap;

import collagene.primer.Primer;
import collagene.seq.AnnotatedSequence;
import collagene.seq.SeqAnnotation;


/**
 * 
 * Utility class to work with list of gaps. Used for aligned sequences
 * 
 * @author Johan Henriksson
 *
 */
public class GapListUtil
	{


	/**
	 * Get the shift for a position, given shift map
	 */
	public static int getShiftAt(TreeMap<Integer, Integer> mapCumulative, int pos)
		{
		SortedMap<Integer, Integer> before=mapCumulative.headMap(pos);
		if(before.isEmpty())
			return 0;
		else
			return before.get(before.lastKey());
		}
	
	
	/**
	 * Compute cumulative shift map
	 */
	public static TreeMap<Integer, Integer> computeCumulativeGaps(TreeMap<Integer, Integer> mapInserts)
		{
		TreeMap<Integer, Integer> mapCumulative=new TreeMap<Integer, Integer>(); //Position, length
		int lastshift=0;
		for(int pos:mapInserts.keySet())
			{
			int ins=mapInserts.get(pos);
			lastshift+=ins;
			mapCumulative.put(pos, lastshift);
			}
		return mapCumulative;
		}
	
	
	/**
	 * Compute insert positions and lengths
	 */
	public static TreeMap<Integer, Integer> computeGaps(String sequence)
		{
		char gapseq=' ';
		TreeMap<Integer, Integer> mapInserts=new TreeMap<Integer, Integer>(); //Position, length
		for(int i=0;i<sequence.length();)
			{
			char c=sequence.charAt(i);
			if(c==gapseq)
				{
				int j=i;
				while(sequence.charAt(j)==gapseq && j<sequence.length())
					j++;
				mapInserts.put(i, j-i);
				i=j;
				}
			else
				i++;
			}
		return mapInserts;
		}
	
	

	/**
	 * Shift features according to shift map (add gaps)
	 */
	public static void shiftFeaturesAddingGaps(AnnotatedSequence seq, TreeMap<Integer, Integer> mapCumulative)
		{
		for(SeqAnnotation annot:seq.annotations)
			{
			annot.range.from+=getShiftAt(mapCumulative, annot.range.from);
			annot.range.to+=getShiftAt(mapCumulative, annot.range.to);
			}
		for(Primer p:seq.primers)
			p.targetPosition+=getShiftAt(mapCumulative, p.targetPosition);
		//Best to just recompute restriction sites
		}

	/**
	 * Shift features according to shift map (remove gaps)
	 */
	public static void shiftFeaturesRemovingGaps(AnnotatedSequence seq, TreeMap<Integer, Integer> mapCumulative)
		{
		shiftFeaturesAddingGaps(seq, invertGapSign(mapCumulative));
		}
	private static TreeMap<Integer, Integer> invertGapSign(TreeMap<Integer, Integer> mapCumulative)
		{
		TreeMap<Integer, Integer> t=new TreeMap<Integer, Integer>();
		for(int pos:mapCumulative.keySet())
			t.put(pos,-mapCumulative.get(t));
		return t;
		}
	
	
	/**
	 * Merge in splits to find a consensus split. This is needed for multiple alignment
	 */
	public static void makeConsensusSplits(TreeMap<Integer, Integer> totsplit, TreeMap<Integer, Integer> onesplit)
		{
		for(int pos:onesplit.keySet())
			{
			Integer prevlen=totsplit.get(pos);
			if(prevlen==null)
				prevlen=0;
			prevlen=Math.max(prevlen,onesplit.get(pos));
			totsplit.put(pos, prevlen);
			}
		}
	
	}
