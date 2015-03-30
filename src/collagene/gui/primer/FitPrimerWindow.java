package collagene.gui.primer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

import collagene.gui.ProjectWindow;
import collagene.gui.qt.QTutil;
import collagene.primer.Primer;
import collagene.seq.AnnotatedSequence;

import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QLineEdit;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QTableWidget;
import com.trolltech.qt.gui.QTableWidgetItem;
import com.trolltech.qt.gui.QAbstractItemView.SelectionBehavior;
import com.trolltech.qt.gui.QHeaderView.ResizeMode;

/**
 * 
 * Dialog to fit an existing primer
 * 
 * @author Johan Henriksson
 *
 */
public class FitPrimerWindow extends QDialog
	{
	private QTableWidget tablePrimers=new QTableWidget();
	
	
	private QLineEdit tfName=new QLineEdit();
	private QLineEdit tfSequence=new QLineEdit();
	private QPushButton bCancel=new QPushButton(tr("Cancel"));
	private QPushButton bOk=new QPushButton(tr("OK"));
	
	public AnnotatedSequence seq;
	private Primer primer=new Primer();

	
	
	public static Collection<Primer> collectPrimers(ProjectWindow pw)
		{
		return collectPrimers(pw.getProject().sequenceLinkedList);
		}
	public static Collection<Primer> collectPrimers(Collection<AnnotatedSequence> seqs)
		{
		TreeSet<Primer> list=new TreeSet<Primer>(new Comparator<Primer>()
			{
			public int compare(Primer a, Primer b)
				{
				int v=a.name.compareTo(b.name);
				if(v==0)
					return a.sequence.compareTo(b.sequence);
				else
					return v;
				}
			});
		for(AnnotatedSequence seq:seqs)
			{
			//Optionally, could keep a list of where they come from too
			for(Primer p:seq.primers)
				list.add(p);
			}
		System.out.println(list);
		return list;
		}


	public void actionSelectedPrimer()
		{
		int row=tablePrimers.currentRow();
		Primer p=(Primer)tablePrimers.item(row,0).data(Qt.ItemDataRole.UserRole);
		tfName.setText(p.name);
		tfSequence.setText(p.sequence);
		}
	
	public FitPrimerWindow(ProjectWindow pw)
		{
		tablePrimers.setColumnCount(2);
		tablePrimers.verticalHeader().hide();
		tablePrimers.setHorizontalHeaderLabels(Arrays.asList(tr("Name"),tr("Sequence")));
		tablePrimers.setSelectionBehavior(SelectionBehavior.SelectRows);
		tablePrimers.horizontalHeader().setResizeMode(ResizeMode.ResizeToContents);
		tablePrimers.horizontalHeader().setStretchLastSection(true);
		tablePrimers.selectionModel().selectionChanged.connect(this,"actionSelectedPrimer()");
		
		ArrayList<Primer> list=new ArrayList<Primer>(collectPrimers(pw));
		tablePrimers.setRowCount(list.size());
		for(int row=0;row<list.size();row++)
			{
			Primer p=list.get(row);
			QTableWidgetItem itName=QTutil.createReadOnlyItem(p.name);
			QTableWidgetItem itSeq=QTutil.createReadOnlyItem(p.sequence);
			tablePrimers.setItem(row, 0, itName);
			tablePrimers.setItem(row, 1, itSeq);
			itName.setData(Qt.ItemDataRole.UserRole, p);
			}
		
		
		QGridLayout lay=new QGridLayout();
		lay.addWidget(new QLabel(tr("Name:")), 0, 0);
		lay.addWidget(new QLabel(tr("Sequence:")), 1, 0);
		lay.addWidget(tfName, 0, 1);
		lay.addWidget(tfSequence, 1, 1);

		bOk.clicked.connect(this,"actionOK()");
		bCancel.clicked.connect(this,"actionCancel()");

		setLayout(QTutil.layoutVertical(tablePrimers,lay,QTutil.layoutHorizontal(bCancel, bOk)));
		
		bOk.setDefault(true);
		setMinimumWidth(500);
		}
	
	public boolean wasOk=false;
	
	public void actionOK()
		{
		if(!tfName.text().equals("") && !tfSequence.text().equals(""))
			{
			primer.name=tfName.text();
			primer.sequence=tfSequence.text();
			wasOk=true;
			close();
			}
		}

	public void actionCancel()
		{
		close();
		}
	
	public void setPrimer(Primer p)
		{
		primer=p;
		tfName.setText(p.name);
		tfSequence.setText(p.sequence);
		}
	
	public Primer getPrimer()
		{
		if(wasOk)
			return primer;
		else
			return null;
		}
		
	}
