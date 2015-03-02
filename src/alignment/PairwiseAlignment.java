package alignment;

import java.util.Collections;
import java.util.LinkedList;

import sequtil.NucleotideUtil;

/**
 * 
 * Pairwise sequence alignment
 * 
 * http://en.wikipedia.org/wiki/Needleman%E2%80%93Wunsch_algorithm
 * 
 * @author Johan Henriksson
 *
 */
public class PairwiseAlignment
	{
	public boolean isLocal=true;
	public AlignmentCostTable costtable=new AlignmentCostTable();
	
	public double penaltySkip   = -10;
	public double penaltyExtend = -0.5;  //taken from http://www.ebi.ac.uk/Tools/psa/emboss_needle/

	
	private static int TRAJ_LEFT=3, TRAJ_UP=1, TRAJ_MATCH=2, TRAJ_END=6;

	public String alignedSequenceA;
	public String alignedSequenceB;

	public LinkedList<Integer> alignedIndexA=new LinkedList<Integer>();
	public LinkedList<Integer> alignedIndexB=new LinkedList<Integer>();

	/**
	 * Compute the alignment
	 */
	public void align(String seqA, String seqB)
		{
		//Arrays are [a][b]
		double cost[][];
		int numskip[][];  // for extension costs
		int traj[][];     // for simplicity, keep track of directions. strictly speaking not needed

		//Convert strings into indices
		int[] indA=costtable.indexOfChars(seqA);
		int[] indB=costtable.indexOfChars(seqB);
		
		//Fill in borders
		cost=new double[indA.length][indB.length];
		traj=new int[indA.length][indB.length];
		numskip=new int[indA.length][indB.length];
		
		//Fill in main table
		for(int i=0;i<seqA.length();i++)
			for(int j=0;j<seqB.length();j++)
				{
				//Compute costs
				double costFromUp    = Integer.MIN_VALUE;
				double costFromLeft  = Integer.MIN_VALUE;
				double costFromMatch = costtable.cost[indA[i]][indB[i]];
				if(i>0 && j>0) 
					costFromMatch += cost[i-1][j-1];
				if(i>0)
					costFromUp    = (numskip[i-1][j]==0 ? penaltySkip : penaltyExtend) + cost[i-1][j];
				if(j>0)
					costFromLeft  = (numskip[i][j-1]==0 ? penaltySkip : penaltyExtend) + cost[i][j-1];

				//Pick the smallest cost
				if(costFromUp>costFromLeft)
					{
					if(costFromUp>costFromMatch)
						{
						traj[i][j]=TRAJ_UP;
						cost[i][j]=costFromUp;
						numskip[i][j]=1+numskip[i-1][j];
						}
					else
						{
						traj[i][j]=TRAJ_MATCH;
						cost[i][j]=costFromMatch;
						numskip[i][j]=0;
						}
					}
				else
					{
					if(costFromLeft>costFromMatch)
						{
						traj[i][j]=TRAJ_LEFT;
						cost[i][j]=costFromLeft;
						numskip[i][j]=1+numskip[i][j-1];
						}
					else
						{
						traj[i][j]=TRAJ_MATCH;
						cost[i][j]=costFromMatch;
						numskip[i][j]=0;
						}
					}
				
				//The restart condition for local alignment
				if(isLocal && cost[i][j]<0)
					{
					traj[i][j]=TRAJ_END;
					cost[i][j]=0;
					}
				}
		

		//Find optimal end point
		double bestcost=-1;
		int besti=0, bestj=0;
		int fromi=0;
		int fromj=0;
		if(!isLocal)
			{
			fromi=seqA.length()-1;
			fromj=seqB.length()-1;
			}
		for(int i=fromi;i<seqA.length();i++)
			{
			for(int j=fromj;j<seqB.length();j++)
				{
				System.out.print(cost[i][j]+"\t");
				if(cost[i][j]>bestcost)
					{
					bestcost=cost[i][j];
					besti=i;
					bestj=j;
					}
				}
			System.out.println();
			}
			
		//Traverse back to find alignment
		StringBuilder sbA=new StringBuilder();
		StringBuilder sbB=new StringBuilder();
		alignedIndexA.clear();
		alignedIndexB.clear();

		int curI=besti;
		int curJ=bestj;
		while(curI>=0 && curJ>=0) //correct?
			{
			if(traj[curI][curJ]==TRAJ_MATCH)
				{
				sbA.append(seqA.charAt(curI));
				sbB.append(seqB.charAt(curJ));
				alignedIndexA.add(curI);
				alignedIndexB.add(curJ);
				curI--;
				curJ--;
				}
			else if(traj[curI][curJ]==TRAJ_UP)
				{
				sbA.append(seqA.charAt(curI));
				sbB.append('_');
				alignedIndexA.add(curI);
				alignedIndexB.add(-1);
				curI--;
				}
			else if(traj[curI][curJ]==TRAJ_LEFT)
				{
				sbA.append('_');
				sbB.append(seqB.charAt(curJ));
				alignedIndexA.add(-1);
				alignedIndexB.add(curJ);
				curJ--;
				}
			else if(traj[curI][curJ]==TRAJ_END)
				break;
			else
				throw new RuntimeException("Unknown traj code "+traj[curI][curJ]);
			}
		
		alignedSequenceA=NucleotideUtil.reverse(sbA.toString());
		alignedSequenceB=NucleotideUtil.reverse(sbB.toString());
		
		Collections.reverse(alignedIndexA);
		Collections.reverse(alignedIndexB);
		}
	
	
	
	
	public static void main(String[] args)
		{
		
		PairwiseAlignment al=new PairwiseAlignment();
		
//		al.align("aabccca", "abcccbbb");
		al.align("abbcccaccb", "bbcccccb");

		System.out.println(al.alignedSequenceA);
		System.out.println(al.alignedSequenceB);
		
		System.out.println(al.alignedIndexA);
		System.out.println(al.alignedIndexB);

		}




	public int matchLength()
		{
		return alignedSequenceA.length();
		}




	public int startOfB()
		{
		return alignedIndexB.get(0);
		}

	public int endOfB()
		{
		return alignedIndexB.get(alignedIndexB.size()-1);
		}




	public int startOfA()
		{
		return alignedIndexA.get(0);
		}

	public int endOfA()
		{
		return alignedIndexA.get(alignedIndexA.size()-1);
		}
	}
