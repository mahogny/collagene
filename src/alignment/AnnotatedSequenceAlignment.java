package alignment;

import java.util.SortedMap;
import java.util.TreeMap;

import alignment.emboss.EmbossCost;
import primer.Primer;
import seq.AnnotatedSequence;
import seq.SeqAnnotation;
import sequtil.NucleotideUtil;

/**
 * 
 * Alignment of two annotated sequences. With rotation of either
 * 
 * @author Johan Henriksson
 *
 */
public class AnnotatedSequenceAlignment
	{
	public boolean rotateB;
	public PairwiseAlignment bestal;

	public AnnotatedSequence alSeqA, alSeqB, alSeqAwithB;

	public AlignmentCostTable costtable=EmbossCost.tableBlosum62;
	
	public void align(AnnotatedSequence seqA, AnnotatedSequence seqB)
		{
		AnnotatedSequence seqC=new AnnotatedSequence(seqB);
		seqC.reverseSequence();
	
		//support lin-lin and circ-lin. circ-circ is overkill!
//		if(seqA.isCircular)
		
		
		PairwiseAlignment alB=new PairwiseAlignment();
		PairwiseAlignment alC=new PairwiseAlignment();
		alB.isLocalA=false;
		alC.isLocalA=false;
		alB.costtable=costtable;
		alC.costtable=costtable;
		alB.align(seqA.getSequence(), seqB.getSequence());
		alC.align(seqA.getSequence(), seqC.getSequence());

		
		System.out.println("X "+seqA.getSequence());
		System.out.println("B "+alB.alignedSequenceA);
		System.out.println("B "+alB.alignedSequenceB);
		System.out.println("!!!!");
		System.out.println("C "+alC.alignedSequenceA);
		System.out.println("C "+alC.alignedSequenceB);
		
		if(alB.bestCost>alC.bestCost)
			{
			bestal=alB;
			rotateB=false;
			}
		else
			{
			bestal=alC;
			rotateB=true;
			}
	
		alSeqA=new AnnotatedSequence();
		alSeqA.setSequence(bestal.alignedSequenceA);
		alSeqA.isCircular=seqA.isCircular;
		
		alSeqB=new AnnotatedSequence();
		alSeqB.setSequence(bestal.alignedSequenceB);
		alSeqB.isCircular=seqB.isCircular;
		
		shiftFeatures(alSeqA, computeCumulativeGaps(moveFeaturesByGaps(bestal.alignedSequenceA)));
		shiftFeatures(alSeqB, computeCumulativeGaps(moveFeaturesByGaps(bestal.alignedSequenceB))); //This will be wrong!!!
		
		alSeqAwithB=new AnnotatedSequence(alSeqA);
		alSeqAwithB.setSequence(bestal.alignedSequenceA, NucleotideUtil.complement(bestal.alignedSequenceB));
		
		System.out.println(bestal.alignedIndexB);
		
		System.out.println(
				computeCumulativeGaps(moveFeaturesByGaps(alC.alignedSequenceB))
				);
		}

	
	/**
	 * Shift features according to shift map
	 */
	public void shiftFeatures(AnnotatedSequence seq, TreeMap<Integer, Integer> mapCumulative)
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
	 * Get the shift for a position, given shift map
	 */
	private int getShiftAt(TreeMap<Integer, Integer> mapCumulative, int pos)
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
	public TreeMap<Integer, Integer> computeCumulativeGaps(TreeMap<Integer, Integer> mapInserts)
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
	public TreeMap<Integer, Integer> moveFeaturesByGaps(String s)
		{
		TreeMap<Integer, Integer> mapInserts=new TreeMap<Integer, Integer>(); //Position, length
		for(int i=0;i<s.length();)
			{
			char c=s.charAt(i);
			if(c=='_')
				{
				int j=i;
				while(s.charAt(j)=='_' && j<s.length())
					j++;
				mapInserts.put(i, j-i);
				i=j;
				}
			else
				i++;
			}
		return mapInserts;
		}
	
	
	public static void main(String[] args)
		{
		
		AnnotatedSequenceAlignment al=new AnnotatedSequenceAlignment();
		
		AnnotatedSequence seqA=new AnnotatedSequence();
		AnnotatedSequence seqB=new AnnotatedSequence();
		seqA.setSequence("GCGCCCAATACGCAAACCGCCTCTCCCCGCGCGTTGGCCGATTCATTAATGCAGCTGGCACGACAGGTTTCCCGACTGGAAAGCGGGCAGTGAGCGCAACGCAATTAATGTGAGTTAGCTCACTCATTAGGCACCCCAGGCTTTACACTTTATGCTTCCGGCTCGTATGTTGTGTGGAATTGTGAGCGGATAACAATTTCACACAGGAAACAGCTATGACCATGATTACGCCAAGCTTGCATGCCTGCAGGTCGACTCTAGAGGATCCCCGGGTACCGAGCTCGAATTCACTGGCCGTCGTTTTACAACGTCGTGACTGGGAAAACCCTGGCGTTACCCAACTTAATCGCCTTGCAGCACATCCCCCTTTCGCCAGCTGGCGTAATAGCGAAGAGGCCCGCACCGATCGCCCTTCCCAACAGTTGCGCAGCCTGAATGGCGAATGGCGCCTGATGCGGTATTTTCTCCTTACGCATCTGTGCGGTATTTCACACCGCATATGGTGCACTCTCAGTACAATCTGCTCTGATGCCGCATAGTTAAGCCAGCCCCGACACCCGCCAACACCCGCTGACGCGCCCTGACGGGCTTGTCTGCTCCCGGCATCCGCTTACAGACAAGCTGTGACCGTCTCCGGGAGCTGCATGTGTCAGAGGTTTTCACCGTCATCACCGAAACGCGCGAGACGAAAGGGCCTCGTGATACGCCTATTTTTATAGGTTAATGTCATGATAATAATGGTTTCTTAGACGTCAGGTGGCACTTTTCGGGGAAATGTGCGCGGAACCCCTATTTGTTTATTTTTCTAAATACATTCAAATATGTATCCGCTCATGAGACAATAACCCTGATAAATGCTTCAATAATATTGAAAAAGGAAGAGTATGAGTATTCAACATTTCCGTGTCGCCCTTATTCCCTTTTTTGCGGCATTTTGCCTTCCTGTTTTTGCTCACCCAGAAACGCTGGTGAAAGTAAAAGATGCTGAAGATCAGTTGGGTGCACGAGTGGGTTACATCGAACTGGATCTCAACAGCGGTAAGATCCTTGAGAGTTTTCGCCCCGAAGAACGTTTTCCAATGATGAGCACTTTTAAAGTTCTGCTATGTGGCGCGGTATTATCCCGTATTGACGCCGGGCAAGAGCAACTCGGTCGCCGCATACACTATTCTCAGAATGACTTGGTTGAGTACTCACCAGTCACAGAAAAGCATCTTACGGATGGCATGACAGTAAGAGAATTATGCAGTGCTGCCATAACCATGAGTGATAACACTGCGGCCAACTTACTTCTGACAACGATCGGAGGACCGAAGGAGCTAACCGCTTTTTTGCACAACATGGGGGATCATGTAACTCGCCTTGATCGTTGGGAACCGGAGCTGAATGAAGCCATACCAAACGACGAGCGTGACACCACGATGCCTGTAGCAATGGCAACAACGTTGCGCAAACTATTAACTGGCGAACTACTTACTCTAGCTTCCCGGCAACAATTAATAGACTGGATGGAGGCGGATAAAGTTGCAGGACCACTTCTGCGCTCGGCCCTTCCGGCTGGCTGGTTTATTGCTGATAAATCTGGAGCCGGTGAGCGTGGGTCTCGCGGTATCATTGCAGCACTGGGGCCAGATGGTAAGCCCTCCCGTATCGTAGTTATCTACACGACGGGGAGTCAGGCAACTATGGATGAACGAAATAGACAGATCGCTGAGATAGGTGCCTCACTGATTAAGCATTGGTAACTGTCAGACCAAGTTTACTCATATATACTTTAGATTGATTTAAAACTTCATTTTTAATTTAAAAGGATCTAGGTGAAGATCCTTTTTGATAATCTCATGACCAAAATCCCTTAACGTGAGTTTTCGTTCCACTGAGCGTCAGACCCCGTAGAAAAGATCAAAGGATCTTCTTGAGATCCTTTTTTTCTGCGCGTAATCTGCTGCTTGCAAACAAAAAAACCACCGCTACCAGCGGTGGTTTGTTTGCCGGATCAAGAGCTACCAACTCTTTTTCCGAAGGTAACTGGCTTCAGCAGAGCGCAGATACCAAATACTGTTCTTCTAGTGTAGCCGTAGTTAGGCCACCACTTCAAGAACTCTGTAGCACCGCCTACATACCTCGCTCTGCTAATCCTGTTACCAGTGGCTGCTGCCAGTGGCGATAAGTCGTGTCTTACCGGGTTGGACTCAAGACGATAGTTACCGGATAAGGCGCAGCGGTCGGGCTGAACGGGGGGTTCGTGCACACAGCCCAGCTTGGAGCGAACGACCTACACCGAACTGAGATACCTACAGCGTGAGCTATGAGAAAGCGCCACGCTTCCCGAAGGGAGAAAGGCGGACAGGTATCCGGTAAGCGGCAGGGTCGGAACAGGAGAGCGCACGAGGGAGCTTCCAGGGGGAAACGCCTGGTATCTTTATAGTCCTGTCGGGTTTCGCCACCTCTGACTTGAGCGTCGATTTTTGTGATGCTCGTCAGGGGGGCGGAGCCTATGGAAAAACGCCAGCAACGCGGCCTTTTTACGGTTCCTGGCCTTTTGCTGGCCTTTTGCTCACATGTTCTTTCCTGCGTTATCCCCTGATTCTGTGGATAACCGTATTACCGCCTTTGAGTGAGCTGATACCGCTCGCCGCAGCCGAACGACCGAGCGCAGCGAGTCAGTGAGCGAGGAAGCGGAAGA");
		seqB.setSequence("GGGTTCCGCGCACATTTCCCCGAAAAGTGCCACCTGACGTCTAAGAAACCATTATTATCATGACATTAACCTATAAAAATAGGCGTATCACGAGGCCCTTTCGTCTCGCGCGTTTCGGTGATGACGGTGAAAACCTCTGACACATGCAGCTCCCGGAGACGGTCACAGCTTGTCTGTAAGCGGATGCCGGGAGCAGACAAGCCCGTCAGGGCGCGTCAGCGGGTGTTGGCGGGTGTCGGGGCTGGCTTAACTATGCGGCATCAGAGCAGATTGTACTGAGAGTGCACCATATGCGGTGTGAAATACCGCACAGATGCGTAAGGAGAAAATACCGCATCAGGCGCCATTCGCCATTCAGGCTGCGCAACTGTTGGGAAGGGCGATCGGTGCGGGCCTCTTCGCTATTACGCCAGCTGGCGAAAGGGGGATGTGCTGCAAGGCGATTAAGTTGGGTAACGCCAGGGTTTTCCCAGTCACGACGTTGTAAAACGACGGCCAGTGAATTCGAGCTCGGTACCCGGGGATCCTCTAGAGTCGACCTGCAGGCATGCAAGCTTGGCGTAATCATGGTCATAGCTGTTTCCTGTGTGAAATTGTTATCCGCT");

		seqB.setSequence(NucleotideUtil.revcomplement(seqB.getSequence()));
		
		al.align(seqA, seqB);
		
		
		}
	
	}
