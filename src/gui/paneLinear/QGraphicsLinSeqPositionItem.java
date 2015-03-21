package gui.paneLinear;

import seq.AnnotatedSequence;

import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.core.QRectF;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QGraphicsRectItem;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QPen;
import com.trolltech.qt.gui.QStyleOptionGraphicsItem;
import com.trolltech.qt.gui.QWidget;

/**
 * 
 * Ruler showing positions
 * 
 * @author Johan Henriksson
 *
 */
public class QGraphicsLinSeqPositionItem extends QGraphicsRectItem
	{
	public AnnotatedSequence seq;
	
	int currentY;
	int curline;
	ViewLinearSequence view;

	
	public double fonth()
		{
		return view.charHeight-1;
		}
	
	public void paint(QPainter painter, QStyleOptionGraphicsItem option, QWidget widget) 
		{
		int charsPerLine=view.charsPerLine;

		int currentY=this.currentY;
		
		//Draw positions next
		if(view.showPositionRuler)
			{
			QPen pen=new QPen();
			pen.setColor(QColor.fromRgb(0,0,0));
			painter.setPen(pen);
			painter.setFont(view.fontSequence);

			int skippos=20; //automatically figure out a good number
			for(int i=0;i<=charsPerLine;i+=skippos)
				{
				int cpos=curline*charsPerLine + i;
				if(cpos>=seq.getLength())
					break;


				
				double x1=view.mapCharToX(i);//+view.charWidth/2;
				double y1=currentY;
				double y2=y1+10; //Zoom?
				double y3=y2+fonth();

				painter.drawLine(new QPointF(x1,y1),new QPointF(x1,y2));
				String txt=""+(cpos);
				double txtW=painter.fontMetrics().width(txt);
				painter.drawText(new QPointF(x1-txtW/2,y3), txt);
				}
			currentY+=10+fonth();
			}
		
		
		}

	@Override
	public QRectF boundingRect()
		{
		double h=0;
		if(view.showPositionRuler)
			h+=10+fonth();
		return new QRectF(0,currentY, 100000, h);
		}
	}
