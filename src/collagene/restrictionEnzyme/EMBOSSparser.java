package collagene.restrictionEnzyme;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * 
 * @author Johan Henriksson
 *
 */
public class EMBOSSparser
	{
	/**
	 * Load enzymes from an EMBOSS file
	 */
	public static void loadEmboss(File f, RestrictionEnzymeSet db) throws IOException
		{
		BufferedReader r=new BufferedReader(new FileReader(f));

		String line;
		while((line=r.readLine())!=null)
			if(!line.startsWith("#"))
				{
				RestrictionEnzyme e=new RestrictionEnzyme();
				
				StringTokenizer stok=new StringTokenizer(line,"\t");
				e.name=stok.nextToken();
				e.sequence=stok.nextToken();
				stok.nextToken(); //length of pattern
				int ncut=Integer.parseInt(stok.nextToken()); //0 represents unknown
				
				RestrictionEnzymeCut cut=new RestrictionEnzymeCut();
				cut.upper=Integer.parseInt(stok.nextToken());
				cut.lower=Integer.parseInt(stok.nextToken());
				e.cuts.add(cut);
				
				if(ncut==2)
					{
					cut=new RestrictionEnzymeCut();
					cut.upper=Integer.parseInt(stok.nextToken());
					cut.lower=Integer.parseInt(stok.nextToken());
					e.cuts.add(cut);
					}
				
				
				db.enzymes.add(e);
				}
		
		System.out.println("# enzymes load: "+db.enzymes.size());
		
		r.close();
		}
	
	public static void main(String[] args)
		{
		
		try
			{
			RestrictionEnzymeSet e=new RestrictionEnzymeSet();
			loadEmboss(new File("bin/emboss_e.502"),e);
			}
		catch (IOException e)
			{
			e.printStackTrace();
			}
		}



	}
