package gui.sequenceWindow;

import gui.IndexUtil;
import gui.qt.QTutil;

import java.util.LinkedList;

import seq.Orientation;
import seq.SeqAnnotation;

import com.trolltech.qt.gui.QComboBox;
import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QLayout;
import com.trolltech.qt.gui.QLineEdit;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QSpinBox;

/**
 * Dialog for editing annotation
 * 
 * @author Johan Henriksson
 *
 */
public class AnnotationWindow extends QDialog
	{
	private SeqAnnotation annot=null;

	private QLineEdit tfName=new QLineEdit();
	private QSpinBox spFrom=new QSpinBox();
	private QSpinBox spTo=new QSpinBox();

	private QPushButton bOk=new QPushButton(tr("OK"));
	private QPushButton bCancel=new QPushButton(tr("Cancel"));

	private QComboBox comboOrientation=new QComboBox();
	
	private LinkedList<Orientation> orientations=new LinkedList<Orientation>();
	
	public AnnotationWindow()
		{
		spFrom.setMinimum(0);
		spFrom.setMaximum(1000000);

		spTo.setMinimum(0);
		spTo.setMaximum(1000000);

		comboOrientation.addItem(tr("No orientation"));
		comboOrientation.addItem(tr("Forward"));
		comboOrientation.addItem(tr("Reverse"));
		
		orientations.add(Orientation.NOTORIENTED);
		orientations.add(Orientation.FORWARD);
		orientations.add(Orientation.REVERSE);
		
		QLayout lay=QTutil.layoutVertical(
				QTutil.withLabel("Name: ", tfName),
				QTutil.withLabel("From: ", spFrom),
				QTutil.withLabel("To: ", spTo),
				QTutil.withLabel("Orientation: ", comboOrientation),
				QTutil.layoutHorizontal(bOk,bCancel)
				);
		
		setLayout(lay);
		
		bOk.clicked.connect(this,"actionOK()");
		bCancel.clicked.connect(this,"actionCancel()");
		}
	
	
	public void actionOK()
		{
		annot.name=tfName.text();
		annot.from=IndexUtil.fromTointernal(spFrom.value());
		annot.to=IndexUtil.toTointernal(spTo.value());
		annot.orientation=orientations.get(comboOrientation.currentIndex());
		close();
		}

	public void actionCancel()
		{
		annot=null;
		close();
		}

	public void setAnnotation(SeqAnnotation a)
		{
		annot=a;
		tfName.setText(a.name);
		spFrom.setValue(IndexUtil.fromTogui(a.from));
		spTo.setValue(IndexUtil.toTogui(a.to));
		comboOrientation.setCurrentIndex(orientations.indexOf(a.orientation));
		}

	public SeqAnnotation getAnnotation()
		{
		return annot;
		}
	
	}
