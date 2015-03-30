package collagene.gui.sequenceWindow;

import java.util.LinkedList;
import java.util.TreeMap;

import collagene.gui.qt.QTutil;
import collagene.seq.AnnotatedSequence;
import collagene.seq.SequenceRange;
import collagene.sequtil.sdm.CommonProteinSequence;
import collagene.sequtil.sdm.SiteDirectedMutagenesis;
import collagene.sequtil.sdm.SiteDirectedMutagenesisCandidate;
import collagene.sequtil.sdm.SiteDirectedMutagenesisStrategy;

import com.trolltech.qt.gui.QComboBox;
import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QLineEdit;
import com.trolltech.qt.gui.QPushButton;

/**
 * 
 * Dialog to set up site directed mutagenesis
 * 
 * see also http://bioinformatics.org/primerx/documentation.html  for how to possibly improve in the future
 * 
 * @author Johan Henriksson
 *
 */
public class SDMWindow extends QDialog
	{
	private QLineEdit tfName=new QLineEdit();
	private QLabel tfOldSequence=new QLabel();
	private QLineEdit tfSequence=new QLineEdit();
	private QPushButton bCancel=new QPushButton(tr("Cancel"));
	private QPushButton bOk=new QPushButton(tr("OK"));
	

	private QComboBox comboStrategy=new QComboBox();
	private QComboBox comboCommonProtSeq=new QComboBox();
	
	private AnnotatedSequence seq;
	private SequenceRange range;
	
	
	
	public SDMWindow(AnnotatedSequence seq, SequenceRange range)
		{
		this.seq=seq;
		this.range=range;
		
		QGridLayout lay=new QGridLayout();
		lay.addWidget(new QLabel(tr("Name:")), 0, 0);
		lay.addWidget(new QLabel(tr("Current sequence:")), 1, 0);
		lay.addWidget(new QLabel(tr("New sequence:")), 2, 0);		
		lay.addWidget(new QLabel(tr("Strategy:")), 3, 0);		
		lay.addWidget(tfName, 0, 1);
		lay.addWidget(tfOldSequence, 1, 1);
		lay.addWidget(tfSequence, 2, 1);
		lay.addWidget(comboStrategy, 3, 1);

		bOk.clicked.connect(this,"actionOK()");
		bCancel.clicked.connect(this,"actionCancel()");
		
		String oldseq=seq.getSequence(range);
		
		String name="sdm"+(int)(Math.random()*1000);
		tfName.setText(name);
		tfOldSequence.setText(oldseq);
		
		setLayout(QTutil.layoutVertical(
				lay,
				QTutil.layoutHorizontal(new QLabel(tr("Common protein sequences:")), comboCommonProtSeq),
				QTutil.layoutHorizontal(bCancel, bOk)
				));

		fillcombos();
		
		comboCommonProtSeq.currentIndexChanged.connect(this,"actionSetSeq()");
		tfSequence.textEdited.connect(this,"actionSeqChanged()");
		bOk.setDefault(true);
		setMinimumWidth(500);
		}
	
	public void actionSeqChanged()
		{
		comboCommonProtSeq.setCurrentIndex(0);
		}
	
	public void actionSetSeq()
		{
		CommonProteinSequence data=(CommonProteinSequence)comboCommonProtSeq.itemData(comboCommonProtSeq.currentIndex());
		if(data!=null)
			{
			System.out.println(data);
			tfSequence.setText(data.dnaseq);
			}
		}
	
	
	public void fillcombos()
		{
		TreeMap<String, SiteDirectedMutagenesisStrategy> mapstrat=new TreeMap<String, SiteDirectedMutagenesisStrategy>();
		mapstrat.put(tr("QUIKCHANGE"), SiteDirectedMutagenesisStrategy.QUIKCHANGE);
		for(String s:mapstrat.keySet())
			comboStrategy.addItem(s, mapstrat.get(s));
		
		LinkedList<CommonProteinSequence> list=CommonProteinSequence.get();
		comboCommonProtSeq.addItem("");
		for(CommonProteinSequence pseq:list)
			comboCommonProtSeq.addItem(pseq.name+" - "+pseq.protseq, pseq);
		}
	
	
	public boolean wasOk=false;
	
	private SiteDirectedMutagenesisCandidate mut;
	
	public void actionOK()
		{
		if(!tfName.text().equals(""))
			{
			SiteDirectedMutagenesisStrategy strat=(SiteDirectedMutagenesisStrategy)comboStrategy.itemData(comboStrategy.currentIndex());
			
			mut=SiteDirectedMutagenesis.designPrimer(seq, range, tfSequence.text(), tfName.text(), strat);
			seq.primers.add(mut.fwd);
			seq.primers.add(mut.rev);
			
			wasOk=true;
			close();
			}
		}

	public void actionCancel()
		{
		close();
		}
	
	public SiteDirectedMutagenesisCandidate getCand()
		{
		if(wasOk)
			return mut;
		else
			return null;
		}
		
	}
