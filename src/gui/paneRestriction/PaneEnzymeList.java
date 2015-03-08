package gui.paneRestriction;

import gui.ProjectWindow;
import gui.digest.SimulatedDigestWindow;
import gui.resource.ImgResource;
import gui.sequenceWindow.SeqViewSettingsMenu;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.TreeMap;

import restrictionEnzyme.RestrictionEnzyme;
import seq.AnnotatedSequence;
import seq.RestrictionSite;

import com.trolltech.qt.QSignalEmitter;
import com.trolltech.qt.core.QModelIndex;
import com.trolltech.qt.core.QUrl;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QAbstractItemView.SelectionBehavior;
import com.trolltech.qt.gui.QDesktopServices;
import com.trolltech.qt.gui.QHeaderView.ResizeMode;
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
	private SeqViewSettingsMenu menuSettings=new SeqViewSettingsMenu();
	
	private AnnotatedSequence seq=new AnnotatedSequence();
	//private QGroupBox layInfo2=new QGroupBox("");
	private QLabel lEnzName=new QLabel();
	
	
	private QLabel labTempIncubation=new QLabel();
	private QLabel labTempInactivation=new QLabel();
	private ProjectWindow projwindow;

	public QSignalEmitter.Signal1<SelectedRestrictionEnzyme> signalEnzymeChanged=new Signal1<SelectedRestrictionEnzyme>();
	
	/**
	 * Set current sequence
	 */
	public void setSequence(AnnotatedSequence seq)
		{
		this.seq=seq;
		
		updateView();
		}
	
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
			if(menuSettings.allowsRestrictionSiteCount(enz, sites.size()))
				{
				tableAvailableEnzymes.setRowCount(currow+1);
				QTableWidgetItem it=new QTableWidgetItem(enz.name);
				it.setData(Qt.ItemDataRole.UserRole, enz);
				
				tableAvailableEnzymes.setItem(currow, 0, it);
				tableAvailableEnzymes.setItem(currow, 1, new QTableWidgetItem(""+sites.size()));
				
				currow++;
				}
			}
		}


	
	/**
	 * Constructor
	 */
	public PaneEnzymeList(ProjectWindow projwindow)
		{
		this.projwindow=projwindow;
		
		tableAvailableEnzymes.setColumnCount(2);
		tableAvailableEnzymes.verticalHeader().hide();
		tableAvailableEnzymes.setHorizontalHeaderLabels(Arrays.asList(tr("Enzyme"),tr("#Cuts")));
		tableAvailableEnzymes.setSelectionBehavior(SelectionBehavior.SelectRows);
		tableAvailableEnzymes.horizontalHeader().setResizeMode(ResizeMode.ResizeToContents);
		tableAvailableEnzymes.horizontalHeader().setStretchLastSection(true);		
		
		
		
		
		//TODO a link to neb?

//		layInfo2.setLayout(layInfo);

		
		bMenu.setMenu(menuSettings);
		menuSettings.signalSettingsChanged.connect(this,"updateView()");

		QVBoxLayout lay=new QVBoxLayout();
		lay.addWidget(bMenu);
		lay.addWidget(tableAvailableEnzymes);
		
		lay.addWidget(lEnzName);
		lay.addWidget(pcutsite);
		lay.addWidget(labTempIncubation);
		lay.addWidget(labTempInactivation);
		lay.addWidget(bufOne);

		lay.addWidget(bDigest);
		lay.addLayout(layCommon);
		lay.setMargin(0);
		setLayout(lay);
		layCommon.setMargin(0);
		
		
		
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
		RestrictionEnzyme enz=getCurrentEnzyme();
		if(enz!=null)
			{
			pcutsite.setEnzyme(enz);
			lEnzName.setText("Enzyme "+enz.name+" "+
					(enz.url==null ? "" : "- <a href=\""+enz.url+"\">web</a>"));
			labTempInactivation.setText("Inactivation: "+formatTemp(enz.tempInactivation));
			labTempIncubation.setText("Incubation: "+formatTemp(enz.tempIncubation));

			lEnzName.linkActivated.connect(this,"actionGoWebsite()");
			
			bufOne.fill(enz.bufferEfficiency);
			}

		//Fill up common buffer efficiency table
		Collection<RestrictionEnzyme> selEnzymes=getSelectedEnzymes();
		TreeMap<String,Double> commonEfficiencies=RestrictionEnzyme.getCommonBufferEfficiency(selEnzymes);
		bufCommon.fill(commonEfficiencies);

		if(selEnzymes.size()>1)
			{
			bufCommon.setVisible(true);
			labCommon.setVisible(true);
			layCommon.addWidget(labCommon);
			layCommon.addWidget(bufCommon);
			}
		else
			{
			bufCommon.setVisible(false);
			labCommon.setVisible(false);
			layCommon.removeWidget(labCommon);
			layCommon.removeWidget(bufCommon);
			}
		
		signalEnzymeChanged.emit(getSelection());
		}
	
	private SelectedRestrictionEnzyme getSelection()
		{
		SelectedRestrictionEnzyme e=new SelectedRestrictionEnzyme();
		e.enzymes=getSelectedEnzymes();
		return e;
		}
	
	private String formatTemp(Double t)
		{
		if(t==null)
			return "-";
		else
			return ""+t+"°C";
		}
	
	/**
	 * Get currently selected enzymes.
	 * TODO rewrite fully!
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

	public void setRestrictionEnzyme(SelectedRestrictionEnzyme enz)
		{
		}
	}
