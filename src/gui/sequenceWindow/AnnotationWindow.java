package gui.sequenceWindow;

import gui.IndexUtil;
import gui.colors.QColorCombo;
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
import com.trolltech.qt.gui.QSizePolicy.Policy;

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
	private QColorCombo colorcombo=new QColorCombo();
	
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

		colorcombo.setSizePolicy(Policy.Minimum, Policy.Minimum);

		QLayout lay=QTutil.layoutVertical(
				QTutil.withLabel(tr("Name: "), tfName),
				QTutil.withLabel(tr("From: "), spFrom),
				QTutil.withLabel(tr("To: "), spTo),
				QTutil.withLabel(tr("Orientation: "), comboOrientation),
				QTutil.withLabel(tr("Color: "), colorcombo),
				QTutil.layoutHorizontal(bOk,bCancel)
				);
		
		setLayout(lay);
		
		bOk.clicked.connect(this,"actionOK()");
		bCancel.clicked.connect(this,"actionCancel()");
		}
	
	
	public void actionOK()
		{
		if(tfName.text().equals(""))
			QTutil.showNotice(this, tr("Annotation must have a name"));
		else
			{
			annot.name=tfName.text();
			annot.range.from=IndexUtil.fromTointernal(spFrom.value());
			annot.range.to=IndexUtil.toTointernal(spTo.value());
			annot.orientation=orientations.get(comboOrientation.currentIndex());
			annot.color=colorcombo.getCurrentColor();
			close();
			}
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
		spFrom.setValue(IndexUtil.fromTogui(a.getFrom()));
		spTo.setValue(IndexUtil.toTogui(a.getTo()));
		comboOrientation.setCurrentIndex(orientations.indexOf(a.orientation));
		colorcombo.setColor(a.color);
		}

	public SeqAnnotation getAnnotation()
		{
		return annot;
		}
	
	}
