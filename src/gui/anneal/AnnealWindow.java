package gui.anneal;

import seq.AnnotatedSequence;
import gui.qt.QTutil;

import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QLineEdit;
import com.trolltech.qt.gui.QPushButton;

/**
 * 
 * Dialog to anneal two oligos
 * 
 * @author Johan Henriksson
 *
 */
public class AnnealWindow extends QDialog
	{
	private QLineEdit tfUpper=new QLineEdit();
	private QLineEdit tfLower=new QLineEdit();
	private QPushButton bCancel=new QPushButton(tr("Cancel"));
	private QPushButton bOk=new QPushButton(tr("OK"));
	
	public AnnotatedSequence seq;
	
	public AnnealWindow()
		{
		QGridLayout lay=new QGridLayout();
		lay.addWidget(new QLabel(tr("Oligo A:")), 0, 0);
		lay.addWidget(new QLabel(tr("Oligo B:")), 1, 0);
		lay.addWidget(tfUpper, 0, 1);
		lay.addWidget(tfLower, 1, 1);

		bOk.clicked.connect(this,"actionOK()");
		bCancel.clicked.connect(this,"close()");
		
		setLayout(QTutil.layoutVertical(lay,QTutil.layoutHorizontal(bCancel, bOk)));
		
		bOk.setDefault(true);
		setMinimumWidth(500);
		}
	
	public void actionOK()
		{
		Annealer a=new Annealer();
		String ta=tfUpper.text();
		String tb=tfLower.text();
		seq=a.anneal(ta, tb);
		if(seq!=null)
			close();
		else
			QTutil.showNotice(this, tr("Failed to align"));
		}
	
	
	}
