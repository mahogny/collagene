package collagene.gui;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import collagene.io.collagene.CollageneXML;
import collagene.restrictionEnzyme.RestrictionEnzymeSet;
import collagene.seq.AnnotatedSequence;

/**
 * 
 * 
 * @author Johan Henriksson
 *
 */
public class CollageneProject
	{
	public File currentProjectFile=null;

	public LinkedList<AnnotatedSequence> sequenceLinkedList=new LinkedList<AnnotatedSequence>();
	public RestrictionEnzymeSet restrictionEnzymes=new RestrictionEnzymeSet();
	public boolean isModified=false;
	
	public void saveProject() throws IOException
		{
		CollageneXML.saveProject(currentProjectFile, this);
		}
	}
