package gui.cloneAssembler;

import gui.cloneAssembler.assembly.VectorAssembly;

import java.util.LinkedList;

import com.trolltech.qt.gui.QMainWindow;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QVBoxLayout;
import com.trolltech.qt.gui.QWidget;

/**
 * 
 * Window to assemble new clone. By PCR, Gibson, maybe other things too
 * 
 * mod <add> mod <add> mod
 * 
 * @author Johan Henriksson
 *
 */
public class CloneAssembler extends QMainWindow
	{
	VectorAssembly assembly=new VectorAssembly();
	//possible to make it neutral vs target?
	
	
	LinkedList<AssemblyWidget> widgets=new LinkedList<AssemblyWidget>();

	QVBoxLayout lay=new QVBoxLayout();

	
	public CloneAssembler()
		{
		QPushButton bAssemble=new QPushButton(tr("Assemble"));
		

		QWidget w=new QWidget();
		setCentralWidget(w);
		
		QVBoxLayout lay2=new QVBoxLayout();
		w.setLayout(lay2);

		lay2.addWidget(bAssemble);
		lay2.addLayout(lay);
		
//		updateGUI();
		
		setMinimumHeight(100);
		setMinimumWidth(100);
		show();
		}
	
	public void addComponent(AssemblyWidget w)
		{
		lay.addWidget(w);
		}
	
	/*
	public void updateGUI()
		{
		for(AssemblyWidget w:widgets)
			{
			lay.addWidget(w);
			}		
		}
	*/

	
	
	
	}
