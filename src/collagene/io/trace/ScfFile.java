package collagene.io.trace;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * 
 * Reader for SCF files
 * 
 * Based on specification at http://staden.sourceforge.net/manual/formats_unix_2.html
 * 
 * @author Johan Henriksson
 *
 */
public class ScfFile
	{
	private static class Header
		{
		//note that all int should be unsigned int
	  int magic_number;
	  int samples;          /* Number of elements in Samples matrix */
	  int samples_offset;   /* Byte offset from start of file */
	  int bases;            /* Number of bases in Bases matrix */
	  //int bases_left_clip;  /* OBSOLETE: No. bases in left clip (vector) */
	  //int bases_right_clip; /* OBSOLETE: No. bases in right clip (qual) */
	  int bases_offset;     /* Byte offset from start of file */
	  int comments_size;    /* Number of bytes in Comment section */
	  int comments_offset;  /* Byte offset from start of file */
	  String version;//[4];         /* "version.revision", eg '3' '.' '0' '0' */
	  int sample_size;      /* Size of samples in bytes 1=8bits, 2=16bits*/
	  //int code_set;         /* code set used (but ignored!)*/
	  //int private_size;     /* No. of bytes of Private data, 0 if none */
	  //int private_offset;   /* Byte offset from start of file */
	  
	  double versionF;
	  
	  public void parse(DataInputStream is) throws IOException
	  	{
	  	magic_number=is.readInt();
	  	
	  	long SCF_MAGIC=('.'<<24) + ('s'<<16) + ('c'<<8) + ('f');
	  	if(magic_number!=SCF_MAGIC)
	  		throw new IOException("Not an SCF file");
	
	  	
	  	samples=is.readInt();
	  	samples_offset=is.readInt();
	  	bases=is.readInt();
	  	/*bases_left_clip=*/is.readInt();
	  	/*bases_right_clip=*/is.readInt();
	  	bases_offset=is.readInt();
	  	comments_size=is.readInt();
	  	comments_offset=is.readInt();
	  	version=readChars(is, 4);
	  	sample_size=is.readInt();
	  	/*code_set=*/is.readInt();
	  	/*private_size=*/is.readInt();
	  	/*private_offset=*/is.readInt();
	  	readChars(is, 18);
	  	System.out.println(version);
	  	
	  	if(version.startsWith("1."))
	  		{
	  		sample_size=1; //Default value
	  		}
	
			if(sample_size!=1 && sample_size!=2)
				throw new IOException("Unsupported sample size: "+sample_size);
	
	  	versionF=Double.parseDouble(version);
	  	}
		}
	
	


	
	/**
	 * Read all base calls
	 */
	private static void readBasecalls(SequenceTrace trace, DataInputStream is, Header h) throws IOException
		{
		is.skip(h.bases_offset);
		System.out.println("#bases "+h.bases);
		
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
	 * Read a given file
	 */
	public static SequenceTrace readFile(File f) throws IOException
		{
		SequenceTrace trace=new SequenceTrace();
		
		DataInputStream is=new DataInputStream(new FileInputStream(f));
		Header h=new Header();
		h.parse(is);
		is.close();

		is=new DataInputStream(new FileInputStream(f));
		is.skip(h.samples_offset);
		if(h.versionF < 2.9)
			{
			//Old format with ACGT-tuples
			trace.levelA=new int[h.samples];
			trace.levelC=new int[h.samples];
			trace.levelG=new int[h.samples];
			trace.levelT=new int[h.samples];
			if (h.sample_size==1)
				{
				for(int i=0;i<h.samples;i++)
					{
					trace.levelA[i]=is.readByte();
					trace.levelC[i]=is.readByte();
					trace.levelG[i]=is.readByte();
					trace.levelT[i]=is.readByte();
					}
				}
			else if(h.sample_size==2)
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
			trace.levelA=deltaSamples2(readIntegral(is, h.samples, h.sample_size), false);
			trace.levelC=deltaSamples2(readIntegral(is, h.samples, h.sample_size), false);
			trace.levelG=deltaSamples2(readIntegral(is, h.samples, h.sample_size), false);
			trace.levelT=deltaSamples2(readIntegral(is, h.samples, h.sample_size), false);
			}
		is.close();
		

		//Read basecalls
		is=new DataInputStream(new FileInputStream(f));
		readBasecalls(trace, is, h);
		is.close();
		
		is=new DataInputStream(new FileInputStream(f));
		is.skip(h.comments_offset);
		String comments=readChars(is, h.comments_size);
		is.close();
		StringTokenizer stok=new StringTokenizer(comments,"\n");
		while(stok.hasMoreTokens())
			{
			String line=stok.nextToken();
			int ind=line.indexOf('=');
			if(ind!=-1)
				trace.properties.put(line.substring(0,ind), line.substring(ind+1));
			}
		
		
		System.out.println(comments);
		
		
		
		
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
		SequenceTrace f=readFile(new File("/home/mahogny/Dropbox/ebi/_my protocols/retrovirus/sangerseq/pbabe/T205_data_pbabe.w2kseq1.scf"));
//		System.out.println(f.properties);
		System.out.println("B:"+f.getCalledSequence());
		}
	}
