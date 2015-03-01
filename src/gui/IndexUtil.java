package gui;


/**
 * 
 * Utility to convert ranges to user visible ones
 * 
 * @author Johan Henriksson
 *
 */
public class IndexUtil
	{
	public static boolean showInternal=false;
	
	private static int fromshift()
		{
		if(showInternal)
			return 0;
		else
			return 1;
		}
	
	
	public static int fromTogui(int index)
		{
		return index+fromshift();
		}
	public static int fromTointernal(int index)
		{
		return index-fromshift();
		}
	
	public static int toTogui(int index)
		{
		return index;
		}
	public static int toTointernal(int index)
		{
		return index;
		}
	}
