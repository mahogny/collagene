package collagene.gui.sequenceWindow;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import collagene.gui.EventNewSequence;
import collagene.gui.IndexUtil;
import collagene.gui.ProjectWindow;
import collagene.gui.QtProgramInfo;
import collagene.gui.colors.ColorSet;
import collagene.gui.paneCircular.PaneCircularSequence;
import collagene.gui.paneInfo.PaneSequenceInfo;
import collagene.gui.paneLinear.PaneLinearSequence;
import collagene.gui.paneRestriction.PaneEnzymeList;
import collagene.gui.primer.FitPrimerWindow;
import collagene.gui.primer.PrimerPropertyWindow;
import collagene.gui.qt.QTutil;
import collagene.gui.resource.ImgResource;
import collagene.melting.CalcTmSanta98;
import collagene.melting.TmException;
import collagene.primer.Primer;
import collagene.primer.PrimerFinder;
import collagene.primer.PrimerFitter;
import collagene.seq.AnnotatedSequence;
import collagene.seq.Orientation;
import collagene.seq.RestrictionSite;
import collagene.seq.SeqAnnotation;
import collagene.seq.SeqColor;
import collagene.seq.SequenceRange;
import collagene.sequtil.CRISPRsuggester;
import collagene.sequtil.NucleotideUtil;
import collagene.sequtil.OrfFinder;

import com.trolltech.qt.core.QUrl;
import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QClipboard;
import com.trolltech.qt.gui.QCloseEvent;
import com.trolltech.qt.gui.QDesktopServices;
import com.trolltech.qt.gui.QHBoxLayout;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QLabel;
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
 * 
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
	private PaneLinearSequence viewOverviewLinear;
	private QStatusBar statusbar=new QStatusBar();
	private QLabel labelTm=new QLabel("");
	private QLabel labelGC=new QLabel("");
	private QLabel labelLength=new QLabel("");

	QHBoxLayout layc=new QHBoxLayout();

	private ProjectWindow projwindow;
	
	private AnnotatedSequence seq=new AnnotatedSequence();
	
	
	public void actionSelectAll()
		{
		SequenceRange range=new SequenceRange();
		range.from=0;
		range.to=seq.getLength();
		viewLinear.setSelection(range);
		viewOverviewLinear.setSelection(range);
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
		String s=seq.getSequence(getSelectionOrAll());
		cb.setText(s);
		}
	public void copyUpperRev()
		{
		QClipboard cb=QApplication.clipboard();
		String s=seq.getSequence(getSelectionOrAll());
		cb.setText(NucleotideUtil.reverse(s));
		}
	public void copyUpperRevComp()
		{
		QClipboard cb=QApplication.clipboard();
		String s=seq.getSequence(getSelectionOrAll());
		cb.setText(NucleotideUtil.revcomplement(s));
		}
	public void copyUpperComp()
		{
		QClipboard cb=QApplication.clipboard();
		String s=seq.getSequence(getSelectionOrAll());
		cb.setText(NucleotideUtil.complement(s));
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
	
	
	public SequenceRange getSelectionOrAll()
		{
		SequenceRange r=getSelection();
		if(r==null)
			return new SequenceRange(0,seq.getLength());
		else
			return r;
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
	 * Action: Fit annotation
	 */
	public void actionAutofitAnnotation()
		{
		FitAnnotationWindow w=new FitAnnotationWindow(projwindow,this);
		w.exec();
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
		//layc.removeWidget(viewCircular);
		if(seq.isCircular)
			{
			//layc.addWidget(viewCircular);
			viewCircular.show();
			viewOverviewLinear.hide();
			}
		else
			{
			viewCircular.hide();
			viewOverviewLinear.show();
			}

		//Brutal, works
		viewCircular.setSequence(seq);
		viewLinear.setSequence(seq);
		viewOverviewLinear.setSequence(seq);
		viewEnz.setSequence(seq);
		viewInfo.setSequence(seq);
		setWindowTitle(QtProgramInfo.programName + " - "+seq.name);
		projwindow.updateView();
		}

	/**
	 * Event: Whenever a selection is changed somewhere
	 */
	public void emitNewSelection(SequenceRange r)
		{
		onViewUpdated(new EventSelectedRegion(seq, r));
		}
	
	/**
	 * Event: For any kind of event
	 */
	public void onViewUpdated(CollageneEvent ob)
		{
		if(ob instanceof EventSequenceModified)
			{
			setSequence(seq);
			}
		else if(ob instanceof EventNewSequence)
			{
			projwindow.updateEvent(ob); //if it did not already go there?
			}
		else if(ob instanceof EventSelectedRegion)
			{
			SequenceRange range=((EventSelectedRegion) ob).range;
			if(range.isNoRange)
				range=null;
			viewLinear.setSelection(range);
			viewOverviewLinear.setSelection(range);
			viewCircular.setSelection(range);
			updateStatusbar();
			}
		else
			{
			if(ob instanceof EventSelectedAnnotation)
				{
				SeqAnnotation annot=((EventSelectedAnnotation) ob).annot;
				if(annot!=null)
					emitNewSelection(new SequenceRange(annot.range));
				}

			viewLinear.handleEvent(ob);
			viewOverviewLinear.handleEvent(ob);
			viewCircular.handleEvent(ob);
			viewEnz.handleEvent(ob);
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
		viewOverviewLinear=new PaneLinearSequence(projwindow);
		viewCircular=new PaneCircularSequence(projwindow);
		viewInfo=new PaneSequenceInfo(projwindow);
		this.projwindow=projwindow;
		
		viewOverviewLinear.setFullsizeMode(true);

		
		ImgResource.setWindowIcon(this);
		
		//Build menus
		QMenuBar menubar=new QMenuBar();

		
		QMenu mseq=menubar.addMenu(tr("Sequence"));		
		mseq.addAction(tr("Select everything"), this, "actionSelectAll()");
		mseq.addSeparator();
		mseq.addAction(tr("BLAST (NCBI)"), this, "blastNCBI()");
		mseq.addAction(tr("CRISPR design"), this, "crispr()");
		mseq.addAction(tr("Site directed mutagenesis"), this, "actionSDM()");
		mseq.addSeparator();
		mseq.addAction(tr("Reverse plasmid"), this, "actionReverseSequence()");
		mseq.addAction(tr("Set plasmid 0-position"), this, "actionSetSequence0()");
		mseq.addSeparator();
		mseq.addAction(tr("Close"), this, "close()");

		QMenu mannotation=menubar.addMenu(tr("Annotation"));
		mannotation.addAction(tr("Add annotation"), this, "addAnnotation()");
		mannotation.addAction(tr("Autofit annotation"), this, "actionAutofitAnnotation()");
		mannotation.addSeparator();
		mannotation.addAction(tr("Find ORFs"), this, "actionFindORFs()");
		mannotation.addAction(tr("Remove unnamed ORFs"), this, "actionRemoveORFs()");
		
		
		QMenu mprimer=menubar.addMenu(tr("Primers"));
		mprimer.addAction(tr("Add forward primer for selection"),this,"actionAddPrimerForward()");
		mprimer.addAction(tr("Add reverse primer for selection"),this,"actionAddPrimerReverse()");
		mprimer.addSeparator();
		mprimer.addAction(tr("Find good forward primer location in selection"),this,"actionFindPrimerForward()");
		mprimer.addAction(tr("Find good reverse primer location in selection"),this,"actionFindPrimerReverse()");
		mprimer.addSeparator();
		mprimer.addAction(tr("Copy primer sequences"),this,"actionPrimerClipboard()");
		mprimer.addAction(tr("Fit existing primer"),this,"actionFitExistingPrimer()");
		
		QMenu mCopy=menubar.addMenu(tr("Copy"));
		mCopy.addAction(tr("Copy upper 5-3' as-is"), this, "copyUpperOrig()");
		mCopy.addAction(tr("Copy upper 5-3' reversed"), this, "copyUpperRev()");
		mCopy.addAction(tr("Copy upper 5-3' complemented"), this, "copyUpperComp()");
		mCopy.addAction(tr("Copy upper 5-3' reverse-complemented"), this, "copyUpperRevComp()");
		mCopy.addSeparator();
		mCopy.addAction(tr("Copy lower 5-3' as-is"), this, "copyLowerOrig()");
		mCopy.addAction(tr("Copy lower 5-3' reversed"), this, "copyLowerRev()");
		mCopy.addAction(tr("Copy lower 5-3' complemented"), this, "copyLowerComp()");
		mCopy.addAction(tr("Copy lower 5-3' reverse-complemented"), this, "copyLowerRevComp()");

		QMenu mFind=menubar.addMenu(tr("Find"));
		mFind.addAction(tr("Go to next ambigous nucleotide"),this,"actionNextAmbnuc()");
		mFind.addAction(tr("Go to previous ambigous nucleotide"),this,"actionPrevAmbnuc()");
		mFind.addSeparator();
		mFind.addAction(tr("Go to next nucleotide mismatch"),this,"actionNextMismatch()");
		mFind.addAction(tr("Go to previous nucleotide mismatch"),this,"actionPrevMismatch()");
		
		
		viewLinear.signalUpdated.connect(this,"onViewUpdated(CollageneEvent)");
		viewOverviewLinear.signalUpdated.connect(this,"onViewUpdated(CollageneEvent)");
		viewEnz.signalUpdated.connect(this,"onViewUpdated(CollageneEvent)");
		viewCircular.signalUpdated.connect(this,"onViewUpdated(CollageneEvent)");
		viewInfo.signalUpdated.connect(this,"onViewUpdated(CollageneEvent)");

	
		QPushButton bPrimerNext=new QPushButton(new QIcon(ImgResource.moveRight),"");
		QPushButton bPrimerPrev=new QPushButton(new QIcon(ImgResource.moveLeft),"");
		bPrimerNext.clicked.connect(this,"actionPrimerNext()");
		bPrimerPrev.clicked.connect(this,"actionPrimerPrev()");

		SequenceWindowSeqSearch laySearch=new SequenceWindowSeqSearch(this);

		QHBoxLayout layPrimer=new QHBoxLayout();
		layPrimer.addWidget(new QLabel("  "+tr("Go to primer:")));
		layPrimer.addWidget(bPrimerPrev);
		layPrimer.addWidget(bPrimerNext);
		layPrimer.setMargin(0);
		layPrimer.setSpacing(0);

		QHBoxLayout layToolbar=new QHBoxLayout();
		layToolbar.addLayout(laySearch);
		layToolbar.addLayout(layPrimer);
		layToolbar.addStretch();
		layToolbar.setMargin(0);
		layToolbar.setSpacing(2);

		
		setMenuBar(menubar);
		
		setStatusBar(statusbar);
		statusbar.addPermanentWidget(labelTm);
		statusbar.addPermanentWidget(labelGC);
		statusbar.addPermanentWidget(labelLength);
		
		layc.addWidget(viewCircular);
		layc.addWidget(viewOverviewLinear);
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
		ImgResource.setWindowIcon(this);
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
	 * Action: Remove unnamed ORFs
	 */
	public void actionRemoveORFs()
		{
		OrfFinder.removeUnnamedOrfs(getSequence());
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
	 * Get primers, in sorted order (by start of range)
	 */
	private LinkedList<Primer> getSortedPrimers()
		{
		LinkedList<Primer> list=new LinkedList<Primer>();
		list.addAll(seq.primers);
		Collections.sort(list, new Comparator<Primer>()
			{
			public int compare(Primer a, Primer b)
				{
				return Integer.compare(a.getRange().from, b.getRange().from);
				}
			});
		return list;
		}
	public void actionNextMismatch()
		{
		goNext(classifyMismatch());
		}
	public void actionPrevMismatch()
		{
		goPrev(classifyMismatch());
		
		}


	/**
	 * Go to next true position in array
	 */
	private void goNext(boolean[] b)
		{
		int curpos=-1;
		SequenceRange r=getSelection();
		if(r!=null)
			curpos=r.from;
		for(int i=curpos+1;i<b.length;i++)
			{
			if(b[i])
				{
				emitNewSelection(new SequenceRange(i,i+1));
				return;
				}
			}
		for(int i=0;i<curpos;i++)
			{
			if(b[i])
				{
				emitNewSelection(new SequenceRange(i,i+1));
				return;
				}
			}
		QTutil.showNotice(this, tr("None found"));
		}
	
	/**
	 * Go to previous true position in array
	 */
	private void goPrev(boolean[] b)
		{
		int curpos=-1;
		SequenceRange r=getSelection();
		if(r!=null)
			curpos=r.from;
		String s=seq.getSequence();
		for(int i=curpos-1;i>=0;i--)
			{
			if(b[i])
				{
				emitNewSelection(new SequenceRange(i,i+1));
				return;
				}
			}
		for(int i=s.length()-1;i>curpos;i--)
			{
			if(b[i])
				{
				emitNewSelection(new SequenceRange(i,i+1));
				return;
				}
			}
		QTutil.showNotice(this, tr("None found"));
		}
	
	/**
	 * Classify nucleotides as ambiguous
	 */
	private boolean[] classifyAmbnuc()
		{
		String s=seq.getSequence();
		boolean[] arr=new boolean[seq.getLength()];
		for(int i=0;i<s.length();i++)
			{
			char c=s.charAt(i);
			arr[i]=!NucleotideUtil.isATGC(c) && c!=' ';
			}
		return arr;
		}

	/**
	 * Classify nucleotides as mismatches
	 * @return
	 */
	private boolean[] classifyMismatch()
		{
		String s=seq.getSequence();
		String s2=seq.getSequenceLower();
		boolean[] arr=new boolean[seq.getLength()];
		for(int i=0;i<s.length();i++)
			{
			char letterUpper=s.charAt(i);
			char letterLower=s2.charAt(i);
			arr[i] = !NucleotideUtil.areComplementary(letterUpper,letterLower) && (!NucleotideUtil.isSpacing(letterLower) && !NucleotideUtil.isSpacing(letterUpper));
			}
		return arr;
		}

	/**
	 * Go to next ambiguous nucleotide
	 */
	public void actionNextAmbnuc()
		{
		goNext(classifyAmbnuc());
		}
	
	/**
	 * Go to previous ambiguous nucleotide
	 */
	public void actionPrevAmbnuc()
		{
		goPrev(classifyAmbnuc());
		}

	/**
	 * Action: Select next primer
	 */
	public void actionPrimerNext()
		{
		LinkedList<Primer> list=getSortedPrimers();
		if(!list.isEmpty())
			{
			SequenceRange r=getSelection();
			if(r==null)
				emitNewSelection(list.get(0).getRange());
			else
				{
				for(Primer p:list)
					if(p.getRange().from>r.from)
						{
						emitNewSelection(p.getRange());
						return;
						}
				emitNewSelection(list.get(0).getRange());
				}
			}
		}
	
	/**
	 * Action: Select previous primer
	 */
	public void actionPrimerPrev()
		{
		LinkedList<Primer> list=getSortedPrimers();
		if(!list.isEmpty())
			{
			SequenceRange r=getSelection();
			if(r==null)
				emitNewSelection(list.get(0).getRange());
			else
				{
				for(int i=list.size()-1;i>=0;i--)
					{
					Primer p=list.get(i);
					if(p.getRange().from<r.from)
						{
						emitNewSelection(p.getRange());
						return;
						}
					}
				emitNewSelection(list.get(list.size()-1).getRange());
				}
			}
		}


	/**
	 * Action: Add primer in forward direction
	 */
	public void actionAddPrimerForward()
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
	public void actionAddPrimerReverse()
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
				emitNewSelection(p.getRange());
				}
			else
				QTutil.showNotice(this, tr("Failed to fit"));
			}
		}
	
	/**
	 * Find primer in selection
	 */
	public void actionFindPrimer(Orientation orientation)
		{
		if(getSelection()!=null)
			{
			PrimerFinder f=new PrimerFinder();
			f.run(getSequence(), getSelection(), orientation);
			
			if(!f.primerCandidates.isEmpty())
				{
				Primer p=f.primerCandidates.get(0).p;
				emitNewSelection(p.getRange());
				}
			else
				QTutil.showNotice(this, tr("Failed to find good primers"));
			}
		}
	
	public void actionFindPrimerForward()
		{
		actionFindPrimer(Orientation.FORWARD);
		}
	public void actionFindPrimerReverse()
		{
		actionFindPrimer(Orientation.REVERSE);
		}

	
	protected void closeEvent(QCloseEvent arg__1)
		{
		super.closeEvent(arg__1);
		projwindow.hasclosed(this);
		}

	}
