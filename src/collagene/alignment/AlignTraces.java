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
		
		
		//For each trace, align it with genome. global alignment, but no cost for going outside the region
		for(PlacedTrace t:placedtraces)
			{
			//Try to align both ways
			PairwiseAlignment al=new PairwiseAlignment();
			al.isLocalAlignment=true;
			al.canGoOutside=true;
			al.align(refseq.getSequence(), t.getTrace().getCalledSequence());
			
			}
		
		
		}

	}
