package seq;

public class SeqColor
	{
	public int r,g,b;

	public SeqColor()
		{
		
		}
	
	
	public SeqColor(int r, int g, int b)
		{
		this.r=r;
		this.g=g;
		this.b=b;
		}

	public SeqColor(SeqColor col)
		{
		this.r=col.r;
		this.g=col.g;
		this.b=col.b;
		}


	@Override
	public String toString()
		{
		return "("+r+","+g+","+b+")";
		}


	public String getColorAsRGBstring()
		{
		return to2hex(r)+to2hex(g)+to2hex(b);
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
	
	
	public int getLightness()
		{
		int max=Math.max(Math.max(r,g),b);
		int min=Math.min(Math.min(r,g),b);
		return (max+min)/2;
		}

	@Override
	public boolean equals(Object obj)
		{
		if(obj instanceof SeqColor)
			{
			SeqColor o=(SeqColor)obj;
			return r==o.r && g==o.g && b==o.b;
			}
		else
			return false;
		}


	}
