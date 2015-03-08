package gui.sequenceWindow;

import seq.AnnotatedSequence;
import seq.Primer;
import gui.qt.QTutil;

import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QLineEdit;
import com.trolltech.qt.gui.QPushButton;

/**
 * 
 * Dialog to set primer data
 * 
 * @author Johan Henriksson
 *
 */
public class PrimerWindow extends QDialog
	{
	private QLineEdit tfName=new QLineEdit();
	private QLineEdit tfSequence=new QLineEdit();
	private QPushButton bCancel=new QPushButton(tr("Cancel"));
	private QPushButton bOk=new QPushButton(tr("OK"));
	
	public AnnotatedSequence seq;
	private Primer primer=new Primer();

	
	public PrimerWindow()
		{
		QGridLayout lay=new QGridLayout();
		lay.addWidget(new QLabel(tr("Name:")), 0, 0);
		lay.addWidget(new QLabel(tr("Sequence:")), 1, 0);
		lay.addWidget(tfName, 0, 1);
		lay.addWidget(tfSequence, 1, 1);

		bOk.clicked.connect(this,"actionOK()");
		bCancel.clicked.connect(this,"actionCancel()");
		
		setLayout(QTutil.layoutVertical(lay,QTutil.layoutHorizontal(bCancel, bOk)));
		
		setMinimumWidth(500);
		}
	
	public void actionOK()
		{
		primer.name=tfName.text();
		primer.sequence=tfSequence.text();
		if(!primer.name.equals("") && !primer.sequence.equals(""))
			close();
		}

	public void actionCancel()
		{
		primer=null;
		close();
		}
	
	public void setPrimer(Primer p)
		{
		primer=p;
		tfName.setText(p.name);
		tfSequence.setText(p.sequence);
		}
	public Primer getPrimer()
		{
		return primer;
		}
		
	}
