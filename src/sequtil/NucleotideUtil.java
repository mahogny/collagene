package sequtil;

import java.util.Random;


public class NucleotideUtil
	{
	public static String revcomplement(String s)
		{
		return reverse(complement(s));
		}
	
	public static String complement(String s)
		{
		StringBuilder sb=new StringBuilder();
		for(char c:s.toCharArray())
			sb.append(complement(c));
		return sb.toString();
		}
	

	public static char complement(char c)
		{
		c=Character.toUpperCase(c); //Really do this?
		if(c=='A')
			return ('T');
		else if(c=='T')
			return ('A');
		else if(c=='C')
			return ('G');
		else if(c=='G')
			return ('C');
		
		else if(c=='N')
			return ('N');
		
		else if(c=='R')
			return ('Y');
		else if(c=='Y')
			return ('R'); 

		else if(c=='M')
			return ('K');
		else if(c=='K')
			return ('M'); 

		else if(c=='S')
			return ('W');
		else if(c=='W')
			return ('S'); 

		
		else if(c=='H')
			return ('D'); 
		else if(c=='D')
			return ('H'); 
		
		else if(c=='B')
			return ('V'); 
		else if(c=='V')
			return ('B'); 
		else if(c==' ')
			return (' '); 
		else
			return ('?');
		}

	
	public static String reverse(String s)
		{
		StringBuilder sb=new StringBuilder();
		for(int i=s.length()-1;i>=0;i--)
			sb.append(s.charAt(i));
		return sb.toString();
		}

	public static boolean isValidNucLetter(char c)
		{
		return "ATCG U N RY MK SW HD BV".indexOf(c)!= -1;
		}

	public static int countGC(String sub)
		{
		int c=0;
		for(int i=0;i<sub.length();i++)
			if(sub.charAt(i)=='C' || sub.charAt(i)=='G')
				c++;
		return c;
		}
	

	/**
	 * Get a random oligo, equal probability of each base
	 */
	public static String getRandomOligo(int n, Random rand)
		{
		char arr[]=new char[n];
		char atcg[]=new char[]{'A','T','C','G'};
		
		for(int i=0;i<n;i++)
			{
			int v=rand.nextInt(4);
			arr[i]=atcg[v];
			}
		return new String(arr);
		}
	
	
	public static void main(String[] args)
		{
		for(int i=0;i<5;i++)
			System.out.println(getRandomOligo(100, new Random()));
		}

	public static String getRepeatOligo(char c, int n)
		{
		char arr[]=new char[n];
		for(int i=0;i<n;i++)
			arr[i]=c;
		return new String(arr);
		}

	public static boolean isATGC(char c)
		{
		return c=='A' || c=='T' || c=='C' || c=='G';
		}

	public static String normalize(String s)
		{
		return s.toUpperCase().replace(" ", "").replace("\t", "").replace("\n", "");
		}

	public static boolean isSpacing(char c)
		{
		return c==' ' || c=='_';
		}

	public static boolean areComplementary(char letterUpper, char letterLower)
		{
		return letterUpper==complement(letterLower);
		}
	}
