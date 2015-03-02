package alignment.emboss;

import alignment.AlignmentCostTable;

public class EmbossCost
	{

	public static AlignmentCostTable tableBlosum62;
	
	static
		{
		try
			{
			tableBlosum62=new AlignmentCostTable(EmbossCost.class.getResourceAsStream("EBLOSUM62"));
			}
		catch (Exception e)
			{
			e.printStackTrace();
			}
		}
	
	
	}
