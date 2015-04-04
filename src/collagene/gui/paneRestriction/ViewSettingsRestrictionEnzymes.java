package collagene.gui.paneRestriction;

import collagene.restrictionEnzyme.RestrictionEnzyme;

/**
 * 
 * 
 * @author Johan Henriksson
 *
 */
public class ViewSettingsRestrictionEnzymes
	{
	public int numRestrictionSite=1;
	public boolean showNickEnzymes=true;
	public boolean showBluntSites=true;
	public boolean showStickySites=true;
	public int motifsize=-1;

	
	public boolean allowsRestrictionSiteCount(RestrictionEnzyme enz, int c)
		{
		if(motifsize!=-1)
			if(enz.getMotifSize()<motifsize)
				return false;
		
		
		boolean b = numRestrictionSite!=-1 && ((c>=1 && c<=numRestrictionSite) || numRestrictionSite==-2 || (c==0 && numRestrictionSite==0));
		if(b)
			{
			boolean a=showBluntSites  && enz.isBlunt();
			boolean d=showStickySites && !enz.isBlunt();
			boolean g=showNickEnzymes && enz.isNicking();
			return a || d || g;
			}
		else
			return false;
		}


	}
