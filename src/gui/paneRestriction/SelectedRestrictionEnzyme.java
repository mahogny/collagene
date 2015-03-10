package gui.paneRestriction;

import java.util.LinkedList;

import restrictionEnzyme.RestrictionEnzyme;

/**
 * 
 * 
 * @author Johan Henriksson
 *
 */
public class SelectedRestrictionEnzyme
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
