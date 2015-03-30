package collagene.gui;

import collagene.alignment.AnnotatedSequenceAlignment;
import collagene.gui.qt.QTutil;
import collagene.seq.AnnotatedSequence;

import com.trolltech.qt.gui.QCheckBox;
import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QLayout;
import com.trolltech.qt.gui.QPushButton;

/**
 * Dialog for editing annotation
 * 
 * @author Johan Henriksson
 *
 */
public class AlignmentWindow extends QDialog
	{
	private QPushButton bOk=new QPushButton(tr("OK"));
	private QPushButton bCancel=new QPushButton(tr("Cancel"));

	
	private QCheckBox cbLocalA=new QCheckBox();
	private QCheckBox cbLocalB=new QCheckBox();
	AnnotatedSequence seqA, seqB;
	
	public AlignmentWindow(AnnotatedSequence seqA, AnnotatedSequence seqB)
		{
		this.seqA=seqA;
		this.seqB=seqB;
		
		QLayout lay=QTutil.layoutVertical(
				QTutil.withLabel(tr("Sequence A: "), new QLabel(seqA.name)),
				QTutil.withLabel(tr("Sequence B: "), new QLabel(seqB.name)),
				QTutil.withLabel(tr("Align A locally: "), cbLocalA),
				QTutil.withLabel(tr("Align B locally: "), cbLocalB),
				QTutil.layoutHorizontal(bOk,bCancel)
				);
		
		cbLocalA.setChecked(false);
		cbLocalB.setChecked(true);
		setLayout(lay);
		
		bOk.clicked.connect(this,"actionOK()");
		bCancel.clicked.connect(this,"actionCancel()");
		}
	
	public boolean wasOk=false;
	
	public void actionOK()
		{
		wasOk=true;
		close();
		}
	
	public void actionCancel()
		{
		close();
		}

	public AnnotatedSequenceAlignment performAlignment()
		{
		AnnotatedSequenceAlignment al=new AnnotatedSequenceAlignment();
		al.isLocalA=cbLocalA.isChecked();
		al.isLocalB=cbLocalB.isChecked();
		al.align(seqA, seqB);
		return al;
		}
	
	}
