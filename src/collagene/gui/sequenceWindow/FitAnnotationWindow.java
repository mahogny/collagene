package collagene.gui.sequenceWindow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import collagene.alignment.AnnotatedSequenceAlignment;
import collagene.gui.ProjectWindow;
import collagene.gui.colors.ColorSet;
import collagene.gui.qt.QTutil;
import collagene.seq.AnnotatedSequence;
import collagene.seq.Orientation;
import collagene.seq.SeqAnnotation;
import collagene.seq.SequenceRange;
import collagene.sequtil.NucleotideUtil;

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
 * Dialog to fit existing annotation
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
	SequenceWindow sw;


	private ProjectWindow pw;

	
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


	
	public FitAnnotationWindow(ProjectWindow pw, SequenceWindow sw)
		{
		this.pw=pw;
		this.sw=sw;
		
		
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
		
		
		AnnotatedSequence seqA=sw.getSequence();
		for(OneAnnotation annot:list)
			{
			//Get sequence normalized to forward orientation
			AnnotatedSequence seqB=new AnnotatedSequence();
			Orientation orientation=Orientation.FORWARD;
			SeqAnnotation prevannot=new SeqAnnotation();
			prevannot.color=ColorSet.colorset.getRandomColor();
			prevannot.name=annot.seq.name;
			if(annot.annot!=null)
				{
				String seq=annot.seq.getSequence(annot.annot.range);
				if(annot.annot.orientation==Orientation.REVERSE)
					seq=NucleotideUtil.revcomplement(seq);
				seqB.setSequence(seq);
				if(annot.annot.orientation==Orientation.NOTORIENTED)
					orientation=Orientation.NOTORIENTED;
				prevannot=annot.annot;
				}
			else
				{
				seqB.setSequence(annot.seq.getSequence());
				}
			
			
			AnnotatedSequenceAlignment al=new AnnotatedSequenceAlignment();
			al.isLocal=false;
			al.canGoOutside=true;
			al.align(seqA, seqB);

			if(al.rotateB)
				orientation=Orientation.reverse(orientation);

			int posFirst=firstCharOfSeq(al.alSeqB.getSequence());
			int posLast=lastCharOfSeq(al.alSeqB.getSequence());
			
			SeqAnnotation newannot=new SeqAnnotation();
			newannot.orientation=orientation;
			newannot.range=new SequenceRange(al.bestal.alignedIndexA.get(posFirst), al.bestal.alignedIndexA.get(posLast));
			newannot.color=prevannot.color;
			newannot.name=prevannot.name;
			newannot.desc=prevannot.desc;
			
			System.out.println(al.bestal.alignedSequenceA+"!");
			System.out.println(al.bestal.alignedSequenceB+"!");
			
			System.out.println("cost: "+al.bestal.bestCost); //TODO compute best possible cost?
			sw.getSequence().addAnnotation(newannot);
			pw.updateEvent(new EventSequenceModified(seqA));
			}
		
		
		}
	
	
	public int firstCharOfSeq(String s)
		{
		for(int i=0;i<s.length();i++)
			if(s.charAt(i)!=' ')
				return i;
		return s.length();
		}

	public int lastCharOfSeq(String s)
		{
		for(int i=s.length()-1;i>=0;i--)
			if(s.charAt(i)!=' ')
				return i;
		return 0;
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
	