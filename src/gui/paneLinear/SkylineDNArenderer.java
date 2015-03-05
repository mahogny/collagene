package gui.paneLinear;

import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QPolygonF;

/**
 * 
 * http://www.biotechniques.com/BiotechniquesJournal/2006/June/DNA-Skyline-fonts-to-facilitate-visual-inspection-of-nucleic-acid-sequences/biotechniques-39569.html
 * 
 * @author Johan Henriksson
 *
 */
public class SkylineDNArenderer
	{

	

	public void draw(QPainter painter, double w, double h, QPointF p, char letter)
		{
		int ind="GATC".indexOf(letter);
		if(ind>=0)
			{
			painter.setBrush(QColor.fromRgb(0,0,0));
			double y1=-h+p.y();
			double y2=-h+p.y()+h/8;
			double y3=-h+p.y()+h/4;
			double ly=-h+p.y()+(ind+1)*h/4;

			double dx=w/4;
			
			QPolygonF poly=new QPolygonF();
			poly.add(p.x(), y1);
			
			//the right >
			poly.add(p.x()+w,    y1);
			poly.add(p.x()+w+dx, y2);
			poly.add(p.x()+w,    y3);
			
			//Down
			poly.add(p.x()+w, ly);
			poly.add(p.x(), ly);

			//the left >
			poly.add(p.x(), y3);
			poly.add(p.x()+dx, y2);

			painter.drawPolygon(poly);
			}
		else
			{
			painter.drawText(p, ""+letter);
			}
		}
	}
