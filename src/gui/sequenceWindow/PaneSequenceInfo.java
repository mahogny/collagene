package gui.sequenceWindow;

import gui.qt.QTutil;
import seq.AnnotatedSequence;

import com.trolltech.qt.QSignalEmitter;
import com.trolltech.qt.core.Qt.CheckState;
import com.trolltech.qt.gui.QCheckBox;
import com.trolltech.qt.gui.QHBoxLayout;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QLineEdit;
import com.trolltech.qt.gui.QTextEdit;
import com.trolltech.qt.gui.QWidget;

/**
 * 
 * Pane: Information/overview of sequence
 * 
 * @author Johan Henriksson
 *
 */
public class PaneSequenceInfo extends QWidget
	{
	private QLineEdit tfName=new QLineEdit();
	private QCheckBox cbIsCircular=new QCheckBox();
	private QTextEdit tfNotes=new QTextEdit();
	private QLabel tfLength=new QLabel();

	public QSignalEmitter.Signal0 signalUpdated=new Signal0();
	
	private AnnotatedSequence seq=new AnnotatedSequence();
	
	private boolean isUpdating=false;
	
	public void setSequence(AnnotatedSequence s)
		{
		isUpdating=true;
		this.seq=s;
		
		tfName.setText(seq.name);
		cbIsCircular.setChecked(seq.isCircular);
		tfNotes.setText(seq.notes);
		tfLength.setText(""+seq.getLength());
		isUpdating=false;
		}
	
	public PaneSequenceInfo()
		{
		QWidget w=QTutil.withinTitledFrame(tr("Information"),
				QTutil.layoutVertical(
						QTutil.withLabel(tr("Name: "), tfName),
						QTutil.withLabel(tr("Is circular: "), cbIsCircular),
						QTutil.withLabel(tr("Length: "), tfLength),
						QTutil.withLabel(tr("Notes: "), tfNotes)
						));
		
		QHBoxLayout lay=new QHBoxLayout();
		lay.addWidget(w);
		setLayout(lay);
		
		tfName.textChanged.connect(this,"updateseq()");
		cbIsCircular.clicked.connect(this,"updateseq()");
		tfNotes.textChanged.connect(this,"updateseqnotes()");
		}

	public void updateseqnotes()
		{
		seq.notes=tfNotes.document().toPlainText();
		}

	public void updateseq()
		{
		if(!isUpdating)
			{
			seq.name=tfName.text();
			seq.isCircular=cbIsCircular.checkState()==CheckState.Checked;
			updateseqnotes();
			signalUpdated.emit();
			}
		}

	}
