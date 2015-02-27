package io;

import io.input.ImportFASTA;
import io.input.ImportGenbank;
import io.input.ImportRawSequence;
import io.madgene.ImportOneXML;
import io.output.ExportFASTA;
import io.output.ExportGenbank;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import seq.AnnotatedSequence;

/**
 * 
 * Registered file handlers
 * 
 * @author Johan Henriksson
 *
 */
public class SequenceFileHandlers
	{
	private static LinkedList<SequenceImporter> listImporters=new LinkedList<SequenceImporter>();
	private static LinkedList<SequenceExporter> listExporters=new LinkedList<SequenceExporter>();
	
	static
		{
		//Importers
		listImporters.add(new ImportFASTA());
		listImporters.add(new ImportGenbank());
		listImporters.add(new ImportOneXML());
		listImporters.add(new ImportRawSequence());
		
		//Exporters
		listExporters.add(new ExportFASTA());
		listExporters.add(new ExportGenbank());
		}

	/**
	 * Get exporter for a file
	 */
	public static SequenceExporter getExporter(File f) 
		{
		for(SequenceExporter imp:listExporters)
			{
			if(imp.isType(f))
				return imp;
			}
		return null;
		}


	/**
	 * Get importer for a file
	 */
	public static SequenceImporter getImporter(File f) throws IOException
		{
		for(SequenceImporter imp:listImporters)
			{
			FileInputStream fis=new FileInputStream(f);
			boolean b=imp.isType(fis);
			fis.close();
			if(b)
				return imp;
			}
		return null;
		}


	/**
	 * Get importer from a byte array
	 */
	public static SequenceImporter getImporter(byte[] arr) throws IOException
		{
		for(SequenceImporter imp:listImporters)
			{
			ByteArrayInputStream fis=new ByteArrayInputStream(arr);
			boolean b=imp.isType(fis);
			fis.close();
			if(b)
				return imp;
			}
		return null;
		}


	/**
	 * Detect type and load from byte array
	 */
	public static List<AnnotatedSequence> load(byte[] barr) throws IOException
		{
		SequenceImporter importer=SequenceFileHandlers.getImporter(barr);
		if(importer!=null)
			{
			ByteArrayInputStream fis=new ByteArrayInputStream(barr);
			List<AnnotatedSequence> seqs=importer.load(fis);
			fis.close();
			return seqs;
			}
		else
			return null;
		}


	/**
	 * Save into file using given exporter
	 */
	public static void save(SequenceExporter exporter, File f, LinkedList<AnnotatedSequence> list) throws IOException
		{
		FileOutputStream fw=new FileOutputStream(f);
		exporter.save(fw, list);
		fw.close();
		}
	
	
	}
