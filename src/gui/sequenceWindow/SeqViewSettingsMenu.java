package gui.sequenceWindow;

import restrictionEnzyme.RestrictionEnzyme;

import com.trolltech.qt.QSignalEmitter;
import com.trolltech.qt.gui.QAction;
import com.trolltech.qt.gui.QMenu;


/**
 * 
 * Settings for a sequence viewer
 * 
 * @author Johan Henriksson
 *
 */
public class SeqViewSettingsMenu extends QMenu
	{
	private int numRestrictionSite=1;

	private boolean showNickEnzymes=true;
	public boolean showSkyline=false;
	private boolean showBluntSites=true;
	private boolean showStickySites=true;
	
	private QAction cbSkyline=new QAction(tr("Skyline sequence"),this);
	private QAction cbNickSites=new QAction(tr("Nick sites"),this);
	private QAction cbShowBlunt=new QAction(tr("Blunt sites"),this);
	private QAction cbShowSticky=new QAction(tr("Sticky sites"),this);

	public QSignalEmitter.Signal0 signalSettingsChanged=new Signal0();


	
	public void setRestrictionSiteNone()
		{
		numRestrictionSite=-1;
		signalSettingsChanged.emit();
		}
	public void setRestrictionSite0()
		{
		numRestrictionSite=0;
		signalSettingsChanged.emit();
		}
	public void setRestrictionSite1()
		{
		numRestrictionSite=1;
		signalSettingsChanged.emit();
		}
	public void setRestrictionSite2()
		{
		numRestrictionSite=2;	
		signalSettingsChanged.emit();
		}
	public void setRestrictionSiteAll()
		{
		numRestrictionSite=-2;
		signalSettingsChanged.emit();
		}
	
	
	public boolean allowsRestrictionSiteCount(RestrictionEnzyme enz, int c)
		{
		boolean b = numRestrictionSite!=-1 && ((c>=1 && c<=numRestrictionSite) || numRestrictionSite==-2 || (c==0 && numRestrictionSite==0));
		if(b)
			{
			boolean a=showBluntSites  && enz.isBlunt();
			boolean d=showStickySites && !enz.isBlunt();
			boolean g=showNickEnzymes && enz.isNicking();
			return a || d || g;
			}
		else
			return false;
		}

	
	

	/**
	 * Constructor
	 */
	public SeqViewSettingsMenu()
		{
		setTitle(tr("Show restriction sites"));
		addAction("None",this,"setRestrictionSiteNone()");
		addAction("0",this,"setRestrictionSite0()");
		addAction("1",this,"setRestrictionSite1()");
		addAction("1 or 2",this,"setRestrictionSite2()");
		addAction("All",this,"setRestrictionSiteAll()");
		addSeparator();
		addAction(cbNickSites);
		addAction(cbShowBlunt);
		addAction(cbShowSticky);
		addSeparator();
		addAction(cbSkyline);
		
		cbNickSites.setCheckable(true);
		cbNickSites.setChecked(showNickEnzymes);
		cbNickSites.triggered.connect(this,"updateSettings()");
		
		cbShowBlunt.setCheckable(true);
		cbShowBlunt.setChecked(showBluntSites);
		cbShowBlunt.triggered.connect(this,"updateSettings()");

		cbShowSticky.setCheckable(true);
		cbShowSticky.setChecked(showStickySites);
		cbShowSticky.triggered.connect(this,"updateSettings()");

		cbSkyline.setCheckable(true);
		cbSkyline.setChecked(showSkyline);
		cbSkyline.triggered.connect(this,"updateSettings()");
		}
	
	
	
	public void updateSettings()
		{
		showNickEnzymes=cbNickSites.isChecked();
		showSkyline=cbSkyline.isChecked();
		showBluntSites=cbShowBlunt.isChecked();
		showStickySites=cbShowSticky.isChecked();
		
		signalSettingsChanged.emit();
		}
	
	}
