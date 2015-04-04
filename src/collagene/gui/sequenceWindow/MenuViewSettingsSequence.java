package collagene.gui.sequenceWindow;

import com.trolltech.qt.QSignalEmitter;
import com.trolltech.qt.gui.QAction;
import com.trolltech.qt.gui.QMenu;


/**
 * 
 * Settings for a sequence viewer regarding sequence
 * 
 * @author Johan Henriksson
 *
 */
public class MenuViewSettingsSequence extends QMenu
	{
	private QAction mSkyline=new QAction(tr("Show sequence as skyline"),this);
	private QAction mSeqPosition=new QAction(tr("Show sequence positions"),this);
	private QAction mFullscreen=new QAction(tr("Zoom all sequence"),this);

	public QSignalEmitter.Signal0 signalSettingsChanged=new Signal0();
	
	int charsPerLine=80;

	/**
	 * Constructor
	 */
	public MenuViewSettingsSequence()
		{
		setTitle(tr("Sequence"));
		
		addAction(mSkyline);
		addAction(mSeqPosition);
		addSeparator();
		addAction(mFullscreen);
		addSeparator();
		addAction(tr("Show 80 characters per line"),this,"actionWidth80()");
		addAction(tr("Show 160 characters per line"),this,"actionWidth160()");
		addAction(tr("Show everything on one line"),this,"actionWidthInf()");

		ViewSettingsSequence s=new ViewSettingsSequence();

		mFullscreen.setCheckable(true);
		mFullscreen.setChecked(s.fullsize);
		mFullscreen.triggered.connect(this,"updateSettings()");

		mSeqPosition.setCheckable(true);
		mSeqPosition.setChecked(s.showPositionRuler);
		mSeqPosition.triggered.connect(this,"updateSettings()");


		mSkyline.setCheckable(true);
		mSkyline.setChecked(s.showSkyline);
		mSkyline.triggered.connect(this,"updateSettings()");
		}

	public void actionWidth80()
		{
		charsPerLine=80;
		updateSettings();
		}
	public void actionWidth160()
		{
		charsPerLine=160;		
		updateSettings();
		}
	public void actionWidthInf()
		{
		charsPerLine=-2;  //-1 is adapt?
		updateSettings();
		}
	
	public void updateSettings()
		{
		signalSettingsChanged.emit();
		}
	
	public ViewSettingsSequence getSettings()
		{
		ViewSettingsSequence s=new ViewSettingsSequence();
		s.showSkyline=mSkyline.isChecked();
		s.charsPerLine=charsPerLine;
		s.fullsize=mFullscreen.isChecked();
		s.showPositionRuler=mSeqPosition.isChecked();
		return s;
		}

	public void setFullsizeMode(boolean b)
		{
		mFullscreen.setChecked(b);
		updateSettings();
		}
	
	}
