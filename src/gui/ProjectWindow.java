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
import java.util.LinkedList;
import java.util.List;

import restrictionEnzyme.NEBparser;
import restrictionEnzyme.RestrictionEnzymeSet;
import seq.AnnotatedSequence;
import sequtil.LigationCandidate;
import sequtil.LigationUtil;
import sequtil.NucleotideUtil;
import sequtil.ProteinTranslator;
import gui.anneal.AnnealWindow;
import gui.cloneAssembler.CloneAssembler;
import gui.paneLinear.EventNewSequence;
import gui.qt.QTutil;
import gui.resource.ImgResource;
import gui.sequenceWindow.EventSequenceModified;
import gui.sequenceWindow.SequenceWindow;
import alignment.AnnotatedSequenceAlignment;
import alignment.PairwiseAlignment;
import alignment.emboss.EmbossCost;

import com.trolltech.qt.core.QCoreApplication;
import com.trolltech.qt.core.QModelIndex;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QAbstractItemView.SelectionMode;
import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QClipboard;
import com.trolltech.qt.gui.QCloseEvent;
import com.trolltech.qt.gui.QFileDialog;
import com.trolltech.qt.gui.QFileDialog.AcceptMode;
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
import com.trolltech.qt.gui.QLineEdit.EchoMode;

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
		ImgResource.setWindowIcon(this);
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
//		wtree.setSelectionBehavior(SelectionBehavior.SelectRows);
		wtree.setSelectionMode(SelectionMode.ExtendedSelection);
		wtree.doubleClicked.connect(this,"actionOpenSequence()");		
		
		QPushButton bNewSequence=new QPushButton(tr("New sequence"));
		QPushButton bAnnealOligos=new QPushButton(tr("Anneal"));
		QPushButton bDeleteSequence=new QPushButton(tr("Delete sequence"));
		QPushButton bAlign=new QPushButton(tr("Align"));
		QPushButton bLigate=new QPushButton(tr("Ligate"));
		QPushButton bAssemble=new QPushButton(tr("Assemble"));
		
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
		bAlign.clicked.connect(this,"actionAlign()");
		bLigate.clicked.connect(this,"actionLigate()");
		bAssemble.clicked.connect(this,"actionAssemble()");
		
		QVBoxLayout lay=new QVBoxLayout();
		lay.addWidget(wtree);
		lay.addWidget(bNewSequence);
		lay.addWidget(bDeleteSequence);
		lay.addWidget(bAnnealOligos);
		lay.addWidget(bAlign);
		lay.addWidget(bLigate);
		lay.addWidget(bAssemble);
		lay.setMargin(2);
		lay.setSpacing(2);
		
		QWidget w=new QWidget();
		w.setLayout(lay);
		setCentralWidget(w);
		
		updateView();
		
		resize(300, 480);
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

	
	public void actionAlign()
		{
		LinkedList<AnnotatedSequence> seqs=getSelectedSequences();
		if(seqs.size()==1)
			{
			String otherseq=QInputDialog.getText(this, tr("Align sequence"), tr("Sequence to align to:"));
			if(otherseq!=null)
				{
				otherseq=NucleotideUtil.normalize(otherseq);
				
				AnnotatedSequenceAlignment al=new AnnotatedSequenceAlignment();
				AnnotatedSequence seqB=new AnnotatedSequence();
				seqB.setSequence(otherseq);
				al.align(seqs.get(0), seqB);
				
				AnnotatedSequence seq=al.alSeqAwithB;
						
				giveNewName(seq);
				addSequenceToProject(seq);
				showSequence(seq);
				}
			}
		else if(seqs.size()==2)
			{
			PairwiseAlignment al=new PairwiseAlignment();
			al.costtable=EmbossCost.tableBlosum62;
			al.isLocalA=false;
			al.align(seqs.get(0).getSequence(), seqs.get(1).getSequence());
			
			AnnotatedSequence seq=new AnnotatedSequence();
			seq.setSequence(al.alignedSequenceA, al.alignedSequenceB);

			giveNewName(seq);
			addSequenceToProject(seq);
			showSequence(seq);
			}
		else
			QTutil.showNotice(this, tr("Select 1 or 2 sequences"));
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

	public LinkedList<AnnotatedSequence> getSelectedSequences()
		{
		LinkedList<AnnotatedSequence> seqs=new LinkedList<AnnotatedSequence>();
		for(QModelIndex ind:wtree.selectionModel().selectedIndexes())
			{
			AnnotatedSequence seq=(AnnotatedSequence)ind.data(Qt.ItemDataRole.UserRole);
			seqs.add(seq);
			}
		return seqs;
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
			showSequence(seq);
			}
		}
	
	private LinkedList<SequenceWindow> seqwindows=new LinkedList<SequenceWindow>();
	
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
//		seq.setSequence("ATACTatcggtatcagactgacagcagacgatcatatatatatataaaatacgaccacagacgtaa".toUpperCase());

		giveNewName(seq);
		addSequenceToProject(seq);
		showSequence(seq);
		}
	
	/**
	 * Action: delete currently selected sequence
	 */
	public void actionDeleteSequence()
		{
		boolean ok=QTutil.showOkCancel(tr("Are you sure you wish to delete this sequence?"));
		if(ok)
			{
			for(AnnotatedSequence seq:getSelectedSequences())
				proj.sequenceLinkedList.remove(seq);
			updateView();
			}
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
		seqwindows.add(w);
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
		String suggest="";
		QClipboard cb=QApplication.clipboard();
		String s=cb.text();
		if(s!=null && ImportAddgene.isAddgeneUrl(s))
			suggest=s;
		String t=QInputDialog.getText(this, tr("Import from Addgene"), tr("URL:"), EchoMode.Normal, suggest);
		if(t!=null)
			{
			try
				{
				if(ImportAddgene.isAddgeneUrl(t))
					addSequenceToProject(ImportAddgene.get(t));
				else
					throw new IOException(tr("Not the right URL; should be one showing one sequence"));
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
		dia.setFileMode(FileMode.ExistingFiles);
		dia.setDirectory(lastDirectory.getAbsolutePath());
		dia.setNameFilter(tr("Sequence files (*.seqfile *.gb *.fasta *.xdna)"));
		if(dia.exec()!=0)
			{
			try
				{
				for(String sf:dia.selectedFiles())
					{
					File f=new File(sf);
					lastDirectory=f.getParentFile();
					SequenceImporter importer=SequenceFileHandlers.getImporter(f);
					if(importer!=null)
						{
						FileInputStream fis=new FileInputStream(f);
						List<AnnotatedSequence> seqs=importer.load(fis);
						System.out.println(seqs.size());
						AnnotatedSequence seq=seqs.get(0);
						fis.close();
						addSequenceToProject(seq); //TODO name?
						}
					else
						QTutil.showNotice(this, tr("Unknown file format"));
					}
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
				currentProjectFile=f;
				}
			catch (IOException e)
				{
				QTutil.showNotice(this, e.getMessage());
				e.printStackTrace();
				}
			updateView();
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
		dia.setAcceptMode(AcceptMode.AcceptSave);
		dia.setDirectory(lastDirectory.getAbsolutePath());
		dia.setDefaultSuffix("madgene");
		dia.setNameFilter(tr("Project files (*.madgene)"));
		if(dia.exec()!=0)
			{
			File f=new File(dia.selectedFiles().get(0));
			lastDirectory=f.getParentFile();
			currentProjectFile=f;
			actionSaveProject();
			}
		}


	/**
	 * Action: Export file
	 */
	public void actionExportFile()
		{
		LinkedList<AnnotatedSequence> seqs=getSelectedSequences();
		if(seqs.size()>0)
			{
			QFileDialog dia=new QFileDialog();
			dia.setFileMode(FileMode.AnyFile);
			dia.setAcceptMode(AcceptMode.AcceptSave);
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
						SequenceFileHandlers.save(exp, f, seqs);
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
	 * Action: Ligate selected sequences
	 */
	public void actionLigate()
		{
		LinkedList<AnnotatedSequence> seqs=getSelectedSequences();
		for(AnnotatedSequence seq:seqs)
			if(seq.isCircular)
				{
				QTutil.showNotice(this, tr("Sequence is already circular")+": "+seq.name);
				return;
				}

		if(seqs.size()==1)
			{
			//Self-ligation
			AnnotatedSequence seq=seqs.get(0);
			if(LigationUtil.canLigateAtoB(seq, seq))
				{
				AnnotatedSequence newseq=new AnnotatedSequence(seq);
				LigationUtil.selfCircularize(newseq);
				newseq.name=newseq.name+"_circ";
				addSequenceToProject(newseq); 
				}
			else
				QTutil.showNotice(this, tr("Sequence does not have matching ends"));
			}
		else if(seqs.size()==2)
			{
			//If ever bringing up a menu to select combinations, also show potential self-ligation. Or just include in the list!
			AnnotatedSequence seqA=seqs.get(0);
			AnnotatedSequence seqB=seqs.get(1);
			
			LinkedList<LigationCandidate> cands=LigationUtil.getLigationCombinations(seqA, seqB);
			if(cands.isEmpty())
				QTutil.showNotice(this, tr("Sequences does not have any matching ends"));
			else
				{
				for(LigationCandidate cand:cands)
					{
					AnnotatedSequence seqOut=cand.getProduct();
					if(LigationUtil.canCircularize(seqOut) && QTutil.showYesNo(tr("Would you like to circularize the resulting plasmid as well?")))
						LigationUtil.selfCircularize(seqOut);
					addSequenceToProject(seqOut); 
					}
				}
			
			}
		else
			QTutil.showNotice(this, tr("Select 1 or 2 sequences"));
		}
	
	public void actionAssemble()
		{
//		CloneAssembler ass=
				new CloneAssembler();
		
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


	
	
	public void updateEvent(Object ob)
		{
		if(ob instanceof EventNewSequence)
			{
			AnnotatedSequence seq=((EventNewSequence) ob).seq;
			addSequenceToProject(seq);
			showSequence(seq);
			}
		else if(ob instanceof EventSequenceModified)
			{
			//Find views that might be interested
			AnnotatedSequence seq=((EventSequenceModified)ob).seq;
			for(SequenceWindow w:seqwindows)
				{
				if(w.getSequence()==seq)
					w.onViewUpdated(ob);
				}
			}

		}


	public void hasclosed(SequenceWindow sequenceWindow)
		{
		seqwindows.remove(sequenceWindow);
		}


	public MadgeneProject getProject()
		{
		return proj;
		}

	}
