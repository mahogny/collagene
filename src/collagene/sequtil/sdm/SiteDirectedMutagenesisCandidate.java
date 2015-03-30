package collagene.sequtil.sdm;

import collagene.primer.Primer;
import collagene.seq.AnnotatedSequence;

/**
 * 
 * 
 * @author Johan Henriksson
 *
 */
public class SiteDirectedMutagenesisCandidate
	{
	public Primer fwd, rev;
	public double tm;
	public AnnotatedSequence newseq;
	}