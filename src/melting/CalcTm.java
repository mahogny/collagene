package melting;

/**
 * General interface to Tm calculation
 * 
 * @author Johan Henriksson
 *
 */
public interface CalcTm
	{
	public double calcTm(String seq1, String seq2) throws TmException;
	}
