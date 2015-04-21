package collagene.gui.paneRestriction;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.TreeMap;

import collagene.gui.ProjectWindow;
import collagene.gui.digest.SimulatedDigestWindow;
import collagene.gui.qt.QTutil;
import collagene.gui.resource.ImgResource;
import collagene.gui.sequenceWindow.CollageneEvent;
import collagene.gui.sequenceWindow.SequenceWindow;
import collagene.restrictionEnzyme.RestrictionEnzyme;
import collagene.seq.AnnotatedSequence;
import collagene.seq.RestrictionSite;
import collagene.seq.SequenceRange;

import com.trolltech.qt.QSignalEmitter;
import com.trolltech.qt.core.QModelIndex;
import com.trolltech.qt.core.QUrl;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QAbstractItemView.SelectionBehavior;
import com.trolltech.qt.gui.QDesktopServices;
import com.trolltech.qt.gui.QHeaderView.ResizeMode;
import com.trolltech.qt.gui.QItemSelectionModel.SelectionFlag;
import com.trolltech.qt.gui.QHBoxLayout;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QTableWidget;
import com.trolltech.qt.gui.QTableWidgetItem;
import com.trolltech.qt.gui.QVBoxLayout;
import com.trolltech.qt.gui.QWidget;

/**
 * 
 * Pane: Show list of restriction enzymes
 * 
 * @author Johan Henriksson
 *
 */
public class PaneEnzymeList extends QWidget
	{
	private BufferEfficiencyWidget bufOne=new BufferEfficiencyWidget();
	private BufferEfficiencyWidget bufCommon=new BufferEfficiencyWidget();
	private QLabel labCommon=new QLabel(tr("Common buffers"));
	private QVBoxLayout layCommon=new QVBoxLayout();

	private QPushButton bDigest=new QPushButton(tr("Digest"));
	private WidgetCutSite pcutsite=new WidgetCutSite();
	private QTableWidget tableAvailableEnzymes=new QTableWidget();

	private QPushButton bMenu=new QPushButton(new QIcon(ImgResource.imgSettings),"");
	private MenuViewSettingsRestrictionSite menuSettings=new MenuViewSettingsRestrictionSite();
	
	
	private AnnotatedSequence seq=new AnnotatedSequence();
	//private QGroupBox layInfo2=new QGroupBox("");
	private QLabel lEnzName=new QLabel();
	
	
	private QLabel labTempIncubation=new QLabel();
	private QLabel labTempInactivation=new QLabel();
	private QLabel labAffected=new QLabel();
	private ProjectWindow projwindow;

	public QSignalEmitter.Signal1<CollageneEvent> signalUpdated=new Signal1<CollageneEvent>();
	private SequenceWindow seqwindow;
	
	/**
	 * Set current sequence
	 */
	public void setSequence(AnnotatedSequence seq)
		{
		this.seq=seq;		
		updateView();
		}
	
	
	/**
	 * Fill in new information
	 */
	public void updateView()
		{
		//Fill table with enzymes
		int currow=0;
		
		while(tableAvailableEnzymes.rowCount()>0)
			tableAvailableEnzymes.removeRow(0);
		
		for(RestrictionEnzyme enz:projwindow.restrictionEnzymes.enzymes)
			{
			LinkedList<RestrictionSite> sites=seq.restrictionSites.get(enz);
			if(sites==null)
				sites=new LinkedList<RestrictionSite>();
			if(menuSettings.getSettings().allowsRestrictionSiteCount(enz, sites.size()))
				{
				tableAvailableEnzymes.setRowCount(currow+1);
				QTableWidgetItem it=QTutil.createReadOnlyItem(enz.name);
				it.setData(Qt.ItemDataRole.UserRole, enz);
				
				tableAvailableEnzymes.setItem(currow, 0, it);
				tableAvailableEnzymes.setItem(currow, 1, QTutil.createReadOnlyItem(""+sites.size()));
				
				currow++;
				}
			}
		}

	/**
	 * Constructor
	 */
	public PaneEnzymeList(SequenceWindow seqwindow, ProjectWindow projwindow)
		{
		this.seqwindow=seqwindow;
		this.projwindow=projwindow;
		
		tableAvailableEnzymes.setColumnCount(2);
		tableAvailableEnzymes.verticalHeader().hide();
		tableAvailableEnzymes.setHorizontalHeaderLabels(Arrays.asList(tr("Enzyme"),tr("#Cuts")));
		tableAvailableEnzymes.setSelectionBehavior(SelectionBehavior.SelectRows);
		tableAvailableEnzymes.horizontalHeader().setResizeMode(ResizeMode.ResizeToContents);
		tableAvailableEnzymes.horizontalHeader().setStretchLastSection(true);		
		
		bMenu.setMenu(menuSettings);
		menuSettings.signalSettingsChanged.connect(this,"updateView()");

		QPushButton bPrevSite=new QPushButton(new QIcon(ImgResource.moveLeft),"");
		QPushButton bNextSite=new QPushButton(new QIcon(ImgResource.moveRight),"");
		
		bPrevSite.clicked.connect(this,"actionPrevSite()");
		bNextSite.clicked.connect(this,"actionNextSite()");
		
		QHBoxLayout laysite=new QHBoxLayout();
		laysite.addWidget(new QLabel(tr("Site: ")));
		laysite.addWidget(bPrevSite);
		laysite.addWidget(bNextSite);
		
		QHBoxLayout layb=new QHBoxLayout();
		layb.addStretch();
		layb.addWidget(bMenu);
		layb.setMargin(0);
		
		QVBoxLayout lay=new QVBoxLayout();
		lay.addLayout(layb);
		lay.addWidget(tableAvailableEnzymes);
		
		lay.addWidget(lEnzName);
		lay.addWidget(pcutsite);
		lay.addLayout(laysite);
		lay.addWidget(labTempIncubation);
		lay.addWidget(labTempInactivation);
		lay.addWidget(labAffected);
		lay.addWidget(bufOne);

		lay.addWidget(bDigest);
		lay.addLayout(layCommon);
		lay.setMargin(0);
		setLayout(lay);
		layCommon.setMargin(0);
		
		
		
		lEnzName.linkActivated.connect(this,"actionGoWebsite()");
		tableAvailableEnzymes.selectionModel().selectionChanged.connect(this,"actionSelectedEnzyme()");
		bDigest.clicked.connect(this,"actionDigest()");

		actionSelectedEnzyme();

		setMinimumWidth(150);
		}

	/**
	 * Get currently selected enzyme
	 */
	public RestrictionEnzyme getCurrentEnzyme()
		{
		for(QModelIndex i:tableAvailableEnzymes.selectionModel().selectedRows())
			{
			RestrictionEnzyme enz=(RestrictionEnzyme)tableAvailableEnzymes.item(i.row(),0).data(Qt.ItemDataRole.UserRole);
			return enz;
			}
		return null;
		}

	/**
	 * Action: Go to website for enzyme
	 */
	public void actionGoWebsite()
		{
		RestrictionEnzyme enz=getCurrentEnzyme();
		if(enz!=null && enz.url!=null)
			QDesktopServices.openUrl(new QUrl(enz.url));
		}
	
	
	/**
	 * Action: An enzyme was selected
	 */
	public void actionSelectedEnzyme()
		{
		if(!isUpdating)
			{
			updateDisplayedEnzyme();
			signalUpdated.emit(getSelection());
			}
		}

	
	/**
	 * Update information about current enzyme
	 */
	private void updateDisplayedEnzyme()
		{
		RestrictionEnzyme enz=getCurrentEnzyme();
		if(enz!=null)
			{
			pcutsite.setEnzyme(enz);
			lEnzName.setText("Enzyme "+enz.name+" "+
					(enz.url==null ? "" : "- <a href=\""+enz.url+"\">web</a>"));
			labTempInactivation.setText("Inactivation: "+formatTemp(enz.tempInactivation));
			labTempIncubation.setText("Incubation: "+formatTemp(enz.tempIncubation));

			
			String aff=tr("Affected by:");
			for(String s:enz.affectedBy)
				aff+=" "+s;
			if(enz.affectedBy.isEmpty())
				aff+=" "+tr("-");
			labAffected.setText(aff);
			
			bufOne.fill(enz.bufferEfficiency);
			}

		//Fill up common buffer efficiency table
		Collection<RestrictionEnzyme> selEnzymes=getSelectedEnzymes();
		TreeMap<String,Double> commonEfficiencies=RestrictionEnzyme.getCommonBufferEfficiency(selEnzymes);
		bufCommon.fill(commonEfficiencies);

		if(selEnzymes.size()>1)
			{
			if(!bufCommon.isVisible())
				{
				bufCommon.setVisible(true);
				labCommon.setVisible(true);
				layCommon.addWidget(labCommon);
				layCommon.addWidget(bufCommon);
				}
			}
		else
			{
			if(bufCommon.isVisible())
				{
				bufCommon.setVisible(false);
				labCommon.setVisible(false);
				layCommon.removeWidget(labCommon);
				layCommon.removeWidget(bufCommon);
				}
			}
		}
	
	private EventSelectedRestrictionEnzyme getSelection()
		{
		EventSelectedRestrictionEnzyme e=new EventSelectedRestrictionEnzyme();
		e.enzymes=getSelectedEnzymes();
		return e;
		}
	
	
	/**
	 * Format temperature
	 */
	private String formatTemp(Double t)
		{
		if(t==null)
			return "-";
		else
			return ""+t+"°C";
		}
	
	
	
	/**
	 * Get currently selected enzymes
	 */
	public LinkedList<RestrictionEnzyme> getSelectedEnzymes()
		{
		LinkedList<RestrictionEnzyme> list=new LinkedList<RestrictionEnzyme>();
		for(QModelIndex i:tableAvailableEnzymes.selectionModel().selectedRows())
			{
			RestrictionEnzyme enz=(RestrictionEnzyme)tableAvailableEnzymes.item(i.row(),0).data(Qt.ItemDataRole.UserRole);
			list.add(enz); 
			}
		return list;
		}
	
	/**
	 * Action: Digest button
	 */
	public void actionDigest()
		{
		if(!getSelectedEnzymes().isEmpty())
			{
			SimulatedDigestWindow d=new SimulatedDigestWindow(projwindow);
			d.setSequence(seq, getSelectedEnzymes());
			d.setMinimumSize(400, 400);
			d.show();
			}
		}

	private boolean isUpdating=false;
	public void setRestrictionEnzyme(EventSelectedRestrictionEnzyme enz)
		{
		isUpdating=true;
		for(int i=0;i<tableAvailableEnzymes.rowCount();i++)
			{
			RestrictionEnzyme thisenz=(RestrictionEnzyme)tableAvailableEnzymes.item(i,0).data(Qt.ItemDataRole.UserRole);
			SelectionFlag flag=SelectionFlag.Select;
			if(!enz.hasEnzyme(thisenz))
				flag=SelectionFlag.Deselect;
			for(int col=0;col<tableAvailableEnzymes.columnCount();col++)
				{
				QModelIndex ind=tableAvailableEnzymes.model().index(i, col);
				tableAvailableEnzymes.selectionModel().select(ind, flag);
				}
			}
		isUpdating=false;
		updateDisplayedEnzyme();
		}
	

	/**
	 * Action: go to previous cut site
	 */
	public void actionPrevSite()
		{
		SequenceRange r=seqwindow.getSelection();
		if(r==null)
			r=new SequenceRange(0, 0);
		LinkedList<RestrictionSite> sites=getSortedSitesForCurrent();
		Collections.reverse(sites);
		if(!sites.isEmpty())
			{
			RestrictionSite nextsite=null;
			for(RestrictionSite s:sites)
				if(s.getEarliestPos()<r.from)
					{
					nextsite=s;
					break;
					}
			if(nextsite==null)
				nextsite=sites.get(0);
			selectSite(nextsite);
			}		
		}
	
	/**
	 * Action: Go to next cut site
	 */
	public void actionNextSite()
		{
		SequenceRange r=seqwindow.getSelection();
		if(r==null)
			r=new SequenceRange(0, 0);
		LinkedList<RestrictionSite> sites=getSortedSitesForCurrent();
		if(!sites.isEmpty())
			{
			RestrictionSite nextsite=null;
			for(RestrictionSite s:sites)
				{
				if(s.getEarliestPos()>r.from)
					{
					nextsite=s;
					break;
					}
				}
			if(nextsite==null)
				nextsite=sites.get(0);
			selectSite(nextsite);
			}		
		}
	
	/**
	 * Set selection to cover a cut site
	 */
	private void selectSite(RestrictionSite s)
		{
		seqwindow.emitNewSelection(new SequenceRange(s.getEarliestPos(), s.getLatestPos()+1));
		}
	
	/**
	 * Get all cut sites for current enzyme, or empty list
	 */
	private LinkedList<RestrictionSite> getSortedSitesForCurrent()
		{
		RestrictionEnzyme curenz=getCurrentEnzyme();
		if(curenz!=null)
			{
			LinkedList<RestrictionSite> sites=new LinkedList<RestrictionSite>();
			sites.addAll(seq.getRestrictionSitesFor(curenz));
			Collections.sort(sites, new Comparator<RestrictionSite>(){
				public int compare(RestrictionSite arg0, RestrictionSite arg1)
					{
					return Integer.compare(arg0.getEarliestPos(), arg1.getEarliestPos());
					}
			});
			return sites;
			}
		else
			return new LinkedList<RestrictionSite>();
		}


	public void handleEvent(CollageneEvent ob)
		{
		if(ob instanceof EventSelectedRestrictionEnzyme)
			{
			EventSelectedRestrictionEnzyme enz=(EventSelectedRestrictionEnzyme)ob;
			setRestrictionEnzyme(enz);
			}
		}
	
	}
