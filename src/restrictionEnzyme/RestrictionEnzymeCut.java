package restrictionEnzyme;

/**
 * One cut for a restriction enzyme (which can have up to 2)
 * 
 * @author Johan Henriksson
 *
 */
public class RestrictionEnzymeCut
	{
	public Integer upper=null;
	public Integer lower=null;
	
	public String toString()
		{
		return "("+upper+"/"+lower+")";
		}
	}