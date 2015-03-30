package collagene.restrictionEnzyme;

import collagene.seq.AnnotatedSequence;
import collagene.seq.RestrictionSite;

public class SequenceFragmentPCR implements SequenceFragment
	{
	public AnnotatedSequence newseq;
	
	@Override
	public int getUpperLength()
		{
		return newseq.getLength();
		}

	@Override
	public int getUpperFrom()
		{
		return 0;
		}

	@Override
	public int getUpperTo()
		{
		return newseq.getLength();
		}

	@Override
	public RestrictionSite getFromSite()
		{
		return null;
		}

	@Override
	public RestrictionSite getToSite()
		{
		return null;
		}

	@Override
	public AnnotatedSequence getFragmentSequence()
		{
		return newseq;
		}

	}
