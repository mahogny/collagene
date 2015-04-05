package collagene.io.trace;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;

import collagene.util.ByteArray;

/**
 * 
 * Reader for SCF files
 * 
 * Based on specification at http://staden.sourceforge.net/manual/formats_unix_2.html
 * 
 * @author Johan Henriksson
 *
 */
public class TraceReaderSCF implements TraceReader
	{
	private static class Header
		{
		//note that all int should be unsigned int
	  int magicNumber;
	  int samples;          /* Number of elements in Samples matrix */
	  int samplesOffset;   /* Byte offset from start of file */
	  int bases;            /* Number of bases in Bases matrix */
	  int basesOffset;     /* Byte offset from start of file */
	  int commentsSize;    /* Number of bytes in Comment section */
	  int commentsOffset;  /* Byte offset from start of file */
	  String version;//[4];         /* "version.revision", eg '3' '.' '0' '0' */
	  int sampleSize;      /* Size of samples in bytes 1=8bits, 2=16bits*/
	  //int code_set;         /* code set used (but ignored!)*/
	  //int private_size;     /* No. of bytes of Private data, 0 if none */
	  //int private_offset;   /* Byte offset from start of file */
	  
	  double versionF;
	  
	  public void parse(DataInputStream is) throws IOException
	  	{
	  	magicNumber=is.readInt();
	  	
	  	long SCF_MAGIC=('.'<<24) + ('s'<<16) + ('c'<<8) + ('f');
	  	if(magicNumber!=SCF_MAGIC)
	  		throw new IOException("Not an SCF file");
	
	  	
	  	samples=is.readInt();
	  	samplesOffset=is.readInt();
	  	bases=is.readInt();
	  	/*bases_left_clip=*/is.readInt();
	  	/*bases_right_clip=*/is.readInt();
	  	basesOffset=is.readInt();
	  	commentsSize=is.readInt();
	  	commentsOffset=is.readInt();
	  	version=readChars(is, 4);
	  	sampleSize=is.readInt();
	  	/*code_set=*/is.readInt();
	  	/*private_size=*/is.readInt();
	  	/*private_offset=*/is.readInt();
	  	readChars(is, 18);
	  	System.out.println(version);
	  	
	  	if(version.startsWith("1."))
	  		{
	  		sampleSize=1; //Default value
	  		}
	
			if(sampleSize!=1 && sampleSize!=2)
				throw new IOException("Unsupported sample size: "+sampleSize);
	
	  	versionF=Double.parseDouble(version);
	  	}
		}
	
	


	
	/**
	 * Read all base calls
	 */
	private static void readBasecalls(SequenceTrace trace, DataInputStream is, Header h) throws IOException
		{
		is.skip(h.basesOffset);
		
		if(h.versionF<2.9)
			{
			//Old direct way of storing base calls
			for(int i=0;i<h.bases;i++)
				{
				SequenceTraceBaseCall bc=new SequenceTraceBaseCall();
				bc.peakIndex=is.readInt();
				bc.pA=is.readByte();
				bc.pC=is.readByte();
				bc.pG=is.readByte();
				bc.pT=is.readByte();
				bc.base=(char)is.readByte();
				is.skipBytes(3);
				trace.basecalls.add(bc);
				}
			}
		else
			{
			//New split up method for storing base calls
			for(int i=0;i<h.bases;i++)
				{
				SequenceTraceBaseCall bc=new SequenceTraceBaseCall();
				trace.basecalls.add(bc);
				}
			for(int i=0;i<h.bases;i++)
				{
				trace.basecalls.get(i).peakIndex=is.readInt();
//				System.out.println(trace.basecalls.get(i).peakIndex);
				}
			for(int i=0;i<h.bases;i++)
				trace.basecalls.get(i).pA=is.readByte();
			for(int i=0;i<h.bases;i++)
				trace.basecalls.get(i).pC=is.readByte();
			for(int i=0;i<h.bases;i++)
				trace.basecalls.get(i).pG=is.readByte();
			for(int i=0;i<h.bases;i++)
				trace.basecalls.get(i).pT=is.readByte();
			for(int i=0;i<h.bases;i++)
				trace.basecalls.get(i).base=(char)is.readByte();
			}
		}
	
	
	/**
	 * Read characters into string
	 */
	private static String readChars(DataInputStream is, int n) throws IOException
		{
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<n;i++)
			sb.append((char)is.readByte());
		return sb.toString();
		}
	
	/**
	 * Read integral values
	 */
	private static int[] readIntegral(DataInputStream is, int n, int bytesize) throws IOException
		{
		int[] arr=new int[n];
		if(bytesize==1)
			for(int i=0;i<n;i++)
				arr[i]=is.readByte();
		else
			for(int i=0;i<n;i++)
				arr[i]=is.readShort();
		return arr;
		}
	
	
	/**
	 * 
	 */
	public SequenceTrace readFile(InputStream is2) throws IOException
		{
		SequenceTrace trace=new SequenceTrace();
		ByteArray ba=new ByteArray();
		
		byte[] b=new byte[10240];
		for(;;)
			{
			int len=is2.read(b);
			if(len==-1)
				break;
			ba.add(b,len);
			if(ba.size()>4000000)
				throw new IOException("File seems too large for an SCF file");
			//could instead first read header. check magic byte. then read the rest
			}
		
		DataInputStream is=new DataInputStream(new ByteArrayInputStream(ba.getArray(),0,ba.size()));
		Header h=new Header();
		h.parse(is);
		is.close();

		is=new DataInputStream(new ByteArrayInputStream(ba.getArray(),0,ba.size()));
		is.skip(h.samplesOffset);
		if(h.versionF < 2.9)
			{
			//Old format with ACGT-tuples
			trace.levelA=new int[h.samples];
			trace.levelC=new int[h.samples];
			trace.levelG=new int[h.samples];
			trace.levelT=new int[h.samples];
			if (h.sampleSize==1)
				{
				for(int i=0;i<h.samples;i++)
					{
					trace.levelA[i]=is.readByte();
					trace.levelC[i]=is.readByte();
					trace.levelG[i]=is.readByte();
					trace.levelT[i]=is.readByte();
					}
				}
			else if(h.sampleSize==2)
				{
				for(int i=0;i<h.samples;i++)
					{
					trace.levelA[i]=is.readShort();
					trace.levelC[i]=is.readShort();
					trace.levelG[i]=is.readShort();
					trace.levelT[i]=is.readShort();
					}
				}
			}
		else
			{
			//New delta-compressed format
			trace.levelA=deltaSamples2(readIntegral(is, h.samples, h.sampleSize), false);
			trace.levelC=deltaSamples2(readIntegral(is, h.samples, h.sampleSize), false);
			trace.levelG=deltaSamples2(readIntegral(is, h.samples, h.sampleSize), false);
			trace.levelT=deltaSamples2(readIntegral(is, h.samples, h.sampleSize), false);
			}
		is.close();
		

		//Read basecalls
		is=new DataInputStream(new ByteArrayInputStream(ba.getArray(),0,ba.size()));
		readBasecalls(trace, is, h);
		is.close();
		
		is=new DataInputStream(new ByteArrayInputStream(ba.getArray(),0,ba.size()));
		is.skip(h.commentsOffset);
		String comments=readChars(is, h.commentsSize);
		is.close();
		StringTokenizer stok=new StringTokenizer(comments,"\n");
		while(stok.hasMoreTokens())
			{
			String line=stok.nextToken();
			int ind=line.indexOf('=');
			if(ind!=-1)
				trace.properties.put(line.substring(0,ind), line.substring(ind+1));
			}
		
		System.out.println(trace.properties);

		return trace;
		}
	
	/**
	 * Read a given file
	 */
	public SequenceTrace readFile(File f) throws IOException
		{
		FileInputStream is=new FileInputStream(f);
		SequenceTrace trace=readFile(is);
		is.close();
		
		return trace;
		}
	

	/**
	 * Convert to/from delta delta format
	 */
	private static int[] deltaSamples2(int samples[], boolean toDelta)
		{
		int numSamples=samples.length;

		/*
		 * change a series of sample points to a series of delta delta
		 * values: ie change them in two steps: first: delta = current_value -
		 * previous_value then: delta_delta = delta - previous_delta 
		 */
		if (toDelta)
			{
			int p_delta = 0;
			for (int i = 0; i<numSamples; i++)
				{
				int p_sample = samples[i];
				samples[i] = samples[i]-p_delta;
				p_delta = p_sample;
				}
			p_delta = 0;
			for (int i = 0; i<numSamples; i++)
				{
				int p_sample = samples[i];
				samples[i] = samples[i]-p_delta;
				p_delta = p_sample;
				}
			}
		else
			{
			int p_sample = 0;
			for (int i = 0; i<numSamples; i++)
				p_sample = samples[i] = samples[i]+p_sample;
			p_sample = 0;
			for (int i = 0; i<numSamples; i++)
				p_sample = samples[i] = samples[i]+p_sample;
			}
		
		return samples;
		}

	public static void main(String[] args) throws IOException
		{
		SequenceTrace f=new TraceReaderSCF().readFile(new File("/home/mahogny/Dropbox/ebi/_my protocols/retrovirus/sangerseq/pbabe/T205_data_pbabe.w2kseq1.scf"));
//		System.out.println(f.properties);
		System.out.println("B:"+f.getCalledSequence());
		}
	}
