package gui;

import io.SequenceExporter;
import io.SequenceFileHandlers;
import io.SequenceImporter;
import io.input.ImportAddgene;
import io.madgene.MadgeneXML;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import restrictionEnzyme.NEBparser;
import restrictionEnzyme.RestrictionEnzymeSet;
import seq.AnnotatedSequence;
import sequtil.ProteinTranslator;
import gui.anneal.AnnealWindow;
import gui.qt.QTutil;
import gui.sequenceWindow.SequenceWindow;

import com.trolltech.qt.core.QCoreApplication;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QClipboard;
import com.trolltech.qt.gui.QCloseEvent;
import com.trolltech.qt.gui.QFileDialog;
import com.trolltech.qt.gui.QInputDialog;
import com.trolltech.qt.gui.QMainWindow;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QMenuBar;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QTreeWidget;
import com.trolltech.qt.gui.QTreeWidgetItem;
import com.trolltech.qt.gui.QVBoxLayout;
import com.trolltech.qt.gui.QWidget;
import com.trolltech.qt.gui.QFileDialog.FileMode;

/**
 * This window will show all available sequences in a project, and other projects?
 * 
 * @author Johan Henriksson
 * 
 */
public class ProjectWindow extends QMainWindow
	{
	private QMenuBar menubar=new QMenuBar();
	private QTreeWidget wtree=new QTreeWidget();
	private QTreeWidgetItem itemPlasmids=new QTreeWidgetItem(wtree, Arrays.asList("Plasmids"));
	

	private File currentProjectFile=null;
	private File lastDirectory=new File(".");
	
	//Best is to add a hidden unique integer ID
	private MadgeneProject proj=new MadgeneProject();
	public RestrictionEnzymeSet restrictionEnzymes=new RestrictionEnzymeSet();

	
	/**
	 * Constructor
	 */
	public ProjectWindow()
		{
		//Read list of enzymes
		try
			{
			NEBparser.parse(restrictionEnzymes);
			}
		catch (IOException e)
			{
			e.printStackTrace();
			}

		
		wtree.header().setVisible(false);
		wtree.doubleClicked.connect(this,"actionOpenSequence()");		
		
		QPushButton bNewSequence=new QPushButton(tr("New sequence"));
		QPushButton bAnnealOligos=new QPushButton(tr("Anneal"));
		QPushButton bDeleteSequence=new QPushButton(tr("Delete sequence"));
		
		setMenuBar(menubar);
		QMenu mfile=menubar.addMenu("File");
		QMenu mImport=menubar.addMenu(tr("Import"));
		QMenu mExport=menubar.addMenu(tr("Export"));
		
		mfile.addAction(tr("New project"), this, "actionNewProject()");
		mfile.addAction(tr("Open project"), this, "actionOpenProject()");
		mfile.addAction(tr("Save project"), this, "actionSaveProject()");
		mfile.addAction(tr("Save project as"), this, "actionSaveProjectAs()");
//		mfile.addSeparator();
		mfile.addSeparator();
		mfile.addAction(tr("Quit"), this, "close()");

		mImport.addAction(tr("From file"), this, "actionImportFile()");
		mImport.addAction(tr("From clipboard"), this, "actionImportClipboard()");
		mImport.addAction(tr("From AddGene"), this, "actionImportAddgene()");
		mImport.addAction(tr("Protein as random DNA"), this, "actionImportProteinDNA()");

		mExport.addAction(tr("Export to file"), this, "actionExportFile()");

		
		bNewSequence.clicked.connect(this,"actionNewSequence()");
		bAnnealOligos.clicked.connect(this,"actionAnnealOligos()");
		bDeleteSequence.clicked.connect(this,"actionDeleteSequence()");
		
		QVBoxLayout lay=new QVBoxLayout();
		lay.addWidget(wtree);
		lay.addWidget(bNewSequence);
		lay.addWidget(bDeleteSequence);
		lay.addWidget(bAnnealOligos);
		
		QWidget w=new QWidget();
		w.setLayout(lay);
		setCentralWidget(w);
		
		updateView();
		
		resize(200, 400);
		show();
		}

	
	/**
	 * Action: import protein sequence as random DNA
	 */
	public void actionImportProteinDNA()
		{
		QClipboard cb=QApplication.clipboard();
		
		String s=cb.text();
		s=s.replace(" ", "").replace("\t", "").toUpperCase();
		AnnotatedSequence seq=new AnnotatedSequence();
		seq.setSequence(new ProteinTranslator().aminoToRandomTriplets(s));
		giveNewName(seq);
		addSequenceToProject(seq);
		showSequence(seq);
		}

	/**
	 * Action: Anneal two oligos
	 */
	public void actionAnnealOligos()
		{
		AnnealWindow w=new AnnealWindow();
		w.exec();
		if(w.seq!=null)
			{
			AnnotatedSequence seq=w.seq;
			giveNewName(seq);
			addSequenceToProject(seq);
			showSequence(seq);
			}
		}
	
	
	public AnnotatedSequence currentlySelectedSequence()
		{
		QTreeWidgetItem item=wtree.currentItem();
		if(item!=null)
			{
			AnnotatedSequence seq=(AnnotatedSequence)item.data(0, Qt.ItemDataRole.UserRole);
			return seq;
			}
		else
			return null;
		}
	
	/**
	 * Action: Open the sequence
	 */
	public void actionOpenSequence()
		{
		AnnotatedSequence seq=currentlySelectedSequence();
		if(seq!=null)
			{
			SequenceWindow w=new SequenceWindow(this);
			w.setSequence(seq);
			}
		}
	
	
	/**
	 * Update project view
	 */
	public void updateView()
		{
		while(itemPlasmids.childCount()>0)
			itemPlasmids.removeChild(itemPlasmids.child(0));
		for(AnnotatedSequence seq:proj.sequenceLinkedList)
			{
			QTreeWidgetItem item=new QTreeWidgetItem(Arrays.asList(seq.name));
			itemPlasmids.addChild(item);
			item.setData(0, Qt.ItemDataRole.UserRole, seq);
			}
		wtree.expandAll();
		}
	
	
	/**
	 * Create a new empty sequence
	 */
	public void actionNewSequence()
		{
		AnnotatedSequence seq=new AnnotatedSequence();
		seq.setSequence("ATACTatcggtatcagactgacagcagacgatcatatatatatataaaatacgaccacagacgtaa".toUpperCase());

		giveNewName(seq);
		addSequenceToProject(seq);
		showSequence(seq);
		}
	
	/**
	 * Action: delete currently selected sequence
	 */
	public void actionDeleteSequence()
		{
		AnnotatedSequence seq=currentlySelectedSequence();
		proj.sequenceLinkedList.remove(seq);
		updateView();
		}
	
	

	protected void closeEvent(QCloseEvent arg__1)
		{
		super.closeEvent(arg__1);
		System.exit(0);
		}
	

	
	/**
	 * Give a new anonymous name to a sequence
	 */
	public void giveNewName(AnnotatedSequence seq)
		{
		seq.name="unnamed_"+(int)(10000.0*Math.random());
		}

	
	/**
	 * Open new window to show sequence
	 */
	public void showSequence(AnnotatedSequence seq)
		{
		SequenceWindow w=new SequenceWindow(this);
		w.setSequence(seq);
		}
	
	/**
	 * Add sequence to project
	 */
	public void addSequenceToProject(AnnotatedSequence seq)
		{
		proj.sequenceLinkedList.add(seq);
		updateView();
		}
	
	
	
	
	
	
	
	

	
	
	
	
	
	
	
	/**
	 * Action: Import from addgene
	 */
	public void actionImportAddgene()
		{
		String t=QInputDialog.getText(this, tr("Import from Addgene"), tr("URL:"));
		if(t!=null)
			{
			try
				{
				addSequenceToProject(ImportAddgene.get(t));
				}
			catch (IOException e)
				{
				QTutil.showNotice(this, tr("Failed to load: ")+e.getMessage());
				e.printStackTrace();
				}
			}
		}
	
	
	
	
	/**
	 * Action: Import from file
	 */
	public void actionImportFile()
		{
		QFileDialog dia=new QFileDialog();
		dia.setFileMode(FileMode.ExistingFile);
		dia.setDirectory(lastDirectory.getAbsolutePath());
		dia.setNameFilter(tr("Sequence files (*.seqfile *.gb *.fasta)"));
		if(dia.exec()!=0)
			{
			File f=new File(dia.selectedFiles().get(0));
			lastDirectory=f.getParentFile();
			try
				{
				SequenceImporter importer=SequenceFileHandlers.getImporter(f);
				if(importer!=null)
					{
					FileInputStream fis=new FileInputStream(f);
					AnnotatedSequence seq=importer.load(fis).get(0);
					fis.close();
					addSequenceToProject(seq); //TODO name?
					}
				else
					QTutil.showNotice(this, tr("Unknown file format"));

				}
			catch (IOException e)
				{
				QTutil.showNotice(this, e.getMessage());
				e.printStackTrace();
				}
			}		
		}

	
	
	/**
	 * Action: New project
	 */
	public void actionNewProject()
		{
		if(QTutil.showOkCancel(tr("Are you sure you want to create a new project?")))
			{
			currentProjectFile=null;
			proj=new MadgeneProject();
			updateView();
			//TODO close all other windows too
			}
		}
	
	
	/**
	 * Action: Open project
	 */
	public void actionOpenProject()
		{
		QFileDialog dia=new QFileDialog();
		dia.setFileMode(FileMode.ExistingFile);
		dia.setDirectory(lastDirectory.getAbsolutePath());
		dia.setNameFilter(tr("Project files (*.madgene)"));
		if(dia.exec()!=0)
			{
			File f=new File(dia.selectedFiles().get(0));
			lastDirectory=f.getParentFile();
			try
				{
				proj=MadgeneXML.loadProject(f);
				}
			catch (IOException e)
				{
				QTutil.showNotice(this, e.getMessage());
				e.printStackTrace();
				}
			}		
		}

	
	/**
	 * Action: Save project
	 */
	public void actionSaveProject()
		{
		if(currentProjectFile==null)
			actionSaveProjectAs();
		if(currentProjectFile!=null)
			try
				{
				MadgeneXML.saveProject(currentProjectFile, proj);
				}
			catch (IOException e)
				{
				QTutil.showNotice(this, e.getMessage());
				e.printStackTrace();
				}
		}

	

	/**
	 * Action: Save as... file
	 */
	public void actionSaveProjectAs()
		{
		QFileDialog dia=new QFileDialog();
		dia.setFileMode(FileMode.AnyFile);
		dia.setDirectory(lastDirectory.getAbsolutePath());
		dia.setDefaultSuffix("madgene");
		dia.setNameFilter(tr("Project files (*.madgene)"));
		if(dia.exec()!=0)
			{
			File f=new File(dia.selectedFiles().get(0));
			lastDirectory=f.getParentFile();
			currentProjectFile=f;
			}
		}


	/**
	 * Action: Export file
	 */
	public void actionExportFile()
		{
		QFileDialog dia=new QFileDialog();
		dia.setFileMode(FileMode.AnyFile);
		dia.setDirectory(lastDirectory.getAbsolutePath());
		dia.setDefaultSuffix("gb");
		dia.setNameFilter(tr("Sequence files (*.gb *.fasta)"));
		if(dia.exec()!=0)
			{
			File f=new File(dia.selectedFiles().get(0));
			
			SequenceExporter exp=SequenceFileHandlers.getExporter(f);
			if(exp!=null)
				{
				try
					{
					SequenceFileHandlers.save(exp, f, proj.sequenceLinkedList);
					lastDirectory=f.getParentFile();
					}
				catch (IOException e)
					{
					e.printStackTrace();
					QTutil.showNotice(this, tr("Failed to export")+"; "+e.getMessage());
					}
				}
			else
				QTutil.showNotice(this, tr("Unknown file format"));
			}
		}

	

	/**
	 * Action: Import from clipboard
	 */
	public void actionImportClipboard()	
		{
		try
			{
			QClipboard cb=QApplication.clipboard();
			String s=cb.text();
			char[] sarr=s.toCharArray();
			byte[] barr=new byte[sarr.length];
			for(int i=0;i<sarr.length;i++)
				barr[i]=(byte)sarr[i]; //not quite optimal
			
			List<AnnotatedSequence> seqs=SequenceFileHandlers.load(barr);
			if(seqs==null)
				QTutil.showNotice(this, tr("Unknown file format"));
			else
				{
				if(seqs.size()>0)
					addSequenceToProject(seqs.get(0)); //TODO
				else
					QTutil.printError(this, tr("No sequences in data"));
				}
			}
		catch (IOException e)
			{
			QTutil.printError(this, tr("Could not read file format"));
			e.printStackTrace();
			}					
		}
	
	
	

	
	/**
	 * Entry point
	 */
	public static void main(String[] args)
		{
		QApplication.initialize(QtProgramInfo.programName, args);
		QCoreApplication.setApplicationName(QtProgramInfo.programName);
		/*MainWindow w=*/new ProjectWindow();
		QTutil.execStaticQApplication();		
		}

	}
