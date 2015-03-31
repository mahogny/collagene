package collagene.restrictionEnzyme;

import collagene.seq.AnnotatedSequence;
import collagene.seq.RestrictionSite;

/**
 * 
 * One fragment of a sequence
 * 
 * @author Johan Henriksson
 *
 */
public interface SequenceFragment
	{
	public int getUpperLength();
	public int getUpperFrom();
	public int getUpperTo();
	public RestrictionSite getFromSite();
	public RestrictionSite getToSite();
	public AnnotatedSequence getFragmentSequence();
	}
