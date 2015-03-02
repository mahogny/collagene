package restrictionEnzyme;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seq.AnnotatedSequence;
import seq.RestrictionSite;
import sequtil.NucleotideUtil;

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
	public Collection<RestrictionSite> findRestrictionSites(AnnotatedSequence sequence)
		{
		List<RestrictionSite> list=findRestrictionSitesOneway(sequence.getSequence());
		List<RestrictionSite> list2=findRestrictionSitesOneway(NucleotideUtil.revcomplement(sequence.getSequence()));
		
		if(sequence.isCircular)
			{
			//TODO handle circular sequences. Easiest way is to pad sequence with itself, 30bp or so. then exclude overlaps
			}
		
		//Transform cut-site for lower strand into upper strand
		for(RestrictionSite s:list2)
			{
			if(s.cuttingUpperPos!=null)
				s.cuttingUpperPos=sequence.getLength()-s.cuttingUpperPos;
			if(s.cuttingLowerPos!=null)
				s.cuttingLowerPos=sequence.getLength()-s.cuttingLowerPos;
			Integer temp=s.cuttingLowerPos;
			s.cuttingLowerPos=s.cuttingUpperPos;
			s.cuttingUpperPos=temp;
			}
		
		//Use a hashset to omit those found twice - in particular symmetric palindromic cutters
		HashSet<RestrictionSite> sites=new HashSet<RestrictionSite>();
		sites.addAll(list);
		sites.addAll(list2);
		
//		list.addAll(list2);
		return sites;
		}

	private List<RestrictionSite> findRestrictionSitesOneway(String sequence)
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
			while(m.find())
				{
				for(RestrictionEnzymeCut cut:e.cuts)
					{
					RestrictionSite site=new RestrictionSite();
					site.enzyme=e;
					site.cuttingUpperPos=cut.upper+m.start();
					site.cuttingLowerPos=cut.lower+m.start();
					
					if(Math.min(site.cuttingUpperPos,site.cuttingLowerPos)<=sequence.length() &&
							Math.max(site.cuttingUpperPos,site.cuttingLowerPos)>=0)  //This test is very blunt
						list.add(site);
					}
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
