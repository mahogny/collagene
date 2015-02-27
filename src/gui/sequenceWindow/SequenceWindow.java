package gui.sequenceWindow;

import gui.ProjectWindow;
import gui.QtProgramInfo;
import gui.paneCircular.PaneCircularSequence;
import gui.paneLinear.PaneLinearSequence;
import gui.paneRestriction.PaneEnzymeList;
import gui.qt.QTutil;
import gui.resource.ImgResource;

import java.text.NumberFormat;
import java.util.Collection;

import melting.CalcTmSanta98;
import melting.TmException;
import seq.AnnotatedSequence;
import seq.RestrictionSite;
import seq.SeqAnnotation;
import seq.SequenceRange;
import sequtil.CRISPRsuggester;
import sequtil.NucleotideUtil;

import com.trolltech.qt.core.QUrl;
import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QClipboard;
import com.trolltech.qt.gui.QDesktopServices;
import com.trolltech.qt.gui.QHBoxLayout;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QMainWindow;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QMenuBar;
import com.trolltech.qt.gui.QResizeEvent;
import com.trolltech.qt.gui.QStatusBar;
import com.trolltech.qt.gui.QTabWidget;
import com.trolltech.qt.gui.QTextEdit;
import com.trolltech.qt.gui.QVBoxLayout;
import com.trolltech.qt.gui.QWidget;


/**
 * The main window
 * 
 * @author Johan Henriksson
 *
 */
public class SequenceWindow extends QMainWindow
	{
//	private File lastDirectory=new File(".");

//	private RestrictionEnzymeSet restrictionEnzymes=new RestrictionEnzymeSet();

	private PaneLinearSequence viewLinear=new PaneLinearSequence();
	private PaneCircularSequence viewCircular=new PaneCircularSequence();
//	private PaneProteinTranslation viewProtein=new PaneProteinTranslation();
	private PaneEnzymeList viewEnz;
	private PaneInfo viewInfo=new PaneInfo();
	
	ProjectWindow projwindow;
	
	private AnnotatedSequence seq=new AnnotatedSequence();
	
	
	private QStatusBar statusbar=new QStatusBar();

	
	private QLabel labelTm=new QLabel("");
	private QLabel labelGC=new QLabel("");
	private QLabel labelLength=new QLabel("");
		
	
	public void actionSelectAll()
		{
		SequenceRange range=new SequenceRange();
		range.from=0;
		range.to=seq.getLength();
		viewLinear.setSelection(range);
		viewCircular.setSelection(range);
		updateTm();
		}
	
	
	public void blastNCBI()
		{
		String sel=getSelectionSequence();
		if(sel!=null)
			{
			QUrl url=new QUrl("http://blast.ncbi.nlm.nih.gov/Blast.cgi?PAGE_TYPE=BlastSearch&"
					+ "USER_FORMAT_DEFAULTS=on&PAGE=MegaBlast&PROGRAM=blastn&"
					+ "QUERY="+sel+"&"
					+ "JOB_TITLE=quick+blast&"
					+ "GAPCOSTS=0%25200"
					+ "&MATCH_SCORES=1%2C-2&"
					+ "DATABASE=nr&"
					+ "BLAST_PROGRAMS=megaBlast&"
					+ "MAX_NUM_SEQ=100&SHORT_QUERY_ADJUST=on&"
					+ "EXPECT=10&"
					+ "WORD_SIZE=28&"
					+ "REPEATS=repeat_9606&"
					+ "TEMPLATE_TYPE=0&"
					+ "TEMPLATE_LENGTH=0&"
					+ "FILTER=m&"
					+ "SHOW_OVERVIEW=on&"
					+ "SHOW_LINKOUT=on&"
					+ "ALIGNMENT_VIEW=Pairwise&"
					+ "MASK_CHAR=2&"
					+ "MASK_COLOR=1&"
					+ "GET_SEQUENCE=on&"
					+ "NUM_OVERVIEW=100&"
					+ "DESCRIPTIONS=100&"
					+ "ALIGNMENTS=100&"
					+ "FORMAT_OBJECT=Alignment&"
					+ "FORMAT_TYPE=HTML&"
					+ "OLD_BLAST=false");
			QDesktopServices.openUrl(url);
			}
		else
			QTutil.showNotice(this, tr("Select sequence first"));
		}

	
	/**
	 * Action: CRISPR design
	 */
	public void crispr()
		{
		QWidget w=new QWidget();
		QHBoxLayout lay=new QHBoxLayout();
		w.setLayout(lay);
		QTextEdit te=new QTextEdit();
		String log=CRISPRsuggester.suggestCrispr(seq.getSequence());
		te.setPlainText(log);
		lay.addWidget(te);
		w.resize(800, 600);
		w.show();
		}
	
	/**
	 * Action: copy
	 */
	public void copyUpperOrig()
		{
		QClipboard cb=QApplication.clipboard();
		String s=getSelectionSequence();
		if(s!=null)
			cb.setText(s);
		else
			errNothingtocopy();
		}
	public void copyUpperRev()
		{
		QClipboard cb=QApplication.clipboard();
		String s=getSelectionSequence();
		if(s!=null)
			cb.setText(NucleotideUtil.reverse(s));
		else
			errNothingtocopy();
		}
	public void copyUpperRevComp()
		{
		QClipboard cb=QApplication.clipboard();
		String s=getSelectionSequence();
		if(s!=null)
			cb.setText(NucleotideUtil.revcomplement(s));
		else
			errNothingtocopy();
		}
	public void copyUpperComp()
		{
		QClipboard cb=QApplication.clipboard();
		String s=getSelectionSequence();
		if(s!=null)
			cb.setText(NucleotideUtil.complement(s));
		else
			errNothingtocopy();
		}
	
	
	
	public void copyLowerOrig()
		{
		QClipboard cb=QApplication.clipboard();
		String s=NucleotideUtil.reverse(getSelectionSequenceLower());
		if(s!=null)
			cb.setText(s);
		else
			errNothingtocopy();
		}
	public void copyLowerRev()
		{
		QClipboard cb=QApplication.clipboard();
		String s=NucleotideUtil.reverse(getSelectionSequenceLower());
		if(s!=null)
			cb.setText(NucleotideUtil.reverse(s));
		else
			errNothingtocopy();
		}
	public void copyLowerRevComp()
		{
		QClipboard cb=QApplication.clipboard();
		String s=NucleotideUtil.reverse(getSelectionSequenceLower());
		if(s!=null)
			cb.setText(NucleotideUtil.revcomplement(s));
		else
			errNothingtocopy();
		}
	public void copyLowerComp()
		{
		QClipboard cb=QApplication.clipboard();
		String s=NucleotideUtil.reverse(getSelectionSequenceLower());
		if(s!=null)
			cb.setText(NucleotideUtil.complement(s));
		else
			errNothingtocopy();
		}
	private void errNothingtocopy()
		{
		QTutil.showNotice(this, tr("Nothing to copy"));
		}
	
	
	private SequenceRange getSelection()
		{
		return viewLinear.getSelection();
		}
	private String getSelectionSequence()
		{
		SequenceRange s=getSelection();
		if(s!=null)
			return seq.getSubsequence(s);
		else
			return null;
		}
	private String getSelectionSequenceLower()
		{
		SequenceRange s=getSelection();
		if(s!=null)
			return seq.getSubsequenceLower(s);
		else
			return null;
		}

	
	/**
	 * Action: Add annotation
	 */
	public void addAnnotation()
		{
		AnnotationWindow w=new AnnotationWindow();
		SeqAnnotation a=new SeqAnnotation();
		SequenceRange r=getSelection();
		if(r!=null)
			{
			a.from=r.getLower();
			a.to=r.getUpper();
			}
		w.setAnnotation(a);
		w.exec();
		if(w.getAnnotation()!=null)
			{
			seq.annotations.add(w.getAnnotation());
			updateSequence();
			}
		}
	
	
	/**
	 * Set current sequence
	 */
	public void setSequence(AnnotatedSequence seq)
		{
		this.seq=seq;
		
		//Find all restriction sites
		Collection<RestrictionSite> sites=projwindow.restrictionEnzymes.findRestrictionSites(seq.getSequence());
		for(RestrictionSite s:sites)
			seq.addRestrictionSite(s);

		updateSequence();
		}
	
	public void updateSequence()
		{
		//Brutal, works
		viewCircular.setSequence(seq);
		viewLinear.setSequence(seq);
	//	viewProtein.setSequence(seq);
		viewEnz.setSequence(seq);
		viewInfo.setSequence(seq);
		setWindowTitle(QtProgramInfo.programName+" - "+seq.name);
		}
	
	/**
	 * Event: Whenever a selection is changed somewhere
	 */
	public void onSelectionChanged(SequenceRange range)
		{
		viewLinear.setSelection(range);
		viewCircular.setSelection(range);
		updateTm();
		}
	
	/**
	 * Update Tm
	 */
	public void updateTm()
		{
		labelTm.setText("");
		labelGC.setText("");
		labelLength.setText("");
		SequenceRange range=getSelection();
		if(range!=null)
			{
			//Update Tm
			String sub=seq.getSubsequence(range);
			try
				{
				NumberFormat nf=NumberFormat.getNumberInstance();
				nf.setMaximumFractionDigits(1);
				nf.setMinimumFractionDigits(1);
				CalcTmSanta98 tmcalc=new CalcTmSanta98();
				double tm=tmcalc.calcTm(sub, NucleotideUtil.complement(sub));
				labelTm.setText("Tm: "+nf.format(tm)+"C");
				}
			catch (TmException e)
				{
				labelTm.setText("Tm: ?");
				}

			//Update GC%
			if(sub.length()>0)
				{
				NumberFormat nf=NumberFormat.getNumberInstance();
				nf.setMaximumFractionDigits(0);
				nf.setMinimumFractionDigits(0);
				int gc=NucleotideUtil.countGC(sub);
				labelGC.setText("GC: "+nf.format((double)gc*100/sub.length())+"%");
				}
			
			//Update region
			labelLength.setText("From: "+range.getLower()+"  To: "+range.getUpper()+"  Length: "+range.getSize()+" bp");
			}
		}
	
	
	/**
	 * Constructor
	 */
	public SequenceWindow(ProjectWindow projwindow)
		{
		viewEnz=new PaneEnzymeList(projwindow);
		this.projwindow=projwindow;
		
		ImgResource.setWindowIcon(this);
		

//		this.restrictionEnzymes=projwindow.restrictionEnzymes;
		
		//Build menus
		QMenuBar menubar=new QMenuBar();
		
		QMenu mseq=menubar.addMenu("Sequence");
		QMenu mcopy=menubar.addMenu(tr("Copy"));
		
		
		
		mcopy.addAction("Copy upper 5-3' as-is", this, "copyUpperOrig()");
		mcopy.addAction("Copy upper 5-3' reversed", this, "copyUpperRev()");
		mcopy.addAction("Copy upper 5-3' complemented", this, "copyUpperComp()");
		mcopy.addAction("Copy upper 5-3' reverse-complemented", this, "copyUpperRevComp()");
		mcopy.addSeparator();
		mcopy.addAction("Copy lower 5-3' as-is", this, "copyLowerOrig()");
		mcopy.addAction("Copy lower 5-3' reversed", this, "copyLowerRev()");
		mcopy.addAction("Copy lower 5-3' complemented", this, "copyLowerComp()");
		mcopy.addAction("Copy lower 5-3' reverse-complemented", this, "copyLowerRevComp()");
		
		mseq.addAction(tr("Select everything"), this, "actionSelectAll()");
		mseq.addSeparator();
		mseq.addAction("BLAST (NCBI)", this, "blastNCBI()");
		mseq.addAction("Add annotation", this, "addAnnotation()");
		mseq.addAction("CRISPR design", this, "crispr()");
//		mseq.addAction("Import protein as random DNA", this, "importProteinDNA()");
		mseq.addSeparator();
		mseq.addAction(tr("Close"), this, "close()");

		viewLinear.signalSelectionChanged.connect(this,"onSelectionChanged(SequenceRange)");
		viewLinear.signalUpdated.connect(this,"updateSequence()");
		viewInfo.signalUpdated.connect(this,"updateSequence()");
		
		
		setMenuBar(menubar);
		
		setStatusBar(statusbar);
		statusbar.addPermanentWidget(labelTm);
		statusbar.addPermanentWidget(labelLength);
		
		/*
		try
			{
			seq=ImportGenbank.loadFile(new File("/home/mahogny/Dropbox/benchling/benchling_export.gb"));
			}
		catch (IOException e)
			{
			e.printStackTrace();
			}
			*/
		
		
		
		
		
//		viewProtein.setMinimumHeight(90);
		
		QHBoxLayout lay=new QHBoxLayout();
		lay.addWidget(viewLinear);
		lay.addWidget(viewCircular);
		lay.addWidget(viewEnz);
		

		
		QVBoxLayout vlay=new QVBoxLayout();
		vlay.addLayout(lay);
		//vlay.addWidget(viewProtein);
		
		
		resize(1200, 600);
		
		AnnotatedSequence seq=new AnnotatedSequence();
		setSequence(seq);
		viewCircular.updatecirc();
		show();

		
		QWidget wleft=new QWidget();
		wleft.setLayout(vlay);
		
		QTabWidget tabw=new QTabWidget();
		tabw.addTab(wleft, tr("Sequence"));
		tabw.addTab(viewInfo, tr("Summary"));

		setCentralWidget(tabw);
		}
	
	
	/**
	 * Handle resize events
	 */
	public void resizeEvent(QResizeEvent event)
		{
		super.resizeEvent(event);
		viewCircular.updatecirc();
		}

	
	
	}
