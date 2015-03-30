package collagene.gui.primer;

import collagene.gui.qt.QTutil;
import collagene.primer.Primer;
import collagene.seq.AnnotatedSequence;

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
public class PrimerPropertyWindow extends QDialog
	{
	private QLineEdit tfName=new QLineEdit();
	private QLineEdit tfSequence=new QLineEdit();
	private QPushButton bCancel=new QPushButton(tr("Cancel"));
	private QPushButton bOk=new QPushButton(tr("OK"));
	
	public AnnotatedSequence seq;
	private Primer primer=new Primer();

	
	public PrimerPropertyWindow()
		{
		QGridLayout lay=new QGridLayout();
		lay.addWidget(new QLabel(tr("Name:")), 0, 0);
		lay.addWidget(new QLabel(tr("Sequence:")), 1, 0);
		lay.addWidget(tfName, 0, 1);
		lay.addWidget(tfSequence, 1, 1);

		bOk.clicked.connect(this,"actionOK()");
		bCancel.clicked.connect(this,"actionCancel()");
		
		setLayout(QTutil.layoutVertical(lay,QTutil.layoutHorizontal(bCancel, bOk)));
		
		bOk.setDefault(true);
		setMinimumWidth(500);
		}
	
	public boolean wasOk=false;
	
	public void actionOK()
		{
		if(!tfName.text().equals("") && !tfSequence.text().equals(""))
			{
			primer.name=tfName.text();
			primer.sequence=tfSequence.text();
			wasOk=true;
			close();
			}
		}

	public void actionCancel()
		{
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
		if(wasOk)
			return primer;
		else
			return null;
		}
		
	}
