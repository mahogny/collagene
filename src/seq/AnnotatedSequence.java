package seq;

import java.util.HashMap;
import java.util.LinkedList;

import primer.Primer;
import restrictionEnzyme.RestrictionEnzyme;
import sequtil.NucleotideUtil;

/**
 * A sequence with annotation
 * 
 * @author Johan Henriksson
 *
 */
public class AnnotatedSequence
	{
	private String sequenceUpper;
	private String sequenceLower;
	public String name="";
	public String notes="";
	public boolean isCircular;
	
	public LinkedList<SeqAnnotation> annotations=new LinkedList<SeqAnnotation>();	
	public HashMap<RestrictionEnzyme,LinkedList<RestrictionSite>> restrictionSites=new HashMap<RestrictionEnzyme, LinkedList<RestrictionSite>>();
	public LinkedList<Primer> primers=new LinkedList<Primer>();
	
	
	/**
	 * some things from benchling:
	 * * related work
	 * * publications
	 * * citations
	 */
	
	
	

	//how to handle a sequence that is not matching up? or even bound in order? or have different length?
	//best is really an alignment that can be of any shape. but then need a simplified alignment for most rendering
	
	//three views: circular, space-shaped and linear. the circular can maybe share code with the arbitrary shape
	
	
	//this would call for a SequenceLayout
	
	/**
	 * Create an empty sequence (with testing content)
	 */
	public AnnotatedSequence()
		{
		setSequence("atcgacacacacaaaaaggacccgggaattatataaatta".toUpperCase());
		}
	
	/**
	 * Copy a sequence
	 */
	public AnnotatedSequence(AnnotatedSequence seq)
		{
		sequenceUpper=seq.sequenceUpper;
		sequenceLower=seq.sequenceLower;
		name=seq.name;
		notes=seq.notes;
		isCircular=seq.isCircular;
		for(SeqAnnotation annot:seq.annotations)
			addAnnotation(new SeqAnnotation(annot));
		for(RestrictionEnzyme enz:seq.restrictionSites.keySet())
			for(RestrictionSite site:seq.restrictionSites.get(enz))
				addRestrictionSite(new RestrictionSite(site));
		for(Primer p:primers)
			addPrimer(new Primer(p));
		}

	public void setSequence(String upper)
		{
		sequenceUpper=upper.toUpperCase();
		sequenceLower=NucleotideUtil.complement(sequenceUpper);
		}
	public void setSequence(String upper, String lower)
		{
		sequenceUpper=upper.toUpperCase();
		sequenceLower=lower.toUpperCase();
		if(sequenceUpper.length()!=sequenceLower.length())
			throw new RuntimeException("upper and lower sequence not the same length");
		}

	public void fillcrap()
		{
		SeqAnnotation annot=new SeqAnnotation();
		annot.from=0;
		annot.to=50;
		annot.name="bleh";
		annotations.add(annot);

		/*
		RestrictionEnzyme enz=new RestrictionEnzyme();
		enz.name="aea";
		RestrictionSite r=new RestrictionSite();
		r.enzyme=enz;
		r.cuttingUpperPos=20;
		r.cuttingLowerPos=25;
		restrictionSites.add(r);
*/
//		FindRestrictionEnzyme f=new FindRestrictionEnzyme().findEnzymes(sequence)

		}
	
	public int getLength()
		{
		return sequenceUpper.length();
		}
	
	

	
	public void addRestrictionSite(RestrictionSite s)
		{
		LinkedList<RestrictionSite> list=restrictionSites.get(s.enzyme);
		if(list==null)
			restrictionSites.put(s.enzyme, list=new LinkedList<RestrictionSite>());
		list.add(s);
		}

	public void addAnnotation(SeqAnnotation annot)
		{
		annotations.add(annot);
		}

	public String getSequence()
		{
		return sequenceUpper;
		}
	public String getSequenceLower()
		{
		return sequenceLower;
		}

	public String getSequence(SequenceRange range)
		{
		range=range.toNormalizedRange(this);
		if(range.from<=range.to && range.to<getLength())
			return sequenceUpper.substring(range.from, range.to);
		else
			return sequenceUpper.substring(range.from) + sequenceUpper.substring(0,range.to);
		}

	public String getSequenceLower(SequenceRange range)
		{
		range=range.toNormalizedRange(this);
		if(range.from<=range.to && range.to<getLength())
			return sequenceLower.substring(range.from, range.to);
		else
			return sequenceLower.substring(range.from) + sequenceLower.substring(0,range.to);
		}

	public void addPrimer(Primer primer)
		{
		primers.add(primer);
		}

	public LinkedList<RestrictionSite> getRestrictionSitesFor(RestrictionEnzyme curenz)
		{
		return restrictionSites.get(curenz);
		}
	

	/**
	 * Move the 0-point on the plasmid
	 */
	public void setNew0(int pos)
		{
		System.out.println(pos);
		String upr=getSequence();
		String lower=getSequenceLower();
		upr=upr.substring(pos)+upr.substring(0,pos);
		lower=lower.substring(pos)+lower.substring(0,pos);
		setSequence(upr,lower);
		
		for(SeqAnnotation ann:annotations)
			ann.setRange(new SequenceRange(ann.from-pos, ann.to-pos).toNormalizedRange(this));
		for(Primer p:primers)
			p.setNew0(pos, this);
		for(RestrictionEnzyme enz:restrictionSites.keySet())
			for(RestrictionSite site:restrictionSites.get(enz))
				site.setNew0(pos, this);
		}

	/**
	 * Ensure the position is within the range
	 */
	public int normalizePos(int i)
		{
		while(i<=0)
			i+=getLength();
		while(i>getLength())
			i-=getLength();
		return i;
		}

	public void reverseSequence()
		{
		String upper=getSequence();
		String lower=getSequenceLower();
		
		setSequence(
				NucleotideUtil.reverse(lower),
				NucleotideUtil.reverse(upper));
		for(SeqAnnotation a:annotations)
			{
			int to=getLength()-a.from;
			int from=getLength()-a.to;
			a.to=to;
			a.from=from;
			
			if(a.orientation==Orientation.FORWARD)
				a.orientation=Orientation.REVERSE;
			else if(a.orientation==Orientation.REVERSE)
				a.orientation=Orientation.FORWARD;
			}
		restrictionSites.clear(); //TODO
		}
	
	
	@Override
	public String toString()
		{
		return sequenceUpper+"\n"+sequenceLower;
		}
	}
