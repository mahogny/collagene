package restrictionEnzyme;

import seq.AnnotatedSequence;
import seq.RestrictionSite;

public interface SequenceFragment
	{
	public int getUpperLength();
	public int getUpperFrom();
	public int getUpperTo();
	public RestrictionSite getFromSite();
	public RestrictionSite getToSite();
	public AnnotatedSequence getFragmentSequence();
	}
