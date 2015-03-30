package gui;

import io.madgene.MadgeneXML;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import restrictionEnzyme.RestrictionEnzymeSet;
import seq.AnnotatedSequence;

/**
 * 
 * 
 * @author Johan Henriksson
 *
 */
public class MadgeneProject
	{
	public File currentProjectFile=null;

	public LinkedList<AnnotatedSequence> sequenceLinkedList=new LinkedList<AnnotatedSequence>();
	public RestrictionEnzymeSet restrictionEnzymes=new RestrictionEnzymeSet();
	public boolean isModified=false;
	
	public void saveProject() throws IOException
		{
		MadgeneXML.saveProject(currentProjectFile, this);
		}
	}
