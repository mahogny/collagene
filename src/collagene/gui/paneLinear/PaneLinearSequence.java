package collagene.gui.paneLinear;

import collagene.gui.ProjectWindow;
import collagene.gui.paneRestriction.MenuViewSettingsRestrictionSite;
import collagene.gui.resource.ImgResource;
import collagene.gui.sequenceWindow.CollageneEvent;
import collagene.gui.sequenceWindow.MenuViewSettingsSequence;
import collagene.gui.sequenceWindow.ViewSettingsSequence;
import collagene.seq.AnnotatedSequence;
import collagene.seq.SequenceRange;

import com.trolltech.qt.QSignalEmitter;
import com.trolltech.qt.core.Qt.Orientation;
import com.trolltech.qt.gui.QHBoxLayout;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QSizePolicy.Policy;
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
	private QLabel labelEmpty=new QLabel("");
	
	private ViewLinearSequence view;

	public MenuViewSettingsRestrictionSite menuSettingsRS=new MenuViewSettingsRestrictionSite();
	public MenuViewSettingsSequence menuSettingsSeq=new MenuViewSettingsSequence();

	public QSignalEmitter.Signal1<CollageneEvent> signalUpdated=new Signal1<CollageneEvent>();

	
	/**
	 * Constructor
	 */
	public PaneLinearSequence(ProjectWindow w)
		{
		view=new ViewLinearSequence(w);

		sliderZoom.setRange(0, 5000);
		sliderZoom.setValue(0);
		sliderZoom.valueChanged.connect(this,"updateview()");
		
		labelEmpty.setSizePolicy(Policy.Expanding, Policy.Minimum);
		
		QHBoxLayout laycirc=new QHBoxLayout();
		laycirc.addWidget(labelEmpty);
		laycirc.addWidget(labelZoom);
		laycirc.addWidget(sliderZoom);
		laycirc.addWidget(bSettings);
		laycirc.setMargin(0);


		menuSettings.addMenu(menuSettingsRS);
		menuSettings.addMenu(menuSettingsSeq);
		
		bSettings.setMenu(menuSettings);

		QVBoxLayout lay=new QVBoxLayout();
		lay.addLayout(laycirc);
		lay.addWidget(view);
		lay.setMargin(0);
		setLayout(lay);

		menuSettingsRS.signalSettingsChanged.connect(this,"updateview()"); 
		menuSettingsSeq.signalSettingsChanged.connect(this,"updateview()"); 
		view.signalUpdated.connect(this,"onViewUpdated(CollageneEvent)");
		
		updateview();
		}
	
	
	public void onViewUpdated(CollageneEvent o)
		{
		signalUpdated.emit(o);
		}
	
	
	public void updateview()
		{
		view.settingsRS=menuSettingsRS.getSettings();
		view.settingsSeq=menuSettingsSeq.getSettings();
		view.charWidth=8+(sliderZoom.value()/(double)sliderZoom.maximum())*10;
		setSequence(view.getSequence()); //cruel
		
		ViewSettingsSequence ss=menuSettingsSeq.getSettings();
		sliderZoom.setVisible(!ss.fullsize);
		labelZoom.setVisible(!ss.fullsize); 
		labelEmpty.setVisible(ss.fullsize); 
		//view.setFullsizeMode(ss.fullsize);
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
		menuSettingsSeq.setFullsizeMode(b);
		}


	public void setEditable(boolean b)
		{
		view.setEditable(b);
		}

	}
