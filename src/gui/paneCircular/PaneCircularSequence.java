package gui.paneCircular;

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
	private QPushButton bShowSelection=new QPushButton(new QIcon(ImgResource.search), "");
	private QPushButton bShowAll=new QPushButton(new QIcon(ImgResource.viewFullscreen), "");

	private CircView view;
	
	public QSignalEmitter.Signal1<Object> signalUpdated=new Signal1<Object>();
	
	
	public PaneCircularSequence(ProjectWindow pw)
		{
		view=new CircView(pw);
		
		sliderZoom.setRange(0, 100000);
		sliderRotate.setRange(0, 1000);
		sliderZoom.valueChanged.connect(this,"updatecirc()");
		sliderRotate.valueChanged.connect(this,"updatecirc()");
		
		QHBoxLayout laycirc=new QHBoxLayout();
		laycirc.addWidget(bShowSelection);
		laycirc.addWidget(sliderZoom);
		laycirc.addWidget(bSettings);
		laycirc.setMargin(0);

		QHBoxLayout laycirc2=new QHBoxLayout();
		laycirc2.addWidget(bShowAll);
		laycirc2.addWidget(ImgResource.label(ImgResource.moveLeft));
		laycirc2.addWidget(sliderRotate);
		laycirc2.addWidget(ImgResource.label(ImgResource.moveRight));
		laycirc2.setMargin(0);

		//private CircViewSettings menuSettings=new CircViewSettings();
		QMenu menu=new QMenu();
		menu.addMenu(view.settings);
		
		bSettings.setMenu(menu);
		bShowAll.clicked.connect(this,"actionShowAll()");
		bShowSelection.clicked.connect(this,"actionShowSelection()");

		QVBoxLayout lay=new QVBoxLayout();
		lay.addLayout(laycirc);
		lay.addLayout(laycirc2);
		lay.addWidget(view);
		lay.setMargin(0);
		setLayout(lay);

		view.settings.signalSettingsChanged.connect(this,"updatecirc()");  //train wreck
		view.signalUpdated.connect(this,"onViewUpdated(Object)");

		view.movetoinstantaneous(
				sliderpantopos(),
				sliderzoomtoscale());

		updatecirc();
		}
	
	public void actionShowAll()
		{
		sliderZoom.setValue(0);
		}

	public void actionShowSelection()
		{
		SequenceRange r=view.getSelection();
		if(r!=null)
			{
			AnnotatedSequence seq=view.getSequence();
			r=r.toUnwrappedRange(seq);
			int mid=(int)(r.from+r.to)/2;
			mid=seq.normalizePos(mid);
			int sliderpos=(int)(sliderRotateMul*3/4 - sliderRotateMul*mid/(double)seq.getLength());
			while(sliderpos<0)
				sliderpos+=sliderRotateMul;
			while(sliderpos>sliderRotateMul)
				sliderpos-=sliderRotateMul;
			sliderRotate.setValue(sliderpos);
			
			double arcspan=(r.to-r.from)/(double)seq.getLength();

			double minspan=0.05;
			if(arcspan>0.5)
				arcspan=0.5;
			if(arcspan<minspan)
				arcspan=minspan;
			int newzoom=scaletosliderzoom(0.6/(arcspan*2));
			sliderZoom.setValue(newzoom);
			}
		}
	
	public void onViewUpdated(Object o)
		{
		signalUpdated.emit(o);
		updatecirc();
		}

	private double sliderzoomtoscale()
		{
		return 0.4+sliderZoom.value()/10000.0;
		}
	
	private int scaletosliderzoom(double scale)
		{
		return (int)(10000*(scale-0.4));
		}
	private double sliderpantopos()
		{
		return sliderRotate.value()/(double)sliderRotateMul;
		}
		
	
	private int sliderRotateMul=1000;
	public void updatecirc()
		{
		view.moveto(
				sliderpantopos(),
				sliderzoomtoscale());
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


	public void setRestrictionEnzyme(EventSelectedRestrictionEnzyme enz)
		{
		view.selectedEnz=enz;
		updatecirc();
		}
	}
