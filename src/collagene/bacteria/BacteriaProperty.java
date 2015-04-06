package collagene.bacteria;

import java.util.ArrayList;

/**
 * 
 * Properties of a strain
 * 
 * @author Johan Henriksson
 *
 */
public class BacteriaProperty
	{
	public String name="DH5a";
	
	public boolean isDam=true;
	public boolean isDcm=true;
	public boolean isEcoKI=true;
	
	
	
	public static ArrayList<BacteriaProperty> strains=new ArrayList<BacteriaProperty>();
	
	public static void readFile()
		{
		//Should find a good source of strains, then decide on a format
		strains.add(new BacteriaProperty());
		}
	
	}
