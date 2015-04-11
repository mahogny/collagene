package collagene.gui.paneInfo;

import java.util.HashMap;
import java.util.LinkedList;

import collagene.bacteria.BacteriaProperty;
import collagene.gui.ProjectWindow;
import collagene.gui.primer.MenuPrimer;
import collagene.gui.qt.QTutil;
import collagene.gui.sequenceWindow.CollageneEvent;
import collagene.gui.sequenceWindow.EventSequenceModified;
import collagene.gui.sequenceWindow.MenuAnnotation;
import collagene.melting.CalcTmSanta98;
import collagene.primer.Primer;
import collagene.seq.AnnotatedSequence;
import collagene.seq.SeqAnnotation;
import collagene.seq.SequenceRange;
import collagene.sequtil.NucleotideUtil;

import com.trolltech.qt.QSignalEmitter;
import com.trolltech.qt.core.Qt.CheckState;
import com.trolltech.qt.core.Qt.TextInteractionFlag;
import com.trolltech.qt.gui.QCheckBox;
import com.trolltech.qt.gui.QGroupBox;
import com.trolltech.qt.gui.QHBoxLayout;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QLineEdit;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QScrollArea;
import com.trolltech.qt.gui.QTextEdit;
import com.trolltech.qt.gui.QVBoxLayout;
import com.trolltech.qt.gui.QWidget;

/**
 * 
 * Pane: Information/overview of sequence
 * 
 * @author Johan Henriksson
 *
 */
public class PaneSequenceInfo extends QScrollArea
	{
	private QLineEdit tfName=new QLineEdit();
	private QCheckBox cbIsCircular=new QCheckBox();
	private QTextEdit tfNotes=new QTextEdit();
	private QLabel tfLength=new QLabel();
	private QLabel tfWeight=new QLabel();
	
	private ProjectWindow pw;
	public QSignalEmitter.Signal1<CollageneEvent> signalUpdated=new Signal1<CollageneEvent>();
	
	private AnnotatedSequence seq=new AnnotatedSequence();
	
	private boolean isUpdating=false;

	private QVBoxLayout layprimers=new QVBoxLayout();
	private QVBoxLayout layannot=new QVBoxLayout();
	private HashMap<Primer, PanePrimer> listPrimers=new HashMap<Primer, PaneSequenceInfo.PanePrimer>();
			
	private HashMap<SeqAnnotation, PaneAnnotation> listAnnot=new HashMap<SeqAnnotation, PaneAnnotation>();

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * One primer
	 */
	public class PanePrimer extends QGroupBox
		{
		Primer primer;
		QLabel lName=new QLabel();
		QLabel lSequence=new QLabel();
		QPushButton bAction=new QPushButton(tr("Action"));

		
		public PanePrimer()
			{
			setTitle(tr("Primer"));
			
			lName.setTextInteractionFlags(TextInteractionFlag.TextSelectableByMouse);
			lSequence.setTextInteractionFlags(TextInteractionFlag.TextSelectableByMouse);
			
			QVBoxLayout lay=new QVBoxLayout();
			lay.addWidget(lName);
			lay.addWidget(lSequence);
			lay.setMargin(0);

			QHBoxLayout hlay=new QHBoxLayout();
			hlay.addLayout(lay);
			hlay.addWidget(bAction);
			hlay.setMargin(0);
			setLayout(hlay);

			bAction.clicked.connect(this,"action()");
			}
		
		
		public void setPrimer(Primer p)
			{
			primer=p;
			String tm=MenuPrimer.tryCalcTm(new CalcTmSanta98(), p.sequence);
			String partmatch=p.getFirstMatchingSequencePart(seq);
			boolean fullmatch=partmatch.length()==p.length();
			SequenceRange range=p.getRange();
			lName.setText(primer.name+", "+primer.length()+" bp ("+range.from+".."+range.to+"), Tm: "+tm);
			lSequence.setText(NucleotideUtil.format3(primer.sequence)+(fullmatch?"":" (First matching part: ..."+NucleotideUtil.format3(partmatch)+")"));
			} 
		
		
		public void action()
			{
			MenuPrimer mPopup=new MenuPrimer(pw, seq, primer, false);
			mPopup.exec(mapToGlobal(bAction.pos()));
			}
		
		}

	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * One annotation
	 */
	public class PaneAnnotation extends QGroupBox
		{
		SeqAnnotation annot;
		QLabel lName=new QLabel();
		QPushButton bAction=new QPushButton(tr("Action"));

		
		public PaneAnnotation()
			{
			setTitle(tr("Annotation"));

			lName.setTextInteractionFlags(TextInteractionFlag.TextSelectableByMouse);

			QVBoxLayout lay=new QVBoxLayout();
			lay.addWidget(lName);
			lay.setMargin(0);

			QHBoxLayout hlay=new QHBoxLayout();
			hlay.addLayout(lay);
			hlay.addWidget(bAction);
			hlay.setMargin(0);
			setLayout(hlay);

			bAction.clicked.connect(this,"action()");
			}
		
		
		public void setAnnotation(SeqAnnotation annot)
			{
			this.annot=annot;
			lName.setText(annot.name+", "+annot.range.from+".."+annot.range.to+" ("+annot.range.getSize(seq)+" bp)");
			} 
		
		
		public void action()
			{
			MenuAnnotation mPopup=new MenuAnnotation(pw, seq, annot);
			mPopup.exec(mapToGlobal(bAction.pos()));
			}
		}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////


	/**
	 * Bacteria information
	 */
	public class PaneBacteria extends QGroupBox
		{
		BacteriaProperty prop=new BacteriaProperty();
		QLineEdit tfName=new QLineEdit();
		QCheckBox cbDam=new QCheckBox(tr("Dam"));
		QCheckBox cbDcm=new QCheckBox(tr("Dcm"));
		QCheckBox cbEcoKI=new QCheckBox(tr("EcoKI"));
		
		public PaneBacteria()
			{
			setTitle(tr("Bacteria"));

			QVBoxLayout lay=new QVBoxLayout();
			lay.addLayout(QTutil.withLabel(tr("Name:"), tfName));
			lay.addWidget(cbDam);
			lay.addWidget(cbDcm);
			lay.addWidget(cbEcoKI);
			lay.setMargin(0);

			QHBoxLayout hlay=new QHBoxLayout();
			hlay.addLayout(lay);
//			hlay.addWidget(bAction);
			hlay.setMargin(0);
			setLayout(hlay);

			BacteriaProperty p=seq.bacteria;
			tfName.setText(p.name);
			cbDam.setChecked(p.isDam);
			cbDcm.setChecked(p.isDcm);
			cbEcoKI.setChecked(p.isEcoKI);
			
			tfName.editingFinished.connect(this,"edited()");
			cbDam.toggled.connect(this,"edited()");
			cbDcm.toggled.connect(this,"edited()");
			cbEcoKI.toggled.connect(this,"edited()");
			}
		
		public BacteriaProperty getProp()
			{
			BacteriaProperty p=new BacteriaProperty();
			p.isDam=cbDam.isChecked();
			p.isDcm=cbDcm.isChecked();
			p.isEcoKI=cbEcoKI.isChecked();
			p.name=tfName.text();
			return p;
			}
		
		public void edited()
			{
			seq.bacteria=getProp();
			}
		}

	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	
	/**
	 * Update everything
	 */
	public void setSequence(AnnotatedSequence s)
		{
		isUpdating=true;
		this.seq=s;
		
		double weight;
		if(s.getLength()<50)
			{
			weight=0;
			for(char c:s.getSequence().toCharArray())
				{
				if(c=='A')
					weight+=312.2;
				else if(c=='T')
					weight+=304.2;
				else if(c=='C')
					weight+=289.2;
				else if(c=='G')
					weight+=329.2;
				}
			}
		else
			weight=(313.2+304.2+289.2+329.2)/4.0*seq.getLength();
		if(!seq.isCircular)
			weight+=79;
		weight*=2;
		
		
		if(!tfName.text().equals(seq.name))
			tfName.setText(seq.name);
		cbIsCircular.setChecked(seq.isCircular);
		if(!tfNotes.toPlainText().equals(seq.notes))
			tfNotes.setText(seq.notes);
		tfLength.setText(""+seq.getLength());
		tfWeight.setText("approx "+weight+" Da");
		
		
		updatePrimers();
		updateAnnotations();
    updateGeometry();  //not enough!
    ensureVisible(0, 100000);
		
		
		isUpdating=false;
		}
	
	
	
	/**
	 * Update primer panes
	 */
	private void updatePrimers()
		{
		//Add and update existing panes
		for(Primer p:seq.primers)
			{
			PanePrimer pp=listPrimers.get(p);
			if(pp==null)
				{
				pp=new PanePrimer();
				listPrimers.put(p,pp);
				layprimers.addWidget(pp);
				}
			pp.setPrimer(p);
			}
		//Remove unused panes
		for(Primer p:new LinkedList<Primer>(listPrimers.keySet()))
			{
			if(!seq.primers.contains(p))
				{
				PanePrimer pp=listPrimers.get(p);
				layprimers.removeWidget(pp);
				pp.setVisible(false);
				listPrimers.remove(p);
				}
			}
		}
	
	
	/**
	 * Update annotation panes
	 */
	private void updateAnnotations()
		{
		//Add and update existing panes
		for(SeqAnnotation annot:seq.annotations)
			{
			PaneAnnotation pane=listAnnot.get(annot);
			if(pane==null)
				{
				pane=new PaneAnnotation();
				listAnnot.put(annot,pane);
				layannot.addWidget(pane);
				}
			pane.setAnnotation(annot);
			}
		//Remove unused panes
		for(SeqAnnotation annot:new LinkedList<SeqAnnotation>(listAnnot.keySet()))
			{
			if(!seq.annotations.contains(annot))
				{
				PaneAnnotation pane=listAnnot.get(annot);
				layannot.removeWidget(pane);
				pane.setVisible(false);
				listAnnot.remove(annot);
				}
			}
		}

	
	/**
	 * Constructor
	 */
	public PaneSequenceInfo(ProjectWindow pw)
		{
		this.pw=pw;
		QWidget w=QTutil.withinTitledFrame(tr("Information"),
				QTutil.layoutVertical(
						QTutil.withLabel(tr("Name: "), tfName),
						QTutil.withLabel(tr("Is circular: "), cbIsCircular),
						QTutil.withLabel(tr("Length: "), tfLength),
						QTutil.withLabel(tr("Weight: "), tfWeight),
						QTutil.withLabel(tr("Notes: "), tfNotes),
						layannot,
						layprimers
						));
		
		
		PaneBacteria panebac=new PaneBacteria();
		QVBoxLayout layh=new QVBoxLayout();
		layh.addWidget(panebac);
		layh.addStretch();
		layh.setMargin(0);

		QHBoxLayout lay=new QHBoxLayout();
		lay.addWidget(w);
		lay.addLayout(layh);
		lay.setMargin(0);
		QWidget tw=new QWidget();
		tw.setLayout(lay);
		setWidget(tw);
		
		tfLength.setTextInteractionFlags(TextInteractionFlag.TextSelectableByMouse);
		tfWeight.setTextInteractionFlags(TextInteractionFlag.TextSelectableByMouse);
		
		tfName.textChanged.connect(this,"updateseq()");
		cbIsCircular.clicked.connect(this,"updateseq()");
		tfNotes.textChanged.connect(this,"updateseq()");
		
//		setVerticalScrollBarPolicy(ScrollBarPolicy.ScrollBarAlwaysOn);
		setWidgetResizable(true);
		}

	public void updateseq()
		{
		if(!isUpdating)
			{
			seq.name=tfName.text();
			seq.isCircular=cbIsCircular.checkState()==CheckState.Checked;
			seq.notes=tfNotes.document().toPlainText();
			signalUpdated.emit(new EventSequenceModified(seq));
			}
		}

	}
