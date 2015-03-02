package seq;

import gui.resource.LabnoteUtil;
import restrictionEnzyme.RestrictionEnzyme;


/**
 * Not yet sure how to handle this...
 * 
 * @author Johan Henriksson
 *
 */
public class RestrictionSite
	{
	public RestrictionEnzyme enzyme;
	public Integer cuttingUpperPos;
	public Integer cuttingLowerPos;
	
	//Note that some enzymes only nick one side
	
	//There might here be a nasty corner case over circular plasmids
	

	@Override
	public String toString()
		{
		return "site: "+enzyme.name;
		}
	
	
	@Override
	public boolean equals(Object obj)
		{
		if(obj instanceof RestrictionSite)
			{
			RestrictionSite o=(RestrictionSite)obj;
			return enzyme.equals(o.enzyme) &&
					LabnoteUtil.equalsNull(cuttingUpperPos, o.cuttingUpperPos) &&
					LabnoteUtil.equalsNull(cuttingLowerPos, o.cuttingLowerPos);
			}
		else
			return false;
		}
	
	@Override
	public int hashCode()
		{
		int c=0;
		if(cuttingUpperPos!=null)
			c+=cuttingUpperPos;
		if(cuttingLowerPos!=null)
			c+=cuttingLowerPos;
		return c;
		}
	}
