package sequtil;

import java.util.LinkedList;

import seq.AnnotatedSequence;
import seq.Orientation;
import seq.SeqAnnotation;

/**
 * 
 * Find ORFs
 * 
 * @author Johan Henriksson
 *
 */
public class OrfFinder
	{
	public String startCodon="ATG"; //also suggest TTG, GTG, CTG
	public int minLength=50;
	
	

	public LinkedList<SeqAnnotation> find(AnnotatedSequence seq)
		{
		LinkedList<SeqAnnotation> annots=find1(seq);
		
		AnnotatedSequence seq2=new AnnotatedSequence();
		seq2.setSequence(NucleotideUtil.revcomplement(seq.getSequence()));
		LinkedList<SeqAnnotation> annots2=find1(seq2);
		
		for(SeqAnnotation a:annots2)
			{
			int from=reverseIndex(a.getTo(),seq);
			int to=reverseIndex(a.getFrom(),seq);  
			a.setRange(from,to);
			a.orientation=Orientation.REVERSE;
			}
		annots.addAll(annots2);
		return annots;
		}
	
	private int reverseIndex(int i,AnnotatedSequence seq)
		{
		return seq.getLength()-i; 
		}
	
	public LinkedList<SeqAnnotation> find1(AnnotatedSequence seq)
		{
		LinkedList<SeqAnnotation> annots=new LinkedList<SeqAnnotation>();
		
		//TODO circular plasmids?
		
		int index=0;
		for(;;)
			{
			index=seq.getSequence().indexOf(startCodon,index);  //If ATG is on the 0-boundary of a circular plasmid, need to check this specially. 
			if(index==-1)
				return annots;
			Integer indexStop=nextStop(seq.getSequence(), index+3);
			
			//If circular, wrap
			if(indexStop!=null)
				{
				SeqAnnotation a=new SeqAnnotation();
				a.range.from=index;
				a.range.to=indexStop;
				if(a.length(seq)>minLength)
					annots.add(a);
				}
			index++;
			}
		}
	
	private static Integer nextStop(String s, int index)
		{
		while(index<s.length()-2)
			{
			String sub=s.substring(index, index+3);
			if(sub.equals("TAA") || sub.equals("TGA") || sub.equals("TAG"))
				return index;
			index+=3;
			}
		return null;
		}
	
	
	public static void main(String[] args)
		{
		AnnotatedSequence seq=new AnnotatedSequence();
		seq.setSequence("atgTTTAAATTTAAATTTtag");
		System.out.println(new OrfFinder().find(seq));
		}
	}
