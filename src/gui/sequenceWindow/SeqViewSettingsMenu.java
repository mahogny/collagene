package gui.sequenceWindow;

import restrictionEnzyme.RestrictionEnzyme;
import restrictionEnzyme.RestrictionEnzymeCut;

import com.trolltech.qt.QSignalEmitter;
import com.trolltech.qt.gui.QAction;
import com.trolltech.qt.gui.QMenu;


/**
 * 
 * @author Johan Henriksson
 *
 */
public class SeqViewSettingsMenu extends QMenu
	{
	private int numRestrictionSite=1;

	private boolean showNickEnzymes=true;
	private QAction cbNickSites=new QAction(tr("Single-cutters"),this);
	
	public QSignalEmitter.Signal0 signalSettingsChanged=new Signal0();
	
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
		numRestrictionSite=-1;
		signalSettingsChanged.emit();
		}
	
	
	public boolean allowsRestrictionSiteCount(RestrictionEnzyme enz, int c)
		{
		boolean b = (c>=1 && c<=numRestrictionSite) || numRestrictionSite==-1;
		if(b)
			{
			if(showNickEnzymes)
				return true;
			else
				{
				for(RestrictionEnzymeCut cut:enz.cuts)
					if(cut.lower==null)
						return false;
				return true;
				}
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
		addAction("None",this,"setRestrictionSite0()");
		addAction("1",this,"setRestrictionSite1()");
		addAction("1 or 2",this,"setRestrictionSite2()");
		addAction("All",this,"setRestrictionSiteAll()");
		addSeparator();
		addAction(cbNickSites);
		
		cbNickSites.setCheckable(true);
		cbNickSites.setChecked(showNickEnzymes);
		cbNickSites.triggered.connect(this,"updateSettings()");
		}
	
	
	
	public void updateSettings()
		{
		showNickEnzymes=cbNickSites.isChecked();
		signalSettingsChanged.emit();
		}
	
	}
