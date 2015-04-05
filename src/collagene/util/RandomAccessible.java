package collagene.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * 
 * A random accessible data source
 * 
 * @author Johan Henriksson
 *
 */
public interface RandomAccessible
	{
	
	
	/**
	 * Seek to position
	 */
	public void seek(long pos) throws IOException;

	/**
	 * Skip forward
	 */
	public long skipBytes(long pos) throws IOException;

	/**
	 * Write an array
	 */
	public void write(byte[] arr) throws IOException;
	
	/**
	 * Write an array
	 */
	public void write(byte[] buf, int off, int len) throws IOException;


	/**
	 * Write a byte
	 */
	public void writeByte(int value) throws IOException;

	/**
	 * Write a long
	 */
	public void writeLong(long value) throws IOException;

	/**
	 * Write a int
	 */
	public void writeInt(int value) throws IOException;

	

	
	
	/**
	 * Read a byte
	 */
	public int read() throws IOException;

	/**
	 * Read an array
	 */
	public int read(byte[] b, int off, int len) throws IOException;
	
	/**
	 * Read an array
	 */
	public int read(byte[] arr) throws IOException;

	/**
	 * Read a byte
	 */
	public int readByte() throws IOException;

	/**
	 * Read a long
	 */
	public long readLong() throws IOException;

	/**
	 * Read an integer
	 */
	public int readInt() throws IOException;


	/**
	 * Get file pointer
	 */
	public long getFilePointer() throws IOException;

	/**
	 * Get size of file
	 */
	public long length() throws IOException;


	/**
	 * Close file
	 */
	public void close() throws IOException;

	

	public void flush() throws IOException;

	public void sync() throws IOException;

	
	public OutputStream getOutputStream();

	public InputStream getInputStream();
	}
