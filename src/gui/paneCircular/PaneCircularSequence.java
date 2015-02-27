package gui.paneCircular;

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
 * Pane: Show sequence as a circular plasmid
 * 
 * @author Johan Henriksson
 *
 */
public class PaneCircularSequence extends QWidget
	{
	
	private QSlider sliderZoom=new QSlider(Orientation.Horizontal);
	private QSlider sliderRotate=new QSlider(Orientation.Horizontal);
	private QPushButton bSettings=new QPushButton(new QIcon(ImgResource.imgSettings), "");

	private CircView view=new CircView();
	
	public QSignalEmitter.Signal0 signalSelectionChanged=new Signal0();
	
	
	public PaneCircularSequence()
		{
		QVBoxLayout lay=new QVBoxLayout();
		setLayout(lay);

		

		QHBoxLayout laycirc=new QHBoxLayout();
		
		sliderZoom.setRange(0, 5000);
		sliderRotate.setRange(0, 1000);
		sliderZoom.valueChanged.connect(this,"updatecirc()");
		sliderRotate.valueChanged.connect(this,"updatecirc()");
		
		laycirc.addWidget(ImgResource.label(ImgResource.search));
		laycirc.addWidget(sliderZoom);
		laycirc.addWidget(ImgResource.label(ImgResource.moveLeft));
		laycirc.addWidget(sliderRotate);
		laycirc.addWidget(ImgResource.label(ImgResource.moveRight));
		laycirc.addWidget(bSettings);

		//private CircViewSettings menuSettings=new CircViewSettings();
		QMenu menu=new QMenu();
		menu.addMenu(view.settings);
		
		bSettings.setMenu(menu);

		lay.addLayout(laycirc);
		lay.addWidget(view);

		view.settings.signalSettingsChanged.connect(this,"updatecirc()");  //train wreck
		
		updatecirc();
		}
	
	
	public void updatecirc()
		{
		view.circPan=sliderRotate.value()/1000.0;
		view.circZoom=0.4+sliderZoom.value()/1000.0;
		view.setCameraFromCirc();
		}

	public void setSequence(AnnotatedSequence seq)
		{
		view.seq=seq;
		updatecirc();
		}

	public void setSelection(SequenceRange range)
		{
		view.setSelection(range);
		}
	}
