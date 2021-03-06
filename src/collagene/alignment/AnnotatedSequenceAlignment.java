package collagene.alignment;

import collagene.alignment.emboss.EmbossCost;
import collagene.seq.AnnotatedSequence;
import collagene.sequtil.NucleotideUtil;

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

	public boolean isLocal=false;
	public boolean canGoOutside=true;
	public boolean includeAllA=false;
	
	public void align(AnnotatedSequence seqA, AnnotatedSequence seqB)
		{
		AnnotatedSequence seqC=new AnnotatedSequence(seqB);
		seqC.reverseSequence();
	
		//support lin-lin and circ-lin. circ-circ is overkill!
//		if(seqA.isCircular)
		
		
		PairwiseAlignment alB=new PairwiseAlignment();
		PairwiseAlignment alC=new PairwiseAlignment();
		alB.isLocalAlignment=isLocal;
		alC.isLocalAlignment=isLocal;
		alB.canGoOutside=canGoOutside;
		alC.canGoOutside=canGoOutside;
		alB.includeAllA=includeAllA;
		alC.includeAllA=includeAllA;
		
		alB.costtable=costtable;
		alC.costtable=costtable;
		alB.align(seqA.getSequence(), seqB.getSequence());
		alC.align(seqA.getSequence(), seqC.getSequence());

		/*
		System.out.println("X "+seqA.getSequence());
		System.out.println("B "+alB.alignedSequenceA);
		System.out.println("B "+alB.alignedSequenceB);
		System.out.println("!!!!");
		System.out.println("C "+alC.alignedSequenceA);
		System.out.println("C "+alC.alignedSequenceB);
		*/
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
	
		alSeqA=new AnnotatedSequence(seqA);
		alSeqA.setSequence(bestal.alignedSequenceA);
		if(!isLocal)
			alSeqA.isCircular=seqA.isCircular;
		
		alSeqB=new AnnotatedSequence(seqB);
		alSeqB.setSequence(bestal.alignedSequenceB);
		if(!isLocal)
			alSeqB.isCircular=seqB.isCircular;
		
		GapListUtil.shiftFeaturesAddingGaps(alSeqA, GapListUtil.computeCumulativeGaps(GapListUtil.computeGaps(bestal.alignedSequenceA)));
		GapListUtil.shiftFeaturesAddingGaps(alSeqB, GapListUtil.computeCumulativeGaps(GapListUtil.computeGaps(bestal.alignedSequenceB))); 
				//This will be wrong!!! need to take first index into account
		
		alSeqAwithB=new AnnotatedSequence(alSeqA);
		alSeqAwithB.setSequence(bestal.alignedSequenceA, NucleotideUtil.complement(bestal.alignedSequenceB));
		/*
		System.out.println(bestal.alignedIndexB);
		
		System.out.println(
				computeCumulativeGaps(moveFeaturesByGaps(alC.alignedSequenceB))
				);*/
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
