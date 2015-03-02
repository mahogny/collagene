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
		cost=new double[indA.length+1][indB.length+1];
		traj=new int[indA.length+1][indB.length+1];
		numskip=new int[indA.length+1][indB.length+1];

		//TODO bug: numskip, depends on left/up. or?
		
		//Fill in main table upper/left border
		for(int i=0;i<seqA.length();i++)
			{
			cost[i+1][0]=penaltySkip+Math.max(0,i-1)*penaltyExtend;
			traj[i+1][0]=TRAJ_UP;
			}
		for(int i=0;i<seqB.length();i++)
			{
			cost[0][i+1]=penaltySkip+Math.max(0,i-1)*penaltyExtend;
			traj[0][i+1]=TRAJ_UP;
			}
		cost[0][0]=0;
		traj[0][0]=TRAJ_END;
		
		//Fill in main table body
		for(int i=0;i<seqA.length();i++)
			for(int j=0;j<seqB.length();j++)
				{
				int mati=i+1;
				int matj=j+1;
				
				//Compute costs
				double costFromUp    = (numskip[mati-1][matj]==0 ? penaltySkip : penaltyExtend) + cost[mati-1][matj];
				double costFromLeft  = (numskip[mati][matj-1]==0 ? penaltySkip : penaltyExtend) + cost[mati][matj-1];
				double costFromMatch = costtable.cost[indA[i]][indB[j]] + cost[mati-1][matj-1];

				//Pick the smallest cost
				if(costFromUp>costFromLeft)
					{
					if(costFromUp>costFromMatch)
						{
						traj[mati][matj]=TRAJ_UP;
						cost[mati][matj]=costFromUp;
						numskip[mati][matj]=1+numskip[mati-1][matj];
						}
					else
						{
						traj[mati][matj]=TRAJ_MATCH;
						cost[mati][matj]=costFromMatch;
						numskip[mati][matj]=0;
						}
					}
				else
					{
					if(costFromLeft>costFromMatch)
						{
						traj[mati][matj]=TRAJ_LEFT;
						cost[mati][matj]=costFromLeft;
						numskip[mati][matj]=1+numskip[mati][matj-1];
						}
					else
						{
						traj[mati][matj]=TRAJ_MATCH;
						cost[mati][matj]=costFromMatch;
						numskip[mati][matj]=0;
						}
					}
				
				//The restart condition for local alignment
				if(isLocal && cost[mati][matj]<0)
					{
					traj[mati][matj]=TRAJ_END;
					cost[mati][matj]=0;
					}
				}
		

		//Find optimal end point
		double bestcost=Integer.MIN_VALUE;
		int besti=0, bestj=0;
		int fromi=0;
		int fromj=0;
		if(!isLocal)
			{
			fromi=seqA.length();
			fromj=seqB.length();
			}
		for(int i=fromi;i<seqA.length()+1;i++)
			{
			for(int j=fromj;j<seqB.length()+1;j++)
				{
//				System.out.print("@ "+i+","+j+"   "+cost[i][j]+"\t");
//				System.out.print(cost[i][j]+"\t");
				if(cost[i][j]>bestcost)
					{
					bestcost=cost[i][j];
					besti=i;
					bestj=j;
					}
				}
			System.out.println();
			}
		System.out.println("best cost "+bestcost);
		
		//Traverse back to find alignment
		StringBuilder sbA=new StringBuilder();
		StringBuilder sbB=new StringBuilder();
		alignedIndexA.clear();
		alignedIndexB.clear();

		int mati=besti;
		int matj=bestj;
//		System.out.println("traj "+mati+"   "+matj+"   "+traj[mati][matj]);
		while(!(mati==0 && matj==0)) //correct?
			{
	//		System.out.println("traj "+mati+"   "+matj+"   "+traj[mati][matj]);
			if(traj[mati][matj]==TRAJ_MATCH)
				{
				sbA.append(seqA.charAt(mati-1));
				sbB.append(seqB.charAt(matj-1));
				alignedIndexA.add(mati);
				alignedIndexB.add(matj);
				mati--;
				matj--;
				}
			else if(traj[mati][matj]==TRAJ_UP)
				{
				sbA.append(seqA.charAt(mati-1));
				sbB.append('_');
				alignedIndexA.add(mati);
				alignedIndexB.add(-1);
				mati--;
				}
			else if(traj[mati][matj]==TRAJ_LEFT)
				{
				sbA.append('_');
				sbB.append(seqB.charAt(matj-1));
				alignedIndexA.add(-1);
				alignedIndexB.add(matj);
				matj--;
				}
			else if(traj[mati][matj]==TRAJ_END)
				break;
			else
				throw new RuntimeException("Unknown traj code "+traj[mati][matj]);
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
		al.align("attcccacct".toUpperCase(), "ttccccct".toUpperCase());

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
