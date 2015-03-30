package collagene.gui.paneRestriction;

import java.util.LinkedList;

import collagene.restrictionEnzyme.RestrictionEnzyme;

/**
 * 
 * 
 * @author Johan Henriksson
 *
 */
public class EventSelectedRestrictionEnzyme
	{
	public LinkedList<RestrictionEnzyme> enzymes=new LinkedList<RestrictionEnzyme>();
	
	
	
	public boolean hasEnzyme(RestrictionEnzyme thisenz)
		{
		return enzymes.contains(thisenz);
		}

	public void add(RestrictionEnzyme enzyme)
		{
		enzymes.add(enzyme);
		}
	}
