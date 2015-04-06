package collagene.gui.paneLinear.tracks;

import collagene.io.trace.SequenceTrace;

/**
 * 
 * likely need to split into another "processed trace"
 * 
 * @author Johan Henriksson
 *
 */
public class PlacedTrace
	{
	private SequenceTrace trace;
	public int from=0;
	
	//Derived data
	int maxProb;
	private double[] avglevel;

	public int getFrom()
		{
		return from;
		}

	public int getTo()
		{
		return from+trace.getNumBases();
		}
	
	
	public SequenceTrace getTrace()
		{
		return trace;
		}
	
	
	public void setTrace(SequenceTrace trace)
		{
		this.trace=trace;
		
		maxProb=0;
		for(int i=0;i<trace.getNumBases();i++)
			maxProb=Math.max(maxProb, trace.basecalls.get(i).getProb());
		
		int[] intlevel=new int[trace.getLevelLength()+1];
		for(int i=0;i<trace.getLevelLength();i++)
			{
			intlevel[i+1]=intlevel[i]
					+trace.levelA[i]+trace.levelC[i]+trace.levelG[i]+trace.levelT[i];
			}
		avglevel=new double[trace.getLevelLength()];
		for(int i=0;i<trace.getLevelLength();i++)
			{
			int wsize=50;
			int from=Math.max(i-wsize,0);
			int to=Math.min(i+wsize, intlevel.length-1);
			avglevel[i] = (intlevel[to]-intlevel[from])/(double)(to-from);
			}
		}
	
	public double getLocalAverage(int i)
		{
		return avglevel[i];
		}

	public void rotate()
		{
		setTrace(trace.rotated());
		}
	
	
	}
