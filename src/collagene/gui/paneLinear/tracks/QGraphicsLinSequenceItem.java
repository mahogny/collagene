package collagene.gui.paneLinear.tracks;

import collagene.gui.paneLinear.SkylineDNArenderer;
import collagene.gui.paneLinear.ViewLinearSequence;
import collagene.seq.AnnotatedSequence;
import collagene.sequtil.NucleotideUtil;
import collagene.sequtil.ProteinTranslator;

import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.core.QRectF;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QFont;
import com.trolltech.qt.gui.QGraphicsRectItem;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QPen;
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

	public int cposLeft;
	public int currentY;
	public ViewLinearSequence view;
	
	LinTrackSequence track;
	double charHeight;
	QFont fontSequence;

	public double fonth()
		{
		return charHeight-1;
		}
	
	public void paint(QPainter painter, QStyleOptionGraphicsItem option, QWidget widget) 
		{
		int charsPerLine=view.charsPerLine;

		int currentY=this.currentY;
		
		if(fontSequence.pointSizeF()<2)
			{
			painter.setPen(QColor.fromRgb(0,0,0));
			int lastx=Math.min(charsPerLine, seq.getLength());
			painter.drawLine(
					new QPointF(view.mapCharToX(0), currentY),
					new QPointF(view.mapCharToX(lastx), currentY));
			return;
			}
		
		SkylineDNArenderer p=new SkylineDNArenderer();
		
		QFont fontMismatch=new QFont(fontSequence);
		fontMismatch.setBold(true);
		QPen penOK=new QPen();
		QPen penMismatch=new QPen();
		penMismatch.setColor(QColor.fromRgb(255, 0, 0));
		
		//Draw the sequence text
		for(int i=0;i<charsPerLine;i++)
			{
			//int cpos=curline*charsPerLine + i;
			int cpos=cposLeft + i;
			if(cpos>=seq.getLength())
				break;
			char letterUpper=seq.getSequence().charAt(cpos);
			char letterLower=seq.getSequenceLower().charAt(cpos);

			painter.setPen(penOK);
			painter.setFont(fontSequence);
			if(view.settingsSeq.showSkyline)
				{
				double w=fontSequence.pointSizeF();
				double h=fontSequence.pointSizeF()*1.5;
				p.draw(painter, w, h, new QPointF(view.mapCharToX(i), currentY+charHeight), letterUpper);
				p.draw(painter, w, h, new QPointF(view.mapCharToX(i), currentY+charHeight*2-2), letterLower);
				}
			else
				{
				double y1=currentY+fonth();
				double y2=currentY+fonth()*2;
				if(!NucleotideUtil.areComplementary(letterUpper,letterLower) && (!NucleotideUtil.isSpacing(letterLower) && !NucleotideUtil.isSpacing(letterUpper)))
					{
					painter.setFont(fontMismatch);
					painter.setPen(penMismatch);
					y1-=fonth()*0.2;
					y2+=fonth()*0.2;
					}

				painter.drawText(new QPointF(view.mapCharToX(i), y1), ""+letterUpper);
				painter.drawText(new QPointF(view.mapCharToX(i), y2), ""+letterLower);
				}
			}

		//Draw protein translation beneath
		painter.setPen(penOK);
		currentY+=fonth()*2;
		if(track.showProteinTranslation)
			{
			QFont font=new QFont(fontSequence);
			ProteinTranslator ptrans=new ProteinTranslator();
			for(int frame=0;frame<3;frame++)
				{
				font.setBold((frame+1)%3+1==track.currentReadingFrame);
				painter.setFont(font);
				for(int i=frame;i<charsPerLine;i+=3)
					{
					int cpos=cposLeft + i - 1;
					//int cpos=curline*charsPerLine + i - 1;
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
		if(track.showProteinTranslation)
			h+=fonth()*3+5;
		return new QRectF(0,currentY, 100000, h);
		}
	}
