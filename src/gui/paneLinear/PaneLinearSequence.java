package gui.paneLinear;

import gui.ProjectWindow;
import gui.paneRestriction.EventSelectedRestrictionEnzyme;
import gui.resource.ImgResource;
import seq.AnnotatedSequence;
import seq.SequenceRange;

import com.trolltech.qt.QSignalEmitter;
import com.trolltech.qt.core.Qt.Orientation;
import com.trolltech.qt.gui.QHBoxLayout;
import com.trolltech.qt.gui.QIcon;
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
		laycirc.addWidget(ImgResource.label(ImgResource.search));
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
		setSequence(view.seq); //cruel
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
	
	public void setRestrictionEnzyme(EventSelectedRestrictionEnzyme enz)
		{
		view.selectedEnz=enz;
		updateview();
		}
	
	
	public void handleEvent(Object ob)
		{
		view.handleEvent(ob);
		}

	}
