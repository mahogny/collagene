package seq;

import java.util.HashMap;
import java.util.LinkedList;

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
	
	public AnnotatedSequence()
		{
		setSequence("atcgacacacacaaaaaggacccgggaattatataaatta");
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
		if(sequenceUpper.length()!=sequenceUpper.length())
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

	public String getSubsequence(SequenceRange range)
		{
		range=range.toNormalizedRange(this);
		if(range.from<=range.to && range.to<getLength())
			return sequenceUpper.substring(range.from, range.to);
		else
			return sequenceUpper.substring(range.from) + sequenceUpper.substring(0,range.to);
		}

	public String getSubsequenceLower(SequenceRange range)
		{
		range=range.toNormalizedRange(this);
		if(range.from<=range.to && range.to<getLength())
			return sequenceLower.substring(range.from, range.to);
		else
			return sequenceLower.substring(range.from) + sequenceLower.substring(0,range.to);
		}
	
	
	
	}
