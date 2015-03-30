package gui.dotplot;

import com.trolltech.qt.gui.QImage;
import com.trolltech.qt.gui.QImage.Format;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QPaintEvent;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QWidget;

/**
 * 
 * figure out http://bioinfo.lifl.fr/yass/yass.php
 * http://nar.oxfordjournals.org/cgi/content/abstract/33/suppl_2/W540
 * 
 * @author Johan Henriksson
 *
 */
public class DotplotView extends QWidget
	{
	String seqA, seqB;
	QImage im;
	
	//need: window size, % overlap threshold
	int windowSize=2;
	double percOverlapThreshold=0.9;
	
	
	public void compute()
		{
		im=new QImage(seqA.length(), seqB.length(), Format.Format_RGB32);
		QPainter p=new QPainter(im);
		p.setPen(QColor.black);
		//Features on top and left? Need to pull out those views, same part as for making PDFs
		
		int w=windowSize*2+1;
		int overlapth=(int)(w*w*percOverlapThreshold);
		
		//Borders, how to deal with them?
		for(int i=windowSize;i<seqA.length()-windowSize;i++)
			{
			for(int j=windowSize;j<seqB.length()-windowSize;j++)
				{
				int cnt=0;
				for(int k=-windowSize;k<=windowSize;k++)
					{
					if(seqA.charAt(i+k)==seqB.charAt(j+k))
						cnt++;
					}
				if(cnt>overlapth)
					p.drawPoint(i, j);
				}
			}
		p.end();
		}
	
	@Override
	protected void paintEvent(QPaintEvent e)
		{
		super.paintEvent(e);
		QPainter pm=new QPainter(this);
		if(im!=null)
			pm.drawImage(0, 0, im);		
		pm.end();
		}
	}
