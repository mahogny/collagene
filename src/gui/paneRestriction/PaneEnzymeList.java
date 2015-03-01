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

import com.trolltech.qt.core.QModelIndex;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QAbstractItemView.SelectionBehavior;
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
	BufferEfficiencyWidget bufOne=new BufferEfficiencyWidget();
	BufferEfficiencyWidget bufCommon=new BufferEfficiencyWidget();
	
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
		lay.addWidget(new QLabel(tr("Common buffers")));
		lay.addWidget(bufCommon);
		setLayout(lay);
		
		
		
		tableAvailableEnzymes.selectionModel().selectionChanged.connect(this,"actionSelectedEnzyme()");
		
		bDigest.clicked.connect(this,"actionDigest()");
		
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
	 * Action: An enzyme was selected
	 */
	public void actionSelectedEnzyme()
		{
		RestrictionEnzyme enz=getCurrentEnzyme();
		if(enz!=null)
			{
			pcutsite.setEnzyme(enz);
			lEnzName.setText("Enzyme "+enz.name);
			labTempInactivation.setText("Inactivation: "+formatTemp(enz.tempInactivation));
			labTempIncubation.setText("Incubation: "+formatTemp(enz.tempIncubation));

			bufOne.fill(enz.bufferEfficiency);
			}

		//Fill up common buffer efficiency table
		Collection<RestrictionEnzyme> selEnzymes=getSelectedEnzymes();
		TreeMap<String,Double> commonEfficiencies=RestrictionEnzyme.getCommonBufferEfficiency(selEnzymes);
		bufCommon.fill(commonEfficiencies);
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
	}
