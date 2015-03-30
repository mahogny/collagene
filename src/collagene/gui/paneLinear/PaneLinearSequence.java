package collagene.gui.paneLinear;

import collagene.gui.ProjectWindow;
import collagene.gui.resource.ImgResource;
import collagene.seq.AnnotatedSequence;
import collagene.seq.SequenceRange;

import com.trolltech.qt.QSignalEmitter;
import com.trolltech.qt.core.Qt.Orientation;
import com.trolltech.qt.gui.QHBoxLayout;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QSlider;
import com.trolltech.qt.gui.QVBoxLayout;
import com.trolltech.qt.gui.QWidget;

/**
 * 
 * Pane: Linear sequence view
 * 
 * @author Johan Henriksson
 *
 */
public class PaneLinearSequence extends QWidget
	{
	private QMenu menuSettings=new QMenu();

	private QSlider sliderZoom=new QSlider(Orientation.Horizontal);
	private QPushButton bSettings=new QPushButton(new QIcon(ImgResource.imgSettings), "");
	private QLabel labelZoom=ImgResource.label(ImgResource.search);
	
	private ViewLinearSequence view;
	
	public QSignalEmitter.Signal1<Object> signalUpdated=new Signal1<Object>();

	
	/**
	 * Constructor
	 */
	public PaneLinearSequence(ProjectWindow w)
		{
		view=new ViewLinearSequence(w);

		sliderZoom.setRange(0, 5000);
		sliderZoom.setValue(0);
		sliderZoom.valueChanged.connect(this,"updateview()");
		
		QHBoxLayout laycirc=new QHBoxLayout();
		laycirc.addWidget(labelZoom);
		laycirc.addWidget(sliderZoom);
		laycirc.addWidget(bSettings);
		laycirc.setMargin(0);

		menuSettings.addMenu(view.settings);
		
		bSettings.setMenu(menuSettings);

		QVBoxLayout lay=new QVBoxLayout();
		lay.addLayout(laycirc);
		lay.addWidget(view);
		lay.setMargin(0);
		setLayout(lay);

		view.settings.signalSettingsChanged.connect(this,"updateview()");  //train wreck
		view.signalUpdated.connect(this,"onViewUpdated(Object)");
		
		updateview();
		}
	
	
	public void onViewUpdated(Object o)
		{
		signalUpdated.emit(o);
		}
	
	
	public void updateview()
		{
		view.charWidth=8+(sliderZoom.value()/(double)sliderZoom.maximum())*10;
		setSequence(view.getSequence()); //cruel
		}

	public void setSequence(AnnotatedSequence seq)
		{
		view.setSequence(seq);
		}

	public void setSelection(SequenceRange range)
		{
		view.setSelection(range);
		}


	public SequenceRange getSelection()
		{
		return view.getSelection();
		}
	
	
	
	public void handleEvent(Object ob)
		{
		view.handleEvent(ob);
		}
	
	
	public void setFullsizeMode(boolean b)
		{
		sliderZoom.setVisible(!b); //or? just have a full size?
		labelZoom.setVisible(!b); //or? just have a full size?
		view.setFullsizeMode(b);
		}


	public void setEditable(boolean b)
		{
		view.setEditable(b);
		}

	}
