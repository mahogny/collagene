package seq;

/**
 * One annotation (feature) of a sequence
 * 
 * @author Johan Henriksson
 *
 */
public class SeqAnnotation
	{
	public String name;
	public String note;
	
	public int from, to; //starting from 0, in format [from,to)
	
	public Orientation orientation=Orientation.FORWARD;
	
	public double colorR=1, colorG=0, colorB=0;

	
	
	public SeqAnnotation()
		{
		}
	
	public SeqAnnotation(SeqAnnotation annot)
		{
		name=annot.name;
		note=annot.note;
		from=annot.from;
		to=annot.to;
		orientation=annot.orientation;
		colorR=annot.colorR;
		colorG=annot.colorG;
		colorB=annot.colorB;
		}



	public boolean isOverlapping(SeqAnnotation o)
		{
		return from<=o.to && to>=o.from;  //TODO likely a >= here
		}



	public String getColorAsRGBstring()
		{
		return to2hex(colorR)+to2hex(colorG)+to2hex(colorB);
		}
	
	private String to2hex(double d)
		{
		int i=(int)(255*d);
		if(i>255)
			i=255;
		String s=Integer.toHexString(i);
		if(s.length()==1)
			return "0"+s;
		else
			return s;
		}

	@Override
	public String toString()
		{
		return "("+from+","+to+")";
		}

	public int length(AnnotatedSequence seq)
		{
		if(seq.isCircular)
			return seq.getLength()-to-from;
		else
			return to-from;
		}
	}
