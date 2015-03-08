package seq;

/**
 * 
 * A primer attached to a sequence
 * 
 * @author Johan Henriksson
 *
 */
public class Primer
	{
	public Primer()
		{
		}
	public Primer(Primer p)
		{
		name=p.name;
		sequence=p.name;
		orientation=p.orientation;
		targetPosition=p.targetPosition;
		}
	
	public String name="";
	public String sequence="";

	public Orientation orientation=Orientation.FORWARD;
	public int targetPosition;

	
	public Integer getProductLength(AnnotatedSequence seq, Primer other)
		{
		if(other.orientation==orientation)
			return null;
		else
			{
			//Canonicalize orientation
			Primer fwd,rev;
			if(orientation==Orientation.FORWARD)
				{
				fwd=this;
				rev=other;
				}
			else
				{
				fwd=other;
				rev=this;
				}
			
			if(fwd.targetPosition<rev.targetPosition)
				return rev.targetPosition-fwd.targetPosition+fwd.length()+rev.length();
			else
				return seq.getLength()-fwd.targetPosition+fwd.length() + rev.targetPosition + rev.length();
			}
		
		}

	
	public int length()
		{
		return sequence.length();
		}

	}
