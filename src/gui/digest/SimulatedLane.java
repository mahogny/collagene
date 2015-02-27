package gui.digest;

import java.util.TreeMap;

import ladder.DNALadder;

/**
 * 
 * 
 * @author Johan Henriksson
 *
 */
public class SimulatedLane
	{
	public SimulatedLane()
		{
		
		}
	public SimulatedLane(DNALadder dnaLadder)
		{
		mapPosWeight.putAll(dnaLadder.sizes);
		}

	public TreeMap<Double, Double> mapPosWeight=new TreeMap<Double, Double>();
	}
