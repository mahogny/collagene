package seq;

/**
 * A selection range over a sequence
 * 
 * @author Johan Henriksson
 *
 */
public class SequenceRange
	{
	public int from, to;

	public int getLower()
		{
		return Math.min(from, to);
		}
	public int getUpper()
		{
		return Math.max(from,to);
		}
	public int getSize()
		{
		return Math.abs(from-to);
		}

	}
