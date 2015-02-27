package ladder;

import java.io.IOException;
import java.util.LinkedList;

/**
 * 
 * Set of ladders
 * 
 * @author Johan Henriksson
 *
 */
public class DNALadderSet
	{
	public LinkedList<DNALadder> ladders=new LinkedList<DNALadder>();

	
	public void load() throws IOException
		{
		add(DNALadder.parse("bioline_hyperladder.txt"));
		add(DNALadder.parse("invitrogen_1kbplus.txt"));
		add(DNALadder.parse("neb_100bp.txt"));
		add(DNALadder.parse("neb_2log.txt"));
		}
	
	public void add(DNALadder ladder)
		{
		ladders.add(ladder);
		}

	public DNALadder get(int i)
		{
		return ladders.get(i);
		}
	}
