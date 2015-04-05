package collagene.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 
 * Random accessible array in memory
 * 
 * @author Johan Henriksson
 *
 */
public class RandomAccessibleArray implements RandomAccessible
	{
	private byte[] fileContentArray;
	private int currentPos=0;
	private int fileSize=0;
	
	
	public void ensureArraySize(int size)
		{
		if(size>fileContentArray.length)
			{
			//This ensures O(n) amortized time at the cost of memory usage
			if(fileContentArray.length*2>size)
				size=fileContentArray.length*2;
			
			byte[] newbuf=new byte[size];
			System.arraycopy(fileContentArray, 0, newbuf, 0, fileSize);
			fileContentArray=newbuf;
			}
		}
	
	
	public RandomAccessibleArray()
		{
		this(1024);
		}
	
	public RandomAccessibleArray(int allocateSize)
		{
		fileContentArray=new byte[allocateSize];
		fileSize=0;
		}
	
	public RandomAccessibleArray(byte[] arr) 
		{
		this.fileContentArray=arr;
		fileSize=arr.length;
		}




	public void write(byte[] arr) throws IOException
		{
		write(arr,0,arr.length);
		}


	public void write(byte[] buf, int off, int len) throws IOException
		{
		ensureArraySize(currentPos+len+16);
		for(int i=0;i<len;i++)
			{
			fileContentArray[currentPos]=buf[i+off];   //Hope this is right?
			currentPos++;
			}
		extendEndOfFile();
		}


	public void writeByte(int value) throws IOException
		{
		ensureArraySize(currentPos+16);
		fileContentArray[currentPos]=(byte)value;   //Hope this is right?
		currentPos++;
		extendEndOfFile();
		}
	
	/**
	 * Ensure that the file size is at least
	 */
	private void extendEndOfFile()
		{
		if(currentPos>fileSize)
			fileSize=currentPos;
		}


	public void writeLong(long value) throws IOException
		{
		new DataOutputStream(getOutputStream()).writeLong(value);
		}


	public void writeInt(int value) throws IOException
		{
		new DataOutputStream(getOutputStream()).writeInt(value);
		}


	public int read() throws IOException
		{
		//System.out.println("read1 "+currentPos+" "+length()+" "+fileContentArray.length);
		if(currentPos>=length())
			return -1;
		else
			{
			int b=fileContentArray[currentPos] & 0xFF;
			currentPos++;
			//System.out.println("---- pos "+currentPos+" of "+length());
			return b;
			}
		}


	public int read(byte[] b, int off, int len) throws IOException
		{
		//System.out.println("---- pos "+currentPos+" of "+length()+" reading "+len);
		
		if(len==0)
			return 0;
		else if(currentPos==length()-1)
			return -1;
		else
			{
			int lastPos=currentPos+len;
			if(lastPos>=length())
				len=(int)length()-currentPos;
			for(int i=0;i<len;i++)
				{
				b[i+off]=fileContentArray[currentPos];
				currentPos++;
				}
			return len;
			}
		}


	public int read(byte[] buf) throws IOException
		{
		return read(buf, 0, buf.length);
		}


	public int readByte() throws IOException
		{
		return read();
		}


	public long readLong() throws IOException
		{
		return new DataInputStream(getInputStream()).readLong();
		}


	public int readInt() throws IOException
		{
		return new DataInputStream(getInputStream()).readInt();
		}


	/**
  	* Sets the file-pointer offset, measured from the beginning of this file, at which the next read or write occurs. 
  	* The offset may be set beyond the end of the file. Setting the offset beyond the end of the file does not change 
  	* the file length. The file length will change only by writing after the offset has been set beyond the end of the file. 	 
  	*/
	public void seek(long pos) throws IOException
		{
		currentPos=(int)pos;
		}

	
	public long skipBytes(long n) throws IOException
		{
		int left=(int)length()-currentPos-1;
		if(left>n)
			n=left;
		currentPos+=n;
		return n;
		}
		


	public long getFilePointer() throws IOException
		{
		return currentPos;
		}


	public long length() throws IOException
		{
		return fileSize;
		}


	/**
	 * This will drop the underlying array for GC
	 */
	public void close() throws IOException
		{
		fileContentArray=null;
		}


	/**
	 * This is a null operation for this class
	 */
	public void flush() throws IOException
		{
		}


	/**
	 * This is a null operation for this class
	 */
	public void sync() throws IOException
		{
		}


	public OutputStream getOutputStream()
		{
		return new OutputStream()
			{
			
			@Override
			public void flush() throws IOException
				{
				RandomAccessibleArray.this.flush();
				}

			@Override
			public void write(byte[] b, int off, int len) throws IOException
				{
				RandomAccessibleArray.this.write(b,off,len);
				}

			@Override
			public void write(int b) throws IOException
				{
				RandomAccessibleArray.this.writeByte(b);
				}
			};
		}

	/**
	 * This could be externalized
	 */
	public InputStream getInputStream()
		{
		return new InputStream()
			{
			@Override
			public long skip(long n) throws IOException
				{
				//System.out.println("is skip");
				return RandomAccessibleArray.this.skipBytes(n);
				}

			@Override
			public int read(byte[] b, int off, int len) throws IOException
				{
				//System.out.println("is read arr "+len);
				return RandomAccessibleArray.this.read(b,off,len);
				}

			@Override
			public int read(byte[] b) throws IOException
				{
				//System.out.println("is read arr2 "+b.length);
				return RandomAccessibleArray.this.read(b);
				}

			@Override
			public int read() throws IOException
				{
				//System.out.println("is read");
				return RandomAccessibleArray.this.read();
				}
			};
		}


	
	
	public static void main(String[] args)
		{
		try
			{
			RandomAccessibleArray f=new RandomAccessibleArray();

			f.writeLong(666);
			for(int i=0;i<10000;i++)
				f.writeInt((int)(Math.random()*100));
			f.seek(0);
			//System.out.println(f.readLong());
			//System.out.println(f.length());
			}
		catch (IOException e)
			{
			e.printStackTrace();
			}
		}


	public byte[] getLowlevelArray()
		{
		return fileContentArray;
		}
	}
