package gui.cloneAssembler;

import com.trolltech.qt.gui.QHBoxLayout;
import com.trolltech.qt.gui.QLineEdit;

/**
 * 
 * Assembly widget: For adding a spacer sequence
 * 
 * @author Johan Henriksson
 *
 */
public class AssemblyWidgetSpacer extends AssemblyWidget
	{
	
	QLineEdit tfseq=new QLineEdit();
	
	public AssemblyWidgetSpacer()
		{
		QHBoxLayout lay=new QHBoxLayout();
		lay.addWidget(tfseq);
		setLayout(lay);
		}
	
	

	}
