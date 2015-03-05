package gui.paneLinear;

import seq.AnnotatedSequence;
import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.core.QRectF;
import com.trolltech.qt.gui.QGraphicsRectItem;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QStyleOptionGraphicsItem;
import com.trolltech.qt.gui.QWidget;

/**
 * 
 * 
 * @author Johan Henriksson
 *
 */
public class QGraphicsLinSeqTextAnnotationItem extends QGraphicsRectItem
	{
	public AnnotatedSequence seq;
	
	int currentY;
	int curline;
	ViewLinearSequence view;
	
	public void paint(QPainter painter, QStyleOptionGraphicsItem option, QWidget widget) 
		{
		int charsPerLine=view.charsPerLine;
		
		SkylineDNArenderer p=new SkylineDNArenderer();
		
		//Draw the sequence text
		for(int i=0;i<charsPerLine;i++)
			{
			int cpos=curline*charsPerLine + i;
			if(cpos>=seq.getLength())
				break;
			char letterUpper=seq.getSequence().charAt(cpos);
			char letterLower=seq.getSequenceLower().charAt(cpos);

			painter.setFont(view.fontSequence);
			if(view.settings.showSkyline)
				{
				double w=view.fontSequence.pointSizeF();
				double h=view.fontSequence.pointSizeF()*1.5;
				p.draw(painter, w, h, new QPointF(view.mapCharToX(i), currentY+view.charHeight), letterUpper);
				p.draw(painter, w, h, new QPointF(view.mapCharToX(i), currentY+view.charHeight*2-2), letterLower);
				}
			else
				{
				painter.drawText(new QPointF(view.mapCharToX(i), currentY+view.charHeight), ""+letterUpper);
				painter.drawText(new QPointF(view.mapCharToX(i), currentY+view.charHeight*2-2), ""+letterLower);
				}
			}

		
		}

	@Override
	public QRectF boundingRect()
		{
		return new QRectF(0,currentY, 100000, currentY+view.charHeight*2);
		}
	}
