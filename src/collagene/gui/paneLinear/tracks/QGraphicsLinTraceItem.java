package collagene.gui.paneLinear.tracks;

import collagene.gui.paneLinear.ViewLinearSequence;
import collagene.io.trace.SequenceTrace;
import collagene.io.trace.SequenceTraceBaseCall;
import collagene.seq.AnnotatedSequence;

import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.core.QRectF;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QFont;
import com.trolltech.qt.gui.QGraphicsRectItem;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QPainterPath;
import com.trolltech.qt.gui.QPen;
import com.trolltech.qt.gui.QStyleOptionGraphicsItem;
import com.trolltech.qt.gui.QWidget;

/**
 * 
 * One trace
 * 
 * @author Johan Henriksson
 *
 */
public class QGraphicsLinTraceItem extends QGraphicsRectItem
	{
	public AnnotatedSequence seq;

	public int cposLeft;
	public int currentY;
	public ViewLinearSequence view;
	
	LinTrackTraces track;
	double charHeight;
	QFont fontSequence;
	PlacedTrace trace;
	
	public double fonth()
		{
		return charHeight-1;
		}

	
	public double mapPeakY(double level)
		{
		double v=Math.min(dispHeight,level*scaleY);
		double y=baseY-v;
		return y;
		}
	private double mapPeakX(double i)
		{
		int offsetletter=trace.getFrom()-cposLeft;
		return view.mapCharToX(i*scaleX+offsetletter);
		}
	
	//Set later
	private int dispHeight;
	private int baseY;
	private double scaleX;
	private double scaleY;
	

	public void paint(QPainter painter, QStyleOptionGraphicsItem option, QWidget widget) 
		{
		int charsPerLine=view.charsPerLine;

		SequenceTrace st=trace.trace;
		
		QFont fontMismatch=new QFont(fontSequence);
		fontMismatch.setBold(true);
		QPen penText=new QPen();
//		QPen penMismatch=new QPen();
	//	penMismatch.setColor(QColor.fromRgb(255, 0, 0));
		
		QPen penBaseline=new QPen();
		penBaseline.setColor(QColor.lightGray);
		
		int seqletterFrom=Math.max(0,trace.getFrom()-cposLeft);
		int seqletterTo=Math.min(view.charsPerLine,trace.getTo()-cposLeft);
		
		int traceletterFrom=Math.max(0,cposLeft-trace.from);
		int traceletterTo=Math.min(st.getNumBases(), cposLeft+view.charsPerLine-trace.getFrom());

		int lastPeakIndex=st.basecalls.get(st.basecalls.size()-1).peakIndex;
		
		dispHeight=150;
		baseY=currentY+20+dispHeight;
		scaleX=st.getNumBases()/(double)lastPeakIndex;
		scaleY=0.03;

		//Draw aligned text on top
		painter.setFont(fontSequence);
		for(int i=0;i<charsPerLine;i++)
			{
			int index=cposLeft+i-trace.from;
			if(index>=0 && index<trace.trace.getNumBases())
				{
				int cpos=cposLeft + i;
				if(cpos>=seq.getLength())
					break;
				
				SequenceTraceBaseCall cb=st.basecalls.get(index);

				//Draw base
				double y1=currentY+fonth();
				double x=view.mapCharToX(i);
				painter.setPen(penText);
				painter.drawText(new QPointF(x, y1), ""+cb.base);
				
				//Draw line down to peak
				double x1=x+view.charWidth/3;
				double x2=mapPeakX(cb.peakIndex);
				double liney=mapPeakY(st.getMaxLevel(cb.peakIndex)-1);
				painter.setPen(penBaseline);
				painter.drawLine(
						new QPointF(x1,y1), 
						new QPointF(x2,liney));
				}
			}
		
		//Colors for each base
		QColor[] colorACGT=new QColor[]{
				QColor.green,
				QColor.blue,
				QColor.black,
				QColor.red
		};
			
		//Draw the trace lines
		painter.setPen(QColor.black);
		painter.drawLine(
				new QPointF(view.mapCharToX(seqletterFrom),baseY),
				new QPointF(view.mapCharToX(seqletterTo),baseY));
		for(int curcol=0;curcol<4;curcol++)
			{
			char[] letters=new char[]{'A','C','G','T'};
			int level[]=st.getLevel(letters[curcol]);
			painter.setPen(colorACGT[curcol]);
			QPainterPath path=new QPainterPath();
			boolean first=true;
			int levelfrom=(int)(traceletterFrom/scaleX);
			int levelto=Math.min((int)((traceletterTo+1)/scaleX),st.getLevelLength());
			for(int i=levelfrom;i<levelto;i++)
				{
				double y=mapPeakY(level[i]);
				double x=mapPeakX(i);
				if(first)
					path.moveTo(x,y);
				else
					path.lineTo(x,y);
				first=false;
				}
			painter.drawPath(path);
			
			double y=baseY+fonth();
			for(SequenceTraceBaseCall cb:st.basecalls)
				{
				if(cb.peakIndex>levelfrom)
					{
					if(cb.peakIndex>levelto)
						break;
					if(cb.base==letters[curcol])
						{
						double x=mapPeakX(cb.peakIndex)-view.charWidth/3;
						painter.drawText(new QPointF(x,y), ""+cb.base);
						}
					}
				}
			}
		}



	@Override
	public QRectF boundingRect()
		{
		double h=250;//fonth()*2;
		return new QRectF(0,currentY, 100000, h);
		}
	}
