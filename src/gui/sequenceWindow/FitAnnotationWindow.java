package gui.sequenceWindow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import seq.AnnotatedSequence;
import seq.SeqAnnotation;
import gui.ProjectWindow;
import gui.qt.QTutil;

import com.trolltech.qt.core.QModelIndex;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QDialog;
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
public class FitAnnotationWindow extends QDialog
	{
	private QTableWidget tableAnnot=new QTableWidget();
	
	
	private QPushButton bCancel=new QPushButton(tr("Cancel"));
	private QPushButton bOk=new QPushButton(tr("OK"));
	
	public AnnotatedSequence seq;

	
	public static class OneAnnotation
		{
		public AnnotatedSequence seq;
		public SeqAnnotation annot;
		}
	
	public static Collection<OneAnnotation> collectPrimers(ProjectWindow pw)
		{
		LinkedList<OneAnnotation> list=new LinkedList<FitAnnotationWindow.OneAnnotation>();
		for(AnnotatedSequence seq:pw.getProject().sequenceLinkedList)
			{
			//Add entire sequence as an item
			OneAnnotation a=new OneAnnotation();
			a.seq=seq;
			list.add(a);
			
			//Add each individual item
			for(SeqAnnotation annot:seq.annotations)
				{
				a=new OneAnnotation();
				a.seq=seq;
				a.annot=annot;
				list.add(a);
				}
			}
		return list;
		}


	
	public FitAnnotationWindow(ProjectWindow pw)
		{
		tableAnnot.setColumnCount(2);
		tableAnnot.verticalHeader().hide();
		tableAnnot.setHorizontalHeaderLabels(Arrays.asList(tr("Name"),tr("Sequence")));
		tableAnnot.setSelectionBehavior(SelectionBehavior.SelectRows);
		tableAnnot.horizontalHeader().setResizeMode(ResizeMode.ResizeToContents);
		tableAnnot.horizontalHeader().setStretchLastSection(true);

		ArrayList<OneAnnotation> list=new ArrayList<OneAnnotation>(collectPrimers(pw));
		tableAnnot.setRowCount(list.size());
		for(int row=0;row<list.size();row++)
			{
			OneAnnotation p=list.get(row);
			QTableWidgetItem itSequence=QTutil.createReadOnlyItem(p.seq.name);
			QTableWidgetItem itName;
			if(p.annot!=null)
				itName=QTutil.createReadOnlyItem(p.annot.name);
			else
				itName=QTutil.createReadOnlyItem("<"+tr("Whole sequence")+">");
			tableAnnot.setItem(row, 0, itSequence);
			tableAnnot.setItem(row, 1, itName);
			itSequence.setData(Qt.ItemDataRole.UserRole, p);
			}
		
		
		bOk.clicked.connect(this,"actionOK()");
		bCancel.clicked.connect(this,"actionCancel()");

		setLayout(QTutil.layoutVertical(tableAnnot,QTutil.layoutHorizontal(bCancel, bOk)));
		
		bOk.setDefault(true);
		setMinimumWidth(500);
		setMinimumHeight(400);
		}
	
	public boolean wasOk=false;

	public void doit()
		{
		LinkedList<OneAnnotation> list=new LinkedList<FitAnnotationWindow.OneAnnotation>();
		for(QModelIndex ind:tableAnnot.selectionModel().selectedRows())
			list.add((OneAnnotation)tableAnnot.item(ind.row(),0).data(Qt.ItemDataRole.UserRole));
		System.out.println(list);
		
		}
	
	
	public void actionOK()
		{
		doit();
		wasOk=true;
		close();
		}

	public void actionCancel()
		{
		close();
		}
	
	}
