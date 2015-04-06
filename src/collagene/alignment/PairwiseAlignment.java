package collagene.alignment;

import java.util.Collections;
import java.util.LinkedList;

import collagene.alignment.emboss.EmbossCost;
import collagene.sequtil.NucleotideUtil;

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
	public boolean isLocalAlignment=true;
	public boolean canGoOutside=false;
	public boolean includeAllA=false;
	private boolean printMatrix=false;
	
	public AlignmentCostTable costtable=new AlignmentCostTable();
	

	public double bestCost;
	
	private static byte TRAJ_LEFT=3, TRAJ_UP=1, TRAJ_MATCH=2, TRAJ_END=6;


	
	public String alignedSequenceA;
	public String alignedSequenceB;

	public LinkedList<Integer> alignedIndexA=new LinkedList<Integer>();
	public LinkedList<Integer> alignedIndexB=new LinkedList<Integer>();

	public char gapSymbol=' ';
	
	/**
	 * Compute the alignment
	 */
	public void align(String seqA, String seqB)
		{
		//Arrays are [a][b]
		double cost[][];
		int numskip[][];  // for extension costs
		byte traj[][];    // for simplicity, keep track of directions. strictly speaking not needed

		//Convert strings into indices
		int[] indA=costtable.indexOfChars(seqA);
		int[] indB=costtable.indexOfChars(seqB);
		
		//Fill in borders
		cost=new double[indA.length+1][indB.length+1];
		traj=new byte[indA.length+1][indB.length+1];
		numskip=new int[indA.length+1][indB.length+1];

		//TODO bug: numskip, depends on left/up. or?
		
		//Fill in main table upper/left border
		for(int i=0;i<seqA.length();i++)
			{
			numskip[i+1][0]=Math.max(0,i-1);
			if(!canGoOutside)
				cost[i+1][0]=costtable.penaltySkip+Math.max(0,i-1)*costtable.penaltyExtend;
			traj[i+1][0]=TRAJ_UP;
			}
		for(int i=0;i<seqB.length();i++)
			{
			numskip[0][i+1]=Math.max(0,i-1);
			if(!canGoOutside)
				cost[0][i+1]=costtable.penaltySkip+Math.max(0,i-1)*costtable.penaltyExtend;
			traj[0][i+1]=TRAJ_LEFT;
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
				double costFromUp    = (numskip[mati-1][matj]==0 ? costtable.penaltySkip : costtable.penaltyExtend) + cost[mati-1][matj];
				double costFromLeft  = (numskip[mati][matj-1]==0 ? costtable.penaltySkip : costtable.penaltyExtend) + cost[mati][matj-1];
				double costFromMatch = costtable.cost[indA[i]][indB[j]] + cost[mati-1][matj-1];
				if(canGoOutside)
					{
					if(i==seqA.length()-1)
						costFromLeft=cost[mati][matj-1];
					if(j==seqB.length()-1)
						costFromUp=cost[mati-1][matj];
					}
				
				
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
				if((isLocalAlignment) && cost[mati][matj]<0)
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
		if(!isLocalAlignment)
			{
			fromi=seqA.length();
			fromj=seqB.length();
			}
		for(int i=fromi;i<seqA.length()+1;i++)
			{
			for(int j=fromj;j<seqB.length()+1;j++)
				{
				if(cost[i][j]>bestcost)
					{
					bestcost=cost[i][j];
					besti=i;
					bestj=j;
					}
				}
			}
		
		//Print matrix if requested
		if(printMatrix)
			{
			System.out.println();
			System.out.print("\t\t");
			for(int j=0;j<seqB.length();j++)
				System.out.print(seqB.charAt(j)+"\t");
			System.out.println();
			for(int i=0;i<seqA.length()+1;i++)
				{
				if(i==0)
					System.out.print("\t");
				else
					System.out.print(seqA.charAt(i-1)+"\t");
				for(int j=0;j<seqB.length()+1;j++)
					System.out.print(trajToString(traj[i][j])+"\t");
				System.out.println();
				}
			System.out.println();
			System.out.print("\t\t");
			for(int j=0;j<seqB.length();j++)
				System.out.print(seqB.charAt(j)+"\t");
			System.out.println();
			for(int i=0;i<seqA.length()+1;i++)
				{
				if(i==0)
					System.out.print("\t");
				else
					System.out.print(seqA.charAt(i-1)+"\t");
				for(int j=0;j<seqB.length()+1;j++)
					System.out.print(cost[i][j]+"\t");
				System.out.println();
				}
			System.out.println("best cost "+bestcost+"  from  "+besti+"  "+bestj);
			}

		this.bestCost=bestcost;
		
		//Traverse back to find alignment
		StringBuilder sbA=new StringBuilder();
		StringBuilder sbB=new StringBuilder();
		alignedIndexA.clear();
		alignedIndexB.clear();

		int mati=besti;
		int matj=bestj;
		while(!(mati==0 && matj==0)) //correct?
			{
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
				sbB.append(gapSymbol);
				alignedIndexA.add(mati);
				alignedIndexB.add(-1);
				mati--;
				}
			else if(traj[mati][matj]==TRAJ_LEFT)
				{
				sbA.append(gapSymbol);
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

		//If the alignment is only partially local, add missing letters for the other sequence.
		//It went from (besti,bestj) to (mati,matj)
		if(includeAllA)
			{
			//Include the full A
			alignedSequenceA=seqA.substring(0,mati) + alignedSequenceA + seqA.substring(besti,seqA.length());
			alignedSequenceB=NucleotideUtil.getRepeatOligo(gapSymbol, mati) + alignedSequenceB + NucleotideUtil.getRepeatOligo(gapSymbol, seqA.length()-besti);
			for(int i=mati;i>=0;i--)
				{
				alignedIndexA.addFirst(i);
				alignedIndexB.addFirst(-1);
				}
			for(int i=besti;i<seqA.length();i++)
				{
				alignedIndexA.add(i);
				alignedIndexB.add(-1);
				}
			}
		
		}
	
	
	
	
	public static void main(String[] args)
		{
		
		PairwiseAlignment al=new PairwiseAlignment();
		al.costtable=EmbossCost.tableBlosum62;
		al.isLocalAlignment=false;
		al.canGoOutside=true;
		al.printMatrix=true;
//		al.align("aabccca", "abcccbbb");
//		al.align("attcccacct".toUpperCase(), "ttccccct".toUpperCase());

		//Problematic case for regular global alignment
		String seq1="AAGCCACTACCT";
		String seq2= "AGCCAC";
		System.out.println(seq1.length());
		System.out.println(seq2.length());
		seq1=NucleotideUtil.normalize(seq1);
		seq2=NucleotideUtil.normalize(seq2);
	
		al.align(seq1, seq2);

		System.out.println(al.alignedSequenceA);
		System.out.println(al.alignedSequenceB);
		
		System.out.println(al.alignedIndexA);
		System.out.println(al.alignedIndexB);

		}



	/**
	 * Get the length of the match
	 */
	public int getMatchLength()
		{
		return alignedSequenceA.length();
		}



	/**
	 * Get the start index of B
	 */
	public int getStartOfB()
		{
		return alignedIndexB.get(0);
		}

	/**
	 * Get the end index of B
	 */
	public int getEndOfB()
		{
		return alignedIndexB.get(alignedIndexB.size()-1);
		}




	/**
	 * Get the start index of A
	 */
	public int getStartOfA()
		{
		return alignedIndexA.get(0);
		}

	/**
	 * Get the end index of A
	 */
	public int getEndOfA()
		{
		return alignedIndexA.get(alignedIndexA.size()-1);
		}
	
	/**
	 * Format trajectory as string
	 */
	public String trajToString(int t)
		{
		if(t==TRAJ_LEFT)
			return "LEFT";
		else if(t==TRAJ_MATCH)
			return "MATCH";
		else if(t==TRAJ_UP)
			return "UP";
		else if(t==TRAJ_END)
			return "END";
		else
			return "N/A";
		}
	}
