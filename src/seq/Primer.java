package seq;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

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
				{
				System.out.println("111");
				return rev.targetPosition-fwd.targetPosition
						+fwd.length()+rev.length();
				}
			else
				return seq.getLength()-fwd.targetPosition+fwd.length() + rev.targetPosition + rev.length();
			}
		
		}

	
	public int length()
		{
		return sequence.length();
		}
	
	
	public LinkedList<PrimerPairInfo> getPairInfo(AnnotatedSequence seq)
		{
		LinkedList<PrimerPairInfo> list=new LinkedList<PrimerPairInfo>();
		
		for(Primer other:seq.primers)
			{
			//could maybe sort here?
			Integer bp=getProductLength(seq, other);
			if(bp!=null)
				{
				PrimerPairInfo i=new PrimerPairInfo();
				i.fwd=this;
				i.rev=other;
				i.productsize=bp;
				list.add(i);
				}
			}
		Collections.sort(list, new Comparator<PrimerPairInfo>()
			{
			public int compare(PrimerPairInfo o1, PrimerPairInfo o2)
				{
				return Integer.compare(o1.productsize, o2.productsize);
				}
			});
		return list;
		}

	}
