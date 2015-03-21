package gui.paneLinear;

import seq.AnnotatedSequence;
import sequtil.ProteinTranslator;

import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.core.QRectF;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QFont;
import com.trolltech.qt.gui.QGraphicsRectItem;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QStyleOptionGraphicsItem;
import com.trolltech.qt.gui.QWidget;

/**
 * 
 * One line of DNA and more
 * 
 * @author Johan Henriksson
 *
 */
public class QGraphicsLinSequenceItem extends QGraphicsRectItem
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
				painter.drawText(new QPointF(view.mapCharToX(i), currentY+fonth()), ""+letterUpper);
				painter.drawText(new QPointF(view.mapCharToX(i), currentY+fonth()*2), ""+letterLower);
				}
			}

		//Draw protein translation beneath
		currentY+=fonth()*2;
		if(view.showProteinTranslation)
			{
			QFont font=new QFont(view.fontSequence);
			ProteinTranslator ptrans=new ProteinTranslator();
			for(int frame=0;frame<3;frame++)
				{
				font.setBold((frame+1)%3+1==view.currentReadingFrame);
				painter.setFont(font);
				for(int i=frame;i<charsPerLine;i+=3)
					{
					int cpos=curline*charsPerLine + i - 1;
					int cpos2=cpos+3;
					if(cpos>=0 && cpos2<=seq.getLength())
						{
						double x1=view.mapCharToX(i-1)+1;
						double x2=view.mapCharToX(i+2)-1;
						painter.fillRect(new QRectF(
								x1, currentY+fonth()*(frame)+5, 
								x2-x1, fonth()-2), QColor.fromRgb(200,200,255));
						
						String triplet=seq.getSequence().substring(cpos,cpos2);
						painter.drawText(new QPointF(view.mapCharToX(i), currentY+fonth()*(1+frame)), ptrans.tripletToAminoLetter(triplet));
						}
					}			
				}
			currentY+=fonth()*3+5;
			}
		}

	@Override
	public QRectF boundingRect()
		{
		double h=fonth()*2;
		if(view.showProteinTranslation)
			h+=fonth()*3+5;
		return new QRectF(0,currentY, 100000, h);
		}
	}