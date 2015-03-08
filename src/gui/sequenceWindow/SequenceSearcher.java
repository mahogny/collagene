package gui.sequenceWindow;

import java.util.SortedSet;
import java.util.TreeSet;

import seq.AnnotatedSequence;
import seq.SequenceRange;
import sequtil.NucleotideUtil;


/**
 * 
 * 
 * @author Johan Henriksson
 *
 */
public class SequenceSearcher
	{
	private TreeSet<Integer> positions=new TreeSet<Integer>();
	private String currentSearchString;
	
	
	
	public SequenceSearcher(AnnotatedSequence seq, String currentSearchString)
		{
		this.currentSearchString=currentSearchString;
		String upper=seq.getSequence();
		String lower=seq.getSequenceLower();
		String search2=NucleotideUtil.reverse(currentSearchString);
		
		if(seq.isCircular)
			{
			upper=upper+upper;
			lower=lower+lower;
			}
		
		int curpos=0;
		for(;;)
			{
			curpos=upper.indexOf(currentSearchString,curpos); 
			if(curpos>=seq.getLength() || curpos==-1)
				break;
			positions.add(curpos);
			curpos++;
			}
		
		curpos=0;
		for(;;)
			{
			curpos=lower.indexOf(search2,curpos); //how does this treat first position?
			if(curpos>=seq.getLength() || curpos==-1)
				break;
			positions.add(curpos);
			curpos++;
			}
		}

	
	/**
	 * Find next hit
	 */
	public SequenceRange next(SequenceRange current)
		{
		int next;
		int pos=0;
		if(current!=null)
			pos=current.from;
		
		SortedSet<Integer> set=positions.tailSet(pos+1);
		if(set.isEmpty())
			{
			if(positions.isEmpty())
				return null;
			else
				next=positions.first();
			}
		else
			next=set.first();
		
		return new SequenceRange(next,next+currentSearchString.length());
		}
	
	
	/**
	 * Find previous hit
	 */
	public SequenceRange prev(SequenceRange current)
		{
		int next;
		int pos=0;
		if(current!=null)
			pos=current.from;
		
		SortedSet<Integer> set=positions.headSet(pos);
		if(set.isEmpty())
			{
			if(positions.isEmpty())
				return null;
			else
				next=positions.last();
			}
		else
			next=set.last();
		
		return new SequenceRange(next,next+currentSearchString.length());
		}
	
	}
