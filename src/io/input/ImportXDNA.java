package io.input;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import seq.AnnotatedSequence;
import seq.Orientation;
import seq.SeqAnnotation;
import seq.SeqColor;
import gui.colors.ColorSet;
import io.SequenceImporter;

/**
 * Strider and Serial Cloner fileformat
 * 
 * Specification: http://www.incenp.org/dvlpt/xdna2.html
 * It also covers serial cloner!
 * 
 * @author Johan Henriksson
 *
 */
public class ImportXDNA implements SequenceImporter
	{

	
	
	
	
	private String readPascalString(DataInputStream dis) throws IOException
		{
		//1 byte for length
		int length=dis.read();
		if(length<0)
			throw new IOException("negative length for PS");
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<length;i++)
			sb.append((char)dis.readByte());
		return sb.toString();
		}
	
	



	@Override
	public boolean isType(InputStream is) throws IOException
		{
		try
			{
			load(is);
			System.out.println("wheeeeeeeeeeeeeee");
			return true;
			}
		catch (Exception e)
			{
			System.out.println("baaaaaaaaaaah");
			e.printStackTrace();
			return false;
			}
		}


	@Override
	public List<AnnotatedSequence> load(InputStream is) throws IOException
		{
		ColorSet colorset=ColorSet.colorset;
		int curcol=0;
		DataInputStream dis=new DataInputStream(is);

		
		AnnotatedSequence seq=new AnnotatedSequence();
		
		///////////////////// Header section /////////
		/*byte version=*/dis.readByte();
		byte seqtype=dis.readByte(); //1 = DNA, 2 = degenerate DNA, 3 = RNA, 4 = protein
		if(seqtype==4)
			throw new IOException("Protein not supported");
		int topology=dis.read();
		if(topology==1)
			seq.isCircular=true;
		else if(topology==0)
			seq.isCircular=false;
		else
			throw new IOException("Error reading topology");
		for(int i=0;i<25;i++)
			dis.readByte(); //Padding
		int seqlength=dis.readInt();
		for(int i=0;i<64;i++)
			dis.readByte(); //Padding
		int commentLength=dis.readInt();
		for(int i=0;i<12;i++)
			dis.readByte(); //Padding

		//Sequence section 
		seq.setSequence(readNchars(seqlength, dis));
		
		//Comment section 
		seq.notes=readNchars(commentLength, dis);

//		System.out.println(seq.notes);
		//Annotations section (optional, serial cloner)
		int unknownByte=dis.read();
		if(unknownByte!=-1)
			{
			String rohLengthS=readPascalString(dis);
			int rohLength=Integer.parseInt(rohLengthS);
			String rohOverhang=readNchars(rohLength, dis);
			
			System.out.println(rohOverhang);
			
			String lohLengthS=readPascalString(dis);
			int lohLength=Integer.parseInt(lohLengthS);
			String lohOverhang=readNchars(lohLength, dis);
			
			int numAnnotation=dis.read();
			System.out.println("num "+numAnnotation);
			
			//TODO add overhang
			//TODO positions, are they +1? likely?
			
			for(int i=0;i<numAnnotation;i++)
				{
				SeqAnnotation a=new SeqAnnotation();
				a.name=readPascalString(dis);
				a.desc=readPascalString(dis);
				String ftype=readPascalString(dis);
				System.out.println(ftype);
				
				a.from=Integer.parseInt(readPascalString(dis));
				a.to=Integer.parseInt(readPascalString(dis));
				
				int strand=dis.read();
				if(strand==1)
					a.orientation=Orientation.FORWARD;
				else if(strand==0)
					{
					a.orientation=Orientation.REVERSE;
					int temp=a.to;
					a.to=a.from;
					a.from=temp;
					}
				else
					throw new IOException("Unknown strand");
				/*int show=*/dis.read();
				/*int unknown2=*/dis.read();
				/*int arrow=*/dis.read();
				readPascalString(dis); //extra
			
				curcol=(curcol+1)%colorset.size();
				a.color=new SeqColor(colorset.get(curcol));

				seq.annotations.add(a);
				}
			}
		seq.name=""+Math.random(); //TODO should really use filename
		
		return Arrays.asList(seq);
		}
	
	private String readNchars(int n, DataInputStream dis) throws IOException
		{
		if(n<0)
			throw new IOException("Negative length");
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<n;i++)
			sb.append((char)dis.readByte());
		return sb.toString();
		}
	
	}
