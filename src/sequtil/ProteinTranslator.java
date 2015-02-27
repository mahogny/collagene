package sequtil;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * 
 * 
 * 
 * @author Johan Henriksson
 *
 */
public class ProteinTranslator
	{
	public HashMap<String,String> dnaToAminoLetter=new HashMap<String, String>();

	public HashMap<String,String> aminoLetterToName=new HashMap<String, String>();

	public HashMap<String, LinkedList<String>> aminoToDNA=new HashMap<String, LinkedList<String>>();
	
	public void addTrans(String aminoName, String aminoLetter, String... tripletList)
		{
		for(String triplet:tripletList)
			{
			dnaToAminoLetter.put(triplet,aminoLetter);
			
			LinkedList<String> list=aminoToDNA.get(aminoLetter);
			if(list==null)
				aminoToDNA.put(aminoLetter,list=new LinkedList<String>());
			list.add(triplet);
			}
		aminoLetterToName.put(aminoLetter,aminoName);
		}

	
	public ProteinTranslator()
		{
		addTrans("Isoleucine","I","ATT","ATC","ATA");
		addTrans("Leucine","L","CTT","CTC","CTA","CTG","TTA","TTG");
		addTrans("Valine","V","GTT","GTC","GTA","GTG");
		addTrans("Phenylalanine","F","TTT","TTC");
		addTrans("Methionine","M","ATG");
		addTrans("Cysteine","C","TGT","TGC");
		addTrans("Alanine","A","GCT","GCC","GCA","GCG");
		addTrans("Glycine","G","GGT","GGC","GGA","GGG");
		addTrans("Proline","P","CCT","CCC","CCA","CCG");
		addTrans("Threonine","T","ACT","ACC","ACA","ACG");
		addTrans("Serine","S","TCT","TCC","TCA","TCG","AGT","AGC");
		addTrans("Tyrosine","Y","TAT","TAC");
		addTrans("Tryptophan","W","TGG");
		addTrans("Glutamine","Q","CAA","CAG");
		addTrans("Asparagine","N","AAT","AAC");
		addTrans("Histidine","H","CAT","CAC");
		addTrans("Glutamic acid","E","GAA","GAG");
		addTrans("Aspartic acid","D","GAT","GAC");
		addTrans("Lysine","K","AAA","AAG");
		addTrans("Arginine","R","CGT","CGC","CGA","CGG","AGA","AGG");

		addTrans("Stop codons","*","TAA","TAG","TGA");
		}


	public String tripletToAminoLetter(String ss)
		{
		String trans=dnaToAminoLetter.get(ss.toUpperCase());
		//if(trans==null)
		//	System.out.println("Warning: could not translate "+ss);
		return trans;
		}

	/**
	 * Translate amino acid into randomized DNA sequence
	 */
	public String aminoToRandomTriplets(String ss)
		{
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<ss.length();i++)
			{
			String a=""+ss.charAt(i);
			LinkedList<String> list=aminoToDNA.get(a);
			if(list==null)
				throw new RuntimeException("Unknown amino acid "+a);
			sb.append(list.get((int)(Math.random()*list.size())));
			}
		return sb.toString();
		}

	
	}
