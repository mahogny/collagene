package gui.digest;

import gui.ProjectWindow;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

import ladder.DNALadder;
import ladder.DNALadderSet;
import restrictionEnzyme.DigestSimulator;
import restrictionEnzyme.RestrictionDigestFragment;
import restrictionEnzyme.RestrictionEnzyme;
import seq.AnnotatedSequence;

import com.trolltech.qt.core.QModelIndex;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.core.Qt.ItemFlag;
import com.trolltech.qt.core.Qt.ItemFlags;
import com.trolltech.qt.gui.QAbstractItemView.SelectionBehavior;
import com.trolltech.qt.gui.QComboBox;
import com.trolltech.qt.gui.QHBoxLayout;
import com.trolltech.qt.gui.QHeaderView.ResizeMode;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QTableWidget;
import com.trolltech.qt.gui.QTableWidgetItem;
import com.trolltech.qt.gui.QVBoxLayout;
import com.trolltech.qt.gui.QWidget;

/**
 * 
 * Window for a simulated digest
 * 
 * @author Johan Henriksson
 *
 */
public class SimulatedDigestWindow extends QWidget
	{
	private QComboBox comboLadder=new QComboBox();
	private QTableWidget tableSeqs=new QTableWidget();
	private QPushButton bClose=new QPushButton(tr("Close"));
	private QPushButton bPickSequence=new QPushButton(tr("Pick"));
	private ProjectWindow projwindow;
	
	private DNALadderSet ladders=new DNALadderSet();
	private SimulatedGel wget=new SimulatedGel();
	private SimulatedLane lane2=new SimulatedLane();
	
	/**
	 * Constructor
	 */
	public SimulatedDigestWindow(ProjectWindow projwindow)
		{
		this.projwindow=projwindow;
		try
			{
			ladders.load();
			}
		catch (IOException e)
			{
			e.printStackTrace();
			}

		for(DNALadder lad:ladders.ladders)
			comboLadder.addItem(lad.name, lad);
		comboLadder.currentIndexChanged.connect(this,"actionChooseLadder()");
		
		tableSeqs.setColumnCount(5);
		tableSeqs.verticalHeader().hide();
		tableSeqs.setHorizontalHeaderLabels(Arrays.asList(tr("From"),tr("To"),tr("Length"),tr("Enzyme 1"),tr("Enzyme 2")));
		tableSeqs.setSelectionBehavior(SelectionBehavior.SelectRows);
		tableSeqs.horizontalHeader().setResizeMode(ResizeMode.ResizeToContents);
		tableSeqs.horizontalHeader().setStretchLastSection(true);		
		tableSeqs.doubleClicked.connect(this,"actionPick()");
		
		bClose.clicked.connect(this,"close()");
		bPickSequence.clicked.connect(this,"actionPick()");

		QHBoxLayout layButtons=new QHBoxLayout();
		layButtons.addWidget(bPickSequence);
		layButtons.addWidget(bClose);

		QVBoxLayout layLadder=new QVBoxLayout();
		layLadder.addWidget(comboLadder);
		layLadder.addWidget(wget);

		QHBoxLayout layHoriz=new QHBoxLayout();
		layHoriz.addLayout(layLadder);
		layHoriz.addWidget(tableSeqs);

		QVBoxLayout totlay=new QVBoxLayout();
		totlay.addLayout(layHoriz);
		totlay.addLayout(layButtons);
		setLayout(totlay);

		setMinimumWidth(500);
		updategraphics();
		}
	
	public RestrictionDigestFragment getSelected()
		{
		for(QModelIndex i:tableSeqs.selectionModel().selectedRows())
			{
			RestrictionDigestFragment f=(RestrictionDigestFragment)tableSeqs.item(i.row(),0).data(Qt.ItemDataRole.UserRole);
			return f;
			}
		return null;
		}
	
	/**
	 * Action: Pick sequence for further editing
	 */
	public void actionPick()
		{
		RestrictionDigestFragment f=getSelected();
		if(f!=null)
			{
			AnnotatedSequence s=f.getFragmentSequence();
			projwindow.giveNewName(s);
			projwindow.addSequenceToProject(s);
			projwindow.showSequence(s);
			}
		}
	
	/**
	 * Action: Select another ladder
	 */
	public void actionChooseLadder()
		{
		updategraphics();
		}
	
	

	/**
	 * Set sequence to digest
	 */
	public void setSequence(AnnotatedSequence seq, LinkedList<RestrictionEnzyme> enzymes)
		{
		DigestSimulator d=new DigestSimulator();
		d.simulate(seq,enzymes);

		//Add all fragments to lane
		lane2.mapPosWeight.clear();
		for(RestrictionDigestFragment r:d.cutregions)
			lane2.mapPosWeight.put((double)r.getUpperLength(), 1.0);
	
		//Add all fragments to table
		while(tableSeqs.rowCount()>0)
			tableSeqs.removeRow(0);
		tableSeqs.setRowCount(d.cutregions.size());
		for(int i=0;i<d.cutregions.size();i++)
			{
			RestrictionDigestFragment r=d.cutregions.get(i);
			
			QTableWidgetItem it=roItem(""+r.getUpperFrom());
			it.setData(Qt.ItemDataRole.UserRole, r);
			
			tableSeqs.setItem(i, 0, it);
			tableSeqs.setItem(i, 1, roItem(""+r.getUpperTo()));
			tableSeqs.setItem(i, 2, roItem(""+r.getUpperLength()));

			tableSeqs.setItem(i, 3, roItem(r.fromSite==null ? "N/A" : r.fromSite.enzyme.name));
			tableSeqs.setItem(i, 4, roItem(r.toSite==null ? "N/A" : r.toSite.enzyme.name));
			}
		
		
		updategraphics();
		}
	
	private QTableWidgetItem roItem(String s)
		{
		QTableWidgetItem it=new QTableWidgetItem(s);
		it.setFlags(new ItemFlags(ItemFlag.ItemIsSelectable, ItemFlag.ItemIsEnabled));
		return it;
		}

	/**
	 * 
	 */
	private void updategraphics()
		{
		DNALadder ladder=ladders.get(comboLadder.currentIndex());
		wget.lane.clear();
		wget.addLane(new SimulatedLane(ladder));
		wget.addLane(lane2);
		}
	
	}
