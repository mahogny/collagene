package collagene.gui.paneRestriction;

import com.trolltech.qt.QSignalEmitter;
import com.trolltech.qt.gui.QAction;
import com.trolltech.qt.gui.QActionGroup;
import com.trolltech.qt.gui.QMenu;


/**
 * 
 * Settings for a sequence viewer regarding restriction sites
 * 
 * @author Johan Henriksson
 *
 */
public class MenuViewSettingsRestrictionSite extends QMenu
	{
	private QAction cbNickSites=new QAction(tr("Nick sites"),this);
	private QAction cbShowBlunt=new QAction(tr("Blunt sites"),this);
	private QAction cbShowSticky=new QAction(tr("Sticky sites"),this);

	public QSignalEmitter.Signal0 signalSettingsChanged=new Signal0();

	private int numRestrictionSite=1;

	
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
	

	
	

	/**
	 * Constructor
	 */
	public MenuViewSettingsRestrictionSite()
		{
		setTitle(tr("Show restriction sites"));

		QActionGroup gmotif=new QActionGroup(this);
		QAction cbMotifAny=gmotif.addAction(tr("Any motif"));
		QAction cbMotif4=gmotif.addAction(tr("Motif >= 4bp"));
		QAction cbMotif6=gmotif.addAction(tr("Motif >= 6bp"));
		QAction cbMotif8=gmotif.addAction(tr("Motif >= 8bp"));
		cbMotifAny.setCheckable(true);
		cbMotif4.setCheckable(true);
		cbMotif6.setCheckable(true);
		cbMotif8.setCheckable(true);
		cbMotifAny.setChecked(true);

		//TODO turn number into a group as well
		
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
		addAction(cbMotifAny);
		addAction(cbMotif4);
		addAction(cbMotif6);
		addAction(cbMotif8);
		
		ViewSettingsRestrictionEnzymes s=new ViewSettingsRestrictionEnzymes();
		
		cbNickSites.setCheckable(true);
		cbNickSites.setChecked(s.showNickEnzymes);
		cbNickSites.triggered.connect(this,"updateSettings()");
		
		cbShowBlunt.setCheckable(true);
		cbShowBlunt.setChecked(s.showBluntSites);
		cbShowBlunt.triggered.connect(this,"updateSettings()");

		cbShowSticky.setCheckable(true);
		cbShowSticky.setChecked(s.showStickySites);
		cbShowSticky.triggered.connect(this,"updateSettings()");

		cbMotifAny.triggered.connect(this,"setMotifAny()");
		cbMotif4.triggered.connect(this,"setMotif4()");
		cbMotif6.triggered.connect(this,"setMotif6()");
		cbMotif8.triggered.connect(this,"setMotif8()");
		}

	int motifsize=-1;
	public void setMotifAny()
		{
		motifsize=-1;
		updateSettings();
		}
	public void setMotif4()
		{
		motifsize=4;
		updateSettings();
		}
	public void setMotif6()
		{
		motifsize=6;
		updateSettings();
		}
	public void setMotif8()
		{
		motifsize=8;
		updateSettings();
		}
	
	
	public void updateSettings()
		{
		signalSettingsChanged.emit();
		}
	
	public ViewSettingsRestrictionEnzymes getSettings()
		{
		ViewSettingsRestrictionEnzymes s=new ViewSettingsRestrictionEnzymes();
		
		s.motifsize=motifsize;
		s.numRestrictionSite=numRestrictionSite;
		s.showNickEnzymes=cbNickSites.isChecked();
		s.showBluntSites=cbShowBlunt.isChecked();
		s.showStickySites=cbShowSticky.isChecked();
		
		return s;
		}
	
	}
