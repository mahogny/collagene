package collagene.bacteria;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;

/**
 * 
 * Properties of a strain, or any source of DNA
 * 
 * @author Johan Henriksson
 *
 */
public class BacteriaProperty
	{
	/**
	 * Proper capitalization of genotypes; lower-case => proper case
	 */
	private static HashMap<String, String> properCapitalization=new HashMap<String, String>();
	
	/**
	 * List of strains
	 */
	public static ArrayList<BacteriaProperty> strains=new ArrayList<BacteriaProperty>();

	/**
	 * Name of strain
	 */
	public String name="";
	
	/**
	 * List of genotypes
	 */
	public HashSet<String> genotype=new HashSet<String>();
	
	
	/**
	 * Read bacterias from stream
	 */
	public static void readStream(BufferedReader r) throws IOException
		{
		BacteriaProperty def=new BacteriaProperty();
		String line;
		String prefix="";
		while((line=r.readLine())!=null)
			{
			if(line.startsWith("#"))
				continue;
			StringTokenizer stok=new StringTokenizer(line,"\t");
			if(stok.hasMoreElements())
				{
				String name=stok.nextToken();
				if(name.equals("prefix"))
					prefix=stok.nextToken()+" ";
				else
					{
					name=prefix+name;
					BacteriaProperty prop=new BacteriaProperty();
					prop.name=name.trim();
					prop.genotype.addAll(def.genotype);
					
					if(stok.hasMoreTokens())
						{
						StringTokenizer ptok=new StringTokenizer(stok.nextToken().trim()," ");
						while(ptok.hasMoreElements())
							{
							String e=ptok.nextToken();
							prop.set(e,true);
							}
						if(prop.name.equals("default"))
							def=prop;
						else
							strains.add(prop);
						}
					}
				}
			}
		}
	
	/**
	 * Check if Dam+
	 */
	public boolean isDam()
		{
		return genotype.contains("Dam");
		}
	
	/**
	 * Check if Dcm+
	 */
	public boolean isDcm()
		{
		return genotype.contains("Dcm");
		}
	
	/**
	 * Check if EcoKI+
	 */
	public boolean isEcoKI()
		{
		return genotype.contains("EcoKI");
		}
	
	/**
	 * Check if CpG+
	 */
	public boolean isCpG()
		{
		return genotype.contains("CpG");
		}

	/**
	 * Set a genotype
	 */
	public void set(String prop, boolean checked)
		{
		prop=getProperCapitalization(prop);
		if(checked)
			genotype.add(prop);
		else
			genotype.remove(prop);
		}
	
	/**
	 * Get the proper capitalization of a name
	 */
	private static String getProperCapitalization(String s)
		{
		String s2=properCapitalization.get(s.toLowerCase());
		if(s2!=null)
			return s2;
		else
			return s;
		}
	
	
	/**
	 * Read from resource
	 */
	private static void readResource(String s) throws IOException
		{
		InputStream is=BacteriaProperty.class.getResourceAsStream(s);
		readStream(new BufferedReader(new InputStreamReader(is)));
		is.close();
		}
	
	
	/**
	 * Add a proper capitalization of a name
	 */
	private static void addProperName(String s)
		{
		properCapitalization.put(s.toLowerCase(), s);
		}
	
	/**
	 * Initiate table of properties
	 */
	static
		{
		addProperName("EcoKI");
		addProperName("Dam");
		addProperName("Dcm");
		addProperName("CpG");
		try
			{
			readResource("strains.txt");
			readResource("strainsInvitrogen.txt");
			}
		catch (IOException e)
			{
			e.printStackTrace();
			}
		}
	}
