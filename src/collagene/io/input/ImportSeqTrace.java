package collagene.io.input;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import collagene.gui.paneLinear.tracks.PlacedTrace;
import collagene.io.SequenceImporter;
import collagene.io.trace.SequenceTrace;
import collagene.io.trace.TraceReaderSCF;
import collagene.seq.AnnotatedSequence;
import collagene.sequtil.NucleotideUtil;

/**
 * 
 * Wrapper to read sequence traces as content-less annotated sequences
 * 
 * @author Johan Henriksson
 *
 */
public class ImportSeqTrace implements SequenceImporter
	{
	@Override
	public boolean isType(InputStream is) throws IOException
		{
		try
			{
			new TraceReaderSCF().readFile(is);
			return true;
			}
		catch (Exception e)
			{
			System.out.println("not scf");
			//e.printStackTrace();
			return false;
			}
		}


	@Override
	public List<AnnotatedSequence> load(InputStream is) throws IOException
		{
		AnnotatedSequence seq=new AnnotatedSequence();
		
		SequenceTrace trace=new TraceReaderSCF().readFile(is);
		PlacedTrace pt=new PlacedTrace();
		pt.setTrace(trace);
		pt.from=0;
		if(trace.properties.containsKey("NAME"))
			seq.name=trace.properties.get("NAME");
		else
			seq.name="untitled";
		
		seq.setSequence(NucleotideUtil.getRepeatOligo(' ', pt.getTrace().getNumBases()));
		
		seq.traces.add(pt);
		
		return Arrays.asList(seq);
		}
	}
