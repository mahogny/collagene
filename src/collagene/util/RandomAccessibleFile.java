package collagene.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

/**
 * Random accessible file
 * 
 * @author Johan Henriksson
 *
 */
public class RandomAccessibleFile implements RandomAccessible
	{
	private RandomAccessFile ra;
	private File f;
	
	
	public RandomAccessibleFile(File f, String mode) throws IOException
		{
		this.f=f;
		ra=new RandomAccessFile(f, mode);
		}



	public void write(byte[] arr) throws IOException
		{
		ra.write(arr);
		}


	public void write(byte[] buf, int off, int len) throws IOException
		{
		ra.write(buf, off, len);
		}


	public void writeByte(int value) throws IOException
		{
		ra.writeByte(value);
		}


	public void writeLong(long value) throws IOException
		{
		ra.writeLong(value);
		}


	public void writeInt(int value) throws IOException
		{
		ra.writeInt(value);
		}


	public int read() throws IOException
		{
		return ra.read();
		}


	public int read(byte[] b, int off, int len) throws IOException
		{
		return ra.read(b,off,len);
		}


	public int read(byte[] arr) throws IOException
		{
		return ra.read(arr);
		}


	public int readByte() throws IOException
		{
		return ra.readByte();
		}


	public long readLong() throws IOException
		{
		return ra.readLong();
		}


	public int readInt() throws IOException
		{
		return ra.readInt();
		}


	public void seek(long pos) throws IOException
		{
		ra.seek(pos);
		}


	public long getFilePointer() throws IOException
		{
		return ra.getFilePointer();
		}


	public long length() throws IOException
		{
		return ra.length();
		}


	public void close() throws IOException
		{
		ra.close();
		ra=null;
		}




	public void flush() throws IOException
		{
		ra.close();
		ra=new RandomAccessFile(f, "rw");
		}


	public void writeLong(int value) throws IOException
		{
		ra.writeLong(value);
		}


	public void sync() throws IOException
		{
		ra.getFD().sync();
		}


	public OutputStream getOutputStream()
		{
		return new OutputStream()
			{
			
			@Override
			public void flush() throws IOException
				{
				RandomAccessibleFile.this.flush();
				}

			@Override
			public void write(byte[] b, int off, int len) throws IOException
				{
				RandomAccessibleFile.this.write(b,off,len);
				}

			@Override
			public void write(int b) throws IOException
				{
				RandomAccessibleFile.this.writeByte(b);
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
				return ra.skipBytes((int)n);
				}

			@Override
			public int read(byte[] b, int off, int len) throws IOException
				{
				return RandomAccessibleFile.this.read(b,off,len);
				}

			@Override
			public int read(byte[] b) throws IOException
				{
				return RandomAccessibleFile.this.read(b);
				}

			@Override
			public int read() throws IOException
				{
				return RandomAccessibleFile.this.read();
				}
			};
		}



	public long skipBytes(long pos) throws IOException
		{
		return ra.skipBytes((int)pos);
		}
		
	}
