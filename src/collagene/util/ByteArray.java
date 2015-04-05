package collagene.util;

/**
 * 
 * 
 * @author Johan Henriksson
 *
 */
public class ByteArray
	{
	private byte arr[]=new byte[100];
	private int len=0;
	
	public ByteArray()
		{
		}
	
	public ByteArray(int capacity)
		{
		arr=new byte[capacity];
		}
	
	public void add(byte value)
		{
		if(len==arr.length)
			{
			byte newarr[]=new byte[arr.length*2];
			System.arraycopy(arr, 0, newarr, 0, arr.length);
			arr=newarr;
			}
		arr[len]=value;
		len++;
		}

	public int size()
		{
		return len;
		}

	public int get(int i)
		{
		return arr[i];
		}

	public void add(byte[] b, int len)
		{
		//can be made much faster
		for(int i=0;i<len;i++)
			add(b[i]);
		}

	public byte[] getArray()
		{
		return arr;
		}
	
	

	}
