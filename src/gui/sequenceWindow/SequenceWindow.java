package gui.sequenceWindow;

import gui.IndexUtil;
import gui.ProjectWindow;
import gui.QtProgramInfo;
import gui.colors.ColorSet;
import gui.paneCircular.PaneCircularSequence;
import gui.paneInfo.PaneSequenceInfo;
import gui.paneLinear.PaneLinearSequence;
import gui.paneLinear.EventNewSequence;
import gui.paneRestriction.PaneEnzymeList;
import gui.paneRestriction.EventSelectedRestrictionEnzyme;
import gui.primer.FitPrimerWindow;
import gui.primer.PrimerPropertyWindow;
import gui.qt.QTutil;
import gui.resource.ImgResource;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.LinkedList;

import primer.Primer;
import primer.PrimerFinder;
import primer.PrimerFitter;
import melting.CalcTmSanta98;
import melting.TmException;
import seq.AnnotatedSequence;
import seq.Orientation;
import seq.RestrictionSite;
import seq.SeqAnnotation;
import seq.SeqColor;
import seq.SequenceRange;
import sequtil.CRISPRsuggester;
import sequtil.NucleotideUtil;
import sequtil.OrfFinder;

import com.trolltech.qt.core.QUrl;
import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QClipboard;
import com.trolltech.qt.gui.QCloseEvent;
import com.trolltech.qt.gui.QDesktopServices;
import com.trolltech.qt.gui.QHBoxLayout;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QLineEdit;
import com.trolltech.qt.gui.QMainWindow;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QMenuBar;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QResizeEvent;
import com.trolltech.qt.gui.QStatusBar;
import com.trolltech.qt.gui.QTabWidget;
import com.trolltech.qt.gui.QTextEdit;
import com.trolltech.qt.gui.QVBoxLayout;
import com.trolltech.qt.gui.QWidget;


/**
 * Window showing one sequence
 * 
 * @author Johan Henriksson
 *
 */
public class SequenceWindow extends QMainWindow
	{
	private PaneLinearSequence viewLinear;
	private PaneCircularSequence viewCircular;
	private PaneEnzymeList viewEnz;
	private PaneSequenceInfo viewInfo;
	private QLineEdit tfSearch=new QLineEdit();
	private QStatusBar statusbar=new QStatusBar();
	private QLabel labelTm=new QLabel("");
	private QLabel labelGC=new QLabel("");
	private QLabel labelLength=new QLabel("");

	QHBoxLayout layc=new QHBoxLayout();

	private ProjectWindow projwindow;
	
	private AnnotatedSequence seq=new AnnotatedSequence();
	public SequenceSearcher currentSearchString=null;
	
	
	public void actionSelectAll()
		{
		SequenceRange range=new SequenceRange();
		range.from=0;
		range.to=seq.getLength();
		viewLinear.setSelection(range);
		viewCircular.setSelection(range);
		updateStatusbar();
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
	 * Action: Site directed mutagenesis
	 */
	public void actionSDM()
		{
		if(!seq.isCircular)
			QTutil.showNotice(this, tr("Can only perform site directed mutagenesis on circular plasmids"));
		else
			{
			SequenceRange r=getSelection();
			if(r!=null)
				{
				SDMWindow w=new SDMWindow(seq, r);
				w.exec();
				if(w.wasOk)
					{
					projwindow.updateEvent(new EventSequenceModified(seq));
					projwindow.updateEvent(new EventNewSequence(w.getCand().newseq));
//					setSequence(seq); //Would be better to emit a signal
					}
				}
			else
				errNoSelection();
			}
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
			errNoSelection();
		}
	public void copyUpperRev()
		{
		QClipboard cb=QApplication.clipboard();
		String s=getSelectionSequence();
		if(s!=null)
			cb.setText(NucleotideUtil.reverse(s));
		else
			errNoSelection();
		}
	public void copyUpperRevComp()
		{
		QClipboard cb=QApplication.clipboard();
		String s=getSelectionSequence();
		if(s!=null)
			cb.setText(NucleotideUtil.revcomplement(s));
		else
			errNoSelection();
		}
	public void copyUpperComp()
		{
		QClipboard cb=QApplication.clipboard();
		String s=getSelectionSequence();
		if(s!=null)
			cb.setText(NucleotideUtil.complement(s));
		else
			errNoSelection();
		}
	
	
	
	public void copyLowerOrig()
		{
		QClipboard cb=QApplication.clipboard();
		String s=NucleotideUtil.reverse(getSelectionSequenceLower());
		if(s!=null)
			cb.setText(s);
		else
			errNoSelection();
		}
	public void copyLowerRev()
		{
		QClipboard cb=QApplication.clipboard();
		String s=NucleotideUtil.reverse(getSelectionSequenceLower());
		if(s!=null)
			cb.setText(NucleotideUtil.reverse(s));
		else
			errNoSelection();
		}
	public void copyLowerRevComp()
		{
		QClipboard cb=QApplication.clipboard();
		String s=NucleotideUtil.reverse(getSelectionSequenceLower());
		if(s!=null)
			cb.setText(NucleotideUtil.revcomplement(s));
		else
			errNoSelection();
		}
	public void copyLowerComp()
		{
		QClipboard cb=QApplication.clipboard();
		String s=NucleotideUtil.reverse(getSelectionSequenceLower());
		if(s!=null)
			cb.setText(NucleotideUtil.complement(s));
		else
			errNoSelection();
		}
	private void errNoSelection()
		{
		QTutil.showNotice(this, tr("No selection made"));
		}
	
	
	public SequenceRange getSelection()
		{
		return viewLinear.getSelection();
		}
	public String getSelectionSequence()
		{
		SequenceRange s=getSelection();
		if(s!=null)
			return seq.getSequence(s);
		else
			return null;
		}
	public String getSelectionSequenceLower()
		{
		SequenceRange s=getSelection();
		if(s!=null)
			return seq.getSequenceLower(s);
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
		a.color=new SeqColor(ColorSet.colorset.getRandomColor());
		SequenceRange r=getSelection();
		if(r!=null)
			{
			r=r.toNormalizedRange(getSequence());
			a.setRange(r);
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
		Collection<RestrictionSite> sites=projwindow.restrictionEnzymes.findRestrictionSites(seq);
		seq.restrictionSites.clear();
		for(RestrictionSite s:sites)
			seq.addRestrictionSite(s);
		
		updateSequence();
		}
	
	public void updateSequence()
		{
		layc.removeWidget(viewCircular);
		if(seq.isCircular)
			{
			layc.addWidget(viewCircular);
			viewCircular.show();
			}
		else
			viewCircular.hide();

		//Brutal, works
		viewCircular.setSequence(seq);
		viewLinear.setSequence(seq);
		viewEnz.setSequence(seq);
		viewInfo.setSequence(seq);
		setWindowTitle(QtProgramInfo.programName+" - "+seq.name);
		projwindow.updateView();
		}
	
	/**
	 * Event: Whenever a selection is changed somewhere
	 */
	public void onViewUpdated(Object ob)
		{
		if(ob instanceof EventSequenceModified)
			{
			setSequence(seq);
			}
		else if(ob instanceof EventNewSequence)
			{
			projwindow.updateEvent(ob); //if it did not already go there?
			}
		else if(ob instanceof SequenceRange)
			{
			SequenceRange range=(SequenceRange)ob;
			if(range.isNoRange)
				range=null;
			viewLinear.setSelection(range);
			viewCircular.setSelection(range);
			updateStatusbar();
			}
		else if(ob instanceof EventSelectedRestrictionEnzyme)
			{
			EventSelectedRestrictionEnzyme enz=(EventSelectedRestrictionEnzyme)ob;
			viewLinear.setRestrictionEnzyme(enz);
			viewCircular.setRestrictionEnzyme(enz);
			viewEnz.setRestrictionEnzyme(enz);
			}
		else
			{
			if(ob instanceof EventSelectedAnnotation)
				{
				SeqAnnotation annot=((EventSelectedAnnotation) ob).annot;
				if(annot!=null)
					onViewUpdated(new SequenceRange(annot.range));
				}

			viewLinear.handleEvent(ob);
			}
		}
	

	
	/**
	 * Update status bar
	 */
	public void updateStatusbar()
		{
		labelTm.setText("");
		labelGC.setText("");
		labelLength.setText("");
		SequenceRange range=getSelection();
		if(range!=null)
			{
			range=range.toNormalizedRange(seq);
			//Update Tm
			String sub=seq.getSequence(range);
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
			labelLength.setText(
					"From: "+IndexUtil.fromTogui(range.from)+
					"  To: "+IndexUtil.toTogui(range.to)+
					"  Length: "+range.getSize(getSequence())+" bp");
			}
		}
	
	
	/**
	 * Constructor
	 */
	public SequenceWindow(ProjectWindow projwindow)
		{
		viewEnz=new PaneEnzymeList(this, projwindow);
		viewLinear=new PaneLinearSequence(projwindow);
		viewCircular=new PaneCircularSequence(projwindow);
		viewInfo=new PaneSequenceInfo(projwindow);
		this.projwindow=projwindow;
		

		
		ImgResource.setWindowIcon(this);
		
		//Build menus
		QMenuBar menubar=new QMenuBar();
		
		QMenu mseq=menubar.addMenu(tr("Sequence"));
		QMenu mannotation=menubar.addMenu(tr("Annotation"));
		
		mseq.addAction(tr("Select everything"), this, "actionSelectAll()");
		mseq.addSeparator();
		mseq.addAction(tr("Copy upper 5-3' as-is"), this, "copyUpperOrig()");
		mseq.addAction(tr("Copy upper 5-3' reversed"), this, "copyUpperRev()");
		mseq.addAction(tr("Copy upper 5-3' complemented"), this, "copyUpperComp()");
		mseq.addAction(tr("Copy upper 5-3' reverse-complemented"), this, "copyUpperRevComp()");
		mseq.addSeparator();
		mseq.addAction(tr("Copy lower 5-3' as-is"), this, "copyLowerOrig()");
		mseq.addAction(tr("Copy lower 5-3' reversed"), this, "copyLowerRev()");
		mseq.addAction(tr("Copy lower 5-3' complemented"), this, "copyLowerComp()");
		mseq.addAction(tr("Copy lower 5-3' reverse-complemented"), this, "copyLowerRevComp()");
		mseq.addSeparator();
		mseq.addAction(tr("BLAST (NCBI)"), this, "blastNCBI()");
		mseq.addAction(tr("CRISPR design"), this, "crispr()");
		mseq.addAction(tr("Site directed mutagenesis"), this, "actionSDM()");
		mseq.addSeparator();
		mseq.addAction(tr("Reverse plasmid"), this, "actionReverseSequence()");
		mseq.addAction(tr("Set plasmid 0-position"), this, "actionSetSequence0()");
		mseq.addSeparator();
		mseq.addAction(tr("Close"), this, "close()");

		mannotation.addAction(tr("Add annotation"), this, "addAnnotation()");
		mannotation.addSeparator();
		mannotation.addAction(tr("Find ORFs"), this, "actionFindORFs()");
		mannotation.addSeparator();
		mannotation.addAction(tr("Add forward primer for selection"),this,"actionAddPrimerFWD()");
		mannotation.addAction(tr("Add reverse primer for selection"),this,"actionAddPrimerREV()");
		mannotation.addAction(tr("Copy primer sequences"),this,"actionPrimerClipboard()");
		mannotation.addAction(tr("Fit existing primer"),this,"actionFitExistingPrimer()");
		mannotation.addAction(tr("Find good primer location in selection"),this,"actionFindPrimer()");
		
		
		
		viewLinear.signalUpdated.connect(this,"onViewUpdated(Object)");
		viewEnz.signalUpdated.connect(this,"onViewUpdated(Object)");
		viewCircular.signalUpdated.connect(this,"onViewUpdated(Object)");
		viewInfo.signalUpdated.connect(this,"onViewUpdated(Object)");

		QPushButton bSearchNext=new QPushButton(new QIcon(ImgResource.moveRight),"");
		QPushButton bSearchPrev=new QPushButton(new QIcon(ImgResource.moveLeft),"");
		tfSearch.textChanged.connect(this,"actionSearch()");
		tfSearch.returnPressed.connect(this,"actionSearchNext()");
		bSearchNext.clicked.connect(this,"actionSearchNext()");
		bSearchPrev.clicked.connect(this,"actionSearchPrev()");

		QHBoxLayout laySearch=new QHBoxLayout();
		laySearch.addWidget(new QLabel(tr("Search:")));
		laySearch.addWidget(tfSearch);
		laySearch.addWidget(bSearchPrev);
		laySearch.addWidget(bSearchNext);
		laySearch.setMargin(0);
		laySearch.setSpacing(0);

		QHBoxLayout layToolbar=new QHBoxLayout();
		layToolbar.addLayout(laySearch);
		layToolbar.addStretch();
		layToolbar.setMargin(0);
		layToolbar.setSpacing(2);

		
		setMenuBar(menubar);
		
		setStatusBar(statusbar);
		statusbar.addPermanentWidget(labelTm);
		statusbar.addPermanentWidget(labelGC);
		statusbar.addPermanentWidget(labelLength);
		
		layc.setMargin(0);
		layc.setSpacing(0);
		
		QHBoxLayout hlay=new QHBoxLayout();
		hlay.addWidget(viewLinear);
		hlay.addLayout(layc);
		hlay.addWidget(viewEnz);
		hlay.setMargin(0);


		QVBoxLayout vlay=new QVBoxLayout();
		vlay.addLayout(layToolbar);
		vlay.addLayout(hlay);
		laySearch.setSpacing(2);
		laySearch.setMargin(1);
		
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
	 * Action: Find ORFs
	 */
	public void actionFindORFs()
		{
		OrfFinder f=new OrfFinder();
		LinkedList<SeqAnnotation> annots=f.find(getSequence());
		int i=1;
		for(SeqAnnotation a:annots)
			{
			a.name="ORF "+i;
			a.color=ColorSet.colorset.getRandomColor();
			getSequence().addAnnotation(a);
			i++;
			}
		setSequence(seq);
		}

	/**
	 * Get the sequence
	 */
	public AnnotatedSequence getSequence()
		{
		return seq;
		}
	
	
	/**
	 * Handle resize events
	 */
	public void resizeEvent(QResizeEvent event)
		{
		super.resizeEvent(event);
		viewCircular.updatecirc();
		}

	
	
	/**
	 * Action: Reverse the sequence
	 */
	public void actionReverseSequence()
		{
		seq.reverseSequence();
		setSequence(seq);
		}
	
	
	/**
	 * Action: Set a new 0-point for the vector
	 */
	public void actionSetSequence0()
		{
		if(getSequence().isCircular)
			{
			SequenceRange r=getSelection();
			if(r!=null)
				{
				seq.setNew0(r.from);
				setSequence(seq);
				System.out.println("here");
				}
			else
				QTutil.showNotice(this, tr("Need to select position first"));
			}
		else
			QTutil.showNotice(this, tr("Can only move 0-position on circular plasmids"));
			
		}

	/**
	 * Action: Perform a new search
	 */
	public void actionSearch()
		{
		if(tfSearch.text().length()==0)
			currentSearchString=null;
		else
			currentSearchString=new SequenceSearcher(getSequence(), tfSearch.text().toUpperCase());
		onViewUpdated(SequenceRange.getNoRange());
		actionSearchNext();
		}
	
	/**
	 * Action: Go to next search position
	 */
	public void actionSearchNext()
		{
		if(currentSearchString!=null)
			{
			SequenceRange r=currentSearchString.next(getSelection());
			onViewUpdated(r);
			}
		}

	/**
	 * Action: Go to next search position
	 */
	public void actionSearchPrev()
		{
		if(currentSearchString!=null)
			{
			SequenceRange r=currentSearchString.prev(getSelection());
			onViewUpdated(r);
			}
		}

	/**
	 * Action: Add primer in forward direction
	 */
	public void actionAddPrimerFWD()
		{
		SequenceRange r=getSelection();
		if(r!=null)
			{
			Primer p=new Primer();
			p.sequence=getSequence().getSequence(r);
			p.orientation=Orientation.FORWARD;
			p.targetPosition=r.to;
			editprimerandadd(p);
			}
		}

	/**
	 * Action: Add primer in reverse direction
	 */
	public void actionAddPrimerREV()
		{
		SequenceRange r=getSelection();
		if(r!=null)
			{
			Primer p=new Primer();
			p.sequence=NucleotideUtil.revcomplement(getSequence().getSequence(r));
			p.orientation=Orientation.REVERSE;
			p.targetPosition=r.from;
			editprimerandadd(p);
			}
		}
	
	private void editprimerandadd(Primer p)
		{
		PrimerPropertyWindow w=new PrimerPropertyWindow();
		w.setPrimer(p);
		w.exec();
		if(w.getPrimer()!=null)
			{
			getSequence().addPrimer(w.getPrimer());
			setSequence(seq);
			}
		}
	
	/**
	 * Action: Copy all primer sequences into clipboard, in an order friendly format
	 */
	public void actionPrimerClipboard()
		{
		StringBuilder sb=new StringBuilder();
		for(Primer p:getSequence().primers)
			sb.append(p.name+"\t"+p.sequence+"\n");
		QClipboard cb=QApplication.clipboard();
		cb.setText(sb.toString());
		QTutil.showNotice(this, tr("Primers have been pasted into clipboard"));
		}
	
	
	/**
	 * Fit an existing primer
	 */
	public void actionFitExistingPrimer()
		{
		FitPrimerWindow w=new FitPrimerWindow(projwindow);
		w.exec();
		if(w.wasOk)
			{
			Primer selp=w.getPrimer();
			AnnotatedSequence seq=getSequence();
			PrimerFitter f=new PrimerFitter();
			f.run(seq, selp.name, selp.sequence);
			Primer p=f.getBestPrimer();
			if(p!=null)
				{
				seq.primers.add(p);
				updateSequence();
				onViewUpdated(p.getRange());
				}
			else
				QTutil.showNotice(this, tr("Failed to fit"));
			}
		}
	
	/**
	 * Find primer in selection
	 */
	public void actionFindPrimer()
		{
		if(getSelection()!=null)
			{
			PrimerFinder f=new PrimerFinder();
			f.run(getSequence(), getSelection());
			
			if(!f.primerCandidates.isEmpty())
				{
				Primer p=f.primerCandidates.get(0).p;
				onViewUpdated(p.getRange());
				}
			else
				QTutil.showNotice(this, tr("Failed to find good primers"));
			}
		}
	
	
	protected void closeEvent(QCloseEvent arg__1)
		{
		super.closeEvent(arg__1);
		projwindow.hasclosed(this);
		}

	}
