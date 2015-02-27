package sequtil;


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
			{
			c=Character.toUpperCase(c);
			if(c=='A')
				sb.append('T');
			else if(c=='T')
				sb.append('A');
			else if(c=='C')
				sb.append('G');
			else if(c=='G')
				sb.append('C');
			
			else if(c=='N')
				sb.append('N');
			
			else if(c=='R')
				sb.append('Y');
			else if(c=='Y')
				sb.append('R'); 

			else if(c=='M')
				sb.append('K');
			else if(c=='K')
				sb.append('M'); 

			else if(c=='S')
				sb.append('W');
			else if(c=='W')
				sb.append('S'); 

			
			else if(c=='H')
				sb.append('D'); 
			else if(c=='D')
				sb.append('H'); 
			
			else if(c=='B')
				sb.append('V'); 
			else if(c=='V')
				sb.append('B'); 

			else
				sb.append('?');
//				throw new RuntimeException("Bad DNA letter: "+c);
			//TODO: might want to have a flag for this
			}
		return sb.toString();
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
	
	
	}
