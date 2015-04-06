package collagene.alignment;

import java.util.Collection;
import java.util.LinkedList;
import collagene.gui.paneLinear.tracks.PlacedTrace;
import collagene.io.trace.SequenceTrace;
import collagene.seq.AnnotatedSequence;

/**
 * 
 * Piece together traces
 * 
 * @author Johan Henriksson
 *
 */
public class AlignTraces
	{
	public AnnotatedSequence refseq;	
	public LinkedList<PlacedTrace> placedtraces=new LinkedList<PlacedTrace>();
	
	
	public void build(AnnotatedSequence refseq, Collection<SequenceTrace> traces)
		{
		refseq=new AnnotatedSequence(refseq);
		this.refseq=refseq;
		placedtraces.clear();
		for(SequenceTrace t:traces)
			{
			PlacedTrace pt=new PlacedTrace();
			pt.setTrace(t);
			placedtraces.add(pt);
			refseq.traces.add(pt);
			}
		
		
		align();
		}

	
	public void align()
		{
		//Different ways depending on if there is a reference or not
		

		//For each trace, align it with genome. global alignment, but no cost for going outside the region
		for(PlacedTrace t:placedtraces)
			{
			//Try to align both ways
			AnnotatedSequence seqb=new AnnotatedSequence();
			seqb.setSequence(t.getTrace().getTrustedSequence());
			
			
			
			AnnotatedSequenceAlignment al=new AnnotatedSequenceAlignment();
			al.isLocal=true; //????
			al.canGoOutside=true;
			al.includeAllA=false;
			//al.costtable.penaltySkip=-200; //Essentially, disallow gaps? or only allow a few ones?
			al.align(refseq, seqb);

			if(al.rotateB)
				{
				t.rotate();
				System.out.println("rotated!");
				}
			System.out.println("------------");
			System.out.println(al.bestal.alignedSequenceA);
			System.out.println(al.bestal.alignedSequenceB);
			System.out.println("----------");
			
			//Insert gaps into reference, but also previously added traces
			//Sequence may already have gaps! but also, may have too many gaps. in that case, should also correct the trace

			
			t.from=al.bestal.getStartOfA()+al.bestal.getStartOfB();   //totally wrong. but close
			
			//May later need a function to re-align, given new choice of what parts of traces to consider
			//Need "trustFrom, trustTo"
			}
		
		//If the sequence is linear, then expand it if needed
		//TODO
	
		//Now pull over the annotation from the original reference, taking gaps into account
		
		}
	
	/**
	 * 
	 * consensus: we can collect for each aligned sequence where it wants to insert splits in the reference.
	 * this can be map<pos,length>. these lists can easily be merged
	 * 
	 * See GapList
	 * 
	 */

	/**
	 * note: already have code to transform split list into integral split list!
	 */

	/**
	 * todo: code to take a sequence and make a split list
	 */
	
	}
