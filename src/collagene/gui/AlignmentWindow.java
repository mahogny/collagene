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

	
	private QCheckBox cbLocal=new QCheckBox();
	private QCheckBox cbCanGoOutside=new QCheckBox();
	AnnotatedSequence seqA, seqB;
	
	public AlignmentWindow(AnnotatedSequence seqA, AnnotatedSequence seqB)
		{
		this.seqA=seqA;
		this.seqB=seqB;
		
		QLayout lay=QTutil.layoutVertical(
				QTutil.withLabel(tr("Sequence A: "), new QLabel(seqA.name)),
				QTutil.withLabel(tr("Sequence B: "), new QLabel(seqB.name)),
				QTutil.withLabel(tr("Align locally: "), cbLocal),
				QTutil.withLabel(tr("No penalty for partial overlap: "), cbCanGoOutside),
				QTutil.layoutHorizontal(bOk,bCancel)
				);
		
		cbLocal.setChecked(false);
		cbCanGoOutside.setChecked(false);
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
		al.isLocal=cbLocal.isChecked();
		al.canGoOutside=cbCanGoOutside.isChecked();
		al.align(seqA, seqB);
		return al;
		}
	
	}
