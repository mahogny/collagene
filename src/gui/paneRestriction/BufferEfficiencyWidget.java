package gui.paneRestriction;

import java.util.LinkedList;
import java.util.TreeMap;

import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QWidget;

/**
 * Widget displaying buffer efficiencies
 * 
 * @author Johan Henriksson
 *
 */
public class BufferEfficiencyWidget extends QWidget
	{
	private QGridLayout layBufferEff=new QGridLayout();
	private LinkedList<QWidget> widgBufferEff=new LinkedList<QWidget>();

	public BufferEfficiencyWidget()
		{
		setLayout(layBufferEff);
		layBufferEff.setMargin(0);
		}
	
	public void fill(TreeMap<String, Double> bufferEfficiency)
		{
		//Clear currently shown widgets for buffer efficiency
		for(QWidget w:widgBufferEff)
			{
			w.hide();
			layBufferEff.removeWidget(w);
			}
		widgBufferEff.clear();

		//Fill up buffer efficiency table
		int c=0;
		for(String buf:bufferEfficiency.keySet())
			{
			QLabel a=new QLabel("<b>"+buf+"</b>");
			QLabel b=new QLabel(""+bufferEfficiency.get(buf));
			layBufferEff.addWidget(a,0,c);
			layBufferEff.addWidget(b,1,c);
			widgBufferEff.add(a);
			widgBufferEff.add(b);
			c++;
			}
		}
	}
