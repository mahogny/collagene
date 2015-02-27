package restrictionEnzyme;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seq.RestrictionSite;

/**
 * 
 * List of restriction enzymes
 * 
 * @author mahogny
 *
 */
public class RestrictionEnzymeSet
	{
	public List<RestrictionEnzyme> enzymes=new LinkedList<RestrictionEnzyme>();

	/**
	 * Find restriction sites
	 */
	public List<RestrictionSite> findRestrictionSites(String sequence)
		{
		LinkedList<RestrictionSite> list=new LinkedList<RestrictionSite>();
		sequence=sequence.toUpperCase();
		
		
		//To make the search fast, one should really build a trie structure first.
		//A dirty version is to build a regexp, find hits, then figure out what they are

		//java pattern:     p1|p2|p3    with N=[ATCG] etc

		//This is a lazy version of speeding up search: Build a regular expression for each enzyme. Then scan through.
		//Java has capture groups, http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html#cg
		for(RestrictionEnzyme e:enzymes)
			{
			Pattern pa=Pattern.compile(e.getRegexp());
			Matcher m=pa.matcher(sequence);
	//		int p=0;
			while(m.find())
				{
				for(RestrictionEnzymeCut cut:e.cuts)
					{
					RestrictionSite site=new RestrictionSite();
					site.enzyme=e;
					site.cuttingUpperPos=cut.upper+m.start();
					site.cuttingLowerPos=cut.lower+m.start();
					
					list.add(site);
					//TODO handle multiple cut sites
					}
//				System.out.println(e+" "+p+" "+m.);
//				p=m.regionStart()+1;
				}
			}
		return list;
		}
		
	
	/**
	 * Return a subset of enzymes matching the given names
	 */
	public RestrictionEnzymeSet subset(Collection<String> nezy)
		{
		RestrictionEnzymeSet d=new RestrictionEnzymeSet();
		for(RestrictionEnzyme e:enzymes)
			if(nezy.contains(e.name))
				d.enzymes.add(e);
		return d;
		}

	/**
	 * Add an enzyme
	 */
	public void addEnzyme(RestrictionEnzyme enz)
		{
		enzymes.add(enz);
		}

	/**
	 * Remove an enzyme
	 */
	public void removeEnzyme(RestrictionEnzyme e)
		{
		enzymes.remove(e);
		}
	
	}
