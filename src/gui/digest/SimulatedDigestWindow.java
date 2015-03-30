package gui.digest;

import gui.ProjectWindow;
import gui.paneLinear.PaneLinearSequence;
import gui.qt.QTutil;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

import ladder.DNALadder;
import ladder.DNALadderSet;
import restrictionEnzyme.DigestSimulator;
import restrictionEnzyme.RestrictionEnzyme;
import restrictionEnzyme.SequenceFragment;
import seq.AnnotatedSequence;

import com.trolltech.qt.core.QModelIndex;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.core.Qt.ItemFlag;
import com.trolltech.qt.core.Qt.ItemFlags;
import com.trolltech.qt.core.Qt.Orientation;
import com.trolltech.qt.gui.QAbstractItemView.SelectionBehavior;
import com.trolltech.qt.gui.QComboBox;
import com.trolltech.qt.gui.QHBoxLayout;
import com.trolltech.qt.gui.QHeaderView.ResizeMode;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QSizePolicy.Policy;
import com.trolltech.qt.gui.QSlider;
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
	private QPushButton bPickSequence=new QPushButton(tr("Add to workspace"));
	private ProjectWindow projwindow;
	
	private DNALadderSet ladders=new DNALadderSet();
	private SimulatedGel wGel=new SimulatedGel();
	private SimulatedLane lane2=new SimulatedLane();
	
	private PaneLinearSequence paneSeq;

	private QSlider zoomGel=new QSlider(Orientation.Vertical);
	
	/**
	 * Constructor
	 */
	public SimulatedDigestWindow(ProjectWindow projwindow)
		{
		this.projwindow=projwindow;
		
		paneSeq=new PaneLinearSequence(projwindow);
		paneSeq.setEditable(false);
		paneSeq.setFullsizeMode(true);
		
		zoomGel.setRange(0, 10000);
		zoomGel.setValue(9000);
		
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
		tableSeqs.selectionModel().selectionChanged.connect(this,"actionShowSeq()");
		tableSeqs.doubleClicked.connect(this,"actionPick()");

		tableSeqs.setSizePolicy(Policy.Expanding, Policy.MinimumExpanding);
		comboLadder.setSizePolicy(Policy.MinimumExpanding, Policy.Minimum);
		wGel.setSizePolicy(Policy.Minimum, Policy.MinimumExpanding);
		
		zoomGel.sliderMoved.connect(this,"updateGel()");
		
		bClose.clicked.connect(this,"close()");
		bPickSequence.clicked.connect(this,"actionPick()");

		QHBoxLayout layButtons=new QHBoxLayout();
		layButtons.addStretch();
		layButtons.addWidget(bPickSequence);
		layButtons.addWidget(bClose);
		layButtons.setSpacing(2);
		layButtons.setMargin(0);

		QVBoxLayout layRight=new QVBoxLayout();
		layRight.addWidget(tableSeqs);
		layRight.addWidget(paneSeq);
		layRight.setSpacing(2);
		layRight.setMargin(0);
		
		QVBoxLayout layLeft=new QVBoxLayout();
		layLeft.addWidget(comboLadder);
		layLeft.addLayout(QTutil.layoutHorizontal(zoomGel,wGel));
		layLeft.setSpacing(2);
		layLeft.setMargin(0);

		QHBoxLayout layHoriz=new QHBoxLayout();
		layHoriz.addLayout(layLeft);
		layHoriz.addLayout(layRight);
		layHoriz.setSpacing(2);
		layHoriz.setMargin(0);

		QVBoxLayout totlay=new QVBoxLayout();
		totlay.addLayout(layHoriz);
		totlay.addLayout(layButtons);
		totlay.setSpacing(2);
		setLayout(totlay);

		tableSeqs.setMinimumWidth(500);
		setMinimumWidth(500);
		updategraphics();
		}
	
	public SequenceFragment getSelected()
		{
		for(QModelIndex i:tableSeqs.selectionModel().selectedRows())
			{
			SequenceFragment f=(SequenceFragment)tableSeqs.item(i.row(),0).data(Qt.ItemDataRole.UserRole);
			return f;
			}
		return null;
		}
	
	/**
	 * Action: Pick sequence for further editing
	 */
	public void actionPick()
		{
		SequenceFragment f=getSelected();
		if(f!=null)
			{
			AnnotatedSequence s=f.getFragmentSequence();
			projwindow.giveNewName(s);
			projwindow.addSequenceToProject(s);
			projwindow.showSequence(s);
			}
		}
	
	public void actionShowSeq()
		{
		SequenceFragment f=getSelected();
		if(f!=null)
			{
			AnnotatedSequence s=f.getFragmentSequence();
			paneSeq.setSequence(s);
			paneSeq.setVisible(true);
			wGel.setCurrent((double)f.getUpperLength());
			}
		else
			{
			paneSeq.setVisible(false);
			wGel.setCurrent(null);
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

		setFragments(d.cutregions);
		}

	
	public void setFragments(LinkedList<? extends SequenceFragment> cutregions)
		{
		//Add all fragments to lane
		lane2.mapPosWeight.clear();
		for(SequenceFragment r:cutregions)
			lane2.mapPosWeight.put((double)r.getUpperLength(), 1.0);

		//Add all fragments to table
		while(tableSeqs.rowCount()>0)
			tableSeqs.removeRow(0);
		tableSeqs.setRowCount(cutregions.size());
		for(int i=0;i<cutregions.size();i++)
			{
			SequenceFragment r=cutregions.get(i);
			
			QTableWidgetItem it=roItem(""+r.getUpperFrom());
			it.setData(Qt.ItemDataRole.UserRole, r);
			
			tableSeqs.setItem(i, 0, it);
			tableSeqs.setItem(i, 1, roItem(""+r.getUpperTo()));
			tableSeqs.setItem(i, 2, roItem(""+r.getUpperLength()));

			tableSeqs.setItem(i, 3, roItem(r.getFromSite()==null ? "N/A" : r.getFromSite().enzyme.name));
			tableSeqs.setItem(i, 4, roItem(r.getToSite()==null ? "N/A" : r.getToSite().enzyme.name));
			}
		updategraphics();
		}
	
	
	public void setFragment(SequenceFragment f)
		{
		LinkedList<SequenceFragment> list=new LinkedList<SequenceFragment>();
		list.add(f);
		setFragments(list);
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
		updateGel();
		actionShowSeq();
		}
	
	public void updateGel()
		{
		DNALadder ladder=ladders.get(comboLadder.currentIndex());
		wGel.zoom=(10000-zoomGel.value())/1000.0;
		wGel.clearLanes();
		wGel.addLane(new SimulatedLane(ladder));
		wGel.addLane(lane2); 
		}
	}
