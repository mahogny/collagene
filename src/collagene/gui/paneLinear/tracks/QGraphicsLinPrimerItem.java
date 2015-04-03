package collagene.gui.paneLinear.tracks;

import collagene.gui.paneLinear.ViewLinearSequence;
import collagene.gui.paneLinear.tracks.LinTrackPrimer.EvalPrimer;
import collagene.seq.AnnotatedSequence;
import collagene.seq.Orientation;

import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.core.QRectF;
import com.trolltech.qt.gui.QBrush;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QFont;
import com.trolltech.qt.gui.QFontMetricsF;
import com.trolltech.qt.gui.QGraphicsRectItem;
import com.trolltech.qt.gui.QLineF;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QPainterPath;
import com.trolltech.qt.gui.QPen;
import com.trolltech.qt.gui.QPolygonF;
import com.trolltech.qt.gui.QStyleOptionGraphicsItem;
import com.trolltech.qt.gui.QWidget;

/**
 * 
 * One trace
 * 
 * @author Johan Henriksson
 *
 */
public class QGraphicsLinPrimerItem extends QGraphicsRectItem
	{
	int arrowh=10;
	int corresponddy=3;

	public AnnotatedSequence seq;

	public int cposLeft;
	public int currentY;
	public ViewLinearSequence view;
	
	EvalPrimer ep;
	double charHeight;
	
	public double fonth()
		{
		return charHeight-1;
		}

	
	private int getRenderFrom()
		{
		if(ep.getLeftAnchor()>=cposLeft)
			return 0;
		else
			return cposLeft-ep.getLeftAnchor();
		}

	private int getRenderTo()
		{
		int cposRight=cposLeft+view.charsPerLine;
		if(ep.getRightAnchor()<=cposRight)
			return ep.p.getLength();
		else
			{
			if(ep.getLeftAnchor()>=cposLeft)
				return cposRight-ep.getLeftAnchor();  
			else
				return getRenderFrom()+view.charsPerLine;
			}
		}
	
	
	public void paint(QPainter painter, QStyleOptionGraphicsItem option, QWidget widget) 
		{
		int baseY=currentY;
		if(ep.p.orientation==Orientation.FORWARD)
			baseY+=arrowh;
		
		QFont fontSequence=new QFont();
		fontSequence.setFamily("Courier");
		fontSequence.setPointSizeF(view.charWidth);

		QPen penText=new QPen(QColor.black);
		QPen penNone=new QPen(QColor.transparent);
		
		QPen penBaseline=new QPen();
		penBaseline.setColor(QColor.lightGray);

		//Figure out range to render (in reversed primer coordinates)
		int renderfrom=getRenderFrom();
		int renderto=getRenderTo();
		
		//Draw polygon surrounding
		QBrush br=new QBrush(QColor.lightGray);
		painter.setBrush(br);
		painter.setPen(penNone);
		double maxy=0;
		boolean firstBase=true;
		for(int i=renderfrom;i<renderto;i++)
			{
			double y1, y2;
			y1=baseY;
			y2=baseY+fonth()+4;
			if(!ep.corresponds(i))
				{
				y1+=corresponddy;
				y2+=corresponddy;
				}
			maxy=Math.max(maxy,y2);
			
			int cpos=ep.getLeftAnchor() + i - cposLeft;
			double x=view.mapCharToX(cpos);
			double x2=view.mapCharToX(cpos+1);
			if(firstBase)
				x-=2;
			firstBase=false;
			
			painter.setPen(penNone);
			painter.drawRect(new QRectF(x,y1,x2-x,y2-y1));
			
			painter.setPen(penText);
			if(i==0)
				painter.drawLine(new QLineF(x,y1,x,y2));
			if(i==ep.p.getLength()-1)
				painter.drawLine(new QLineF(x2,y1,x2,y2));
			painter.drawLine(new QLineF(x,y1,x2,y1));
			painter.drawLine(new QLineF(x,y2,x2,y2));
			
			int arrowshift=5;
			
			if(i==ep.p.getLength()-1 && ep.p.orientation==Orientation.FORWARD) 
				{
				double xtop=x-arrowshift;
				double xtop2=x2-arrowshift;
				double y3=y1-arrowh;
				QPolygonF poly=new QPolygonF();
				poly.add(x,y1+1);
				poly.add(x2,y1+1);
				poly.add(xtop2,y3);
				poly.add(xtop,y3);
				painter.setPen(penNone);
				painter.drawConvexPolygon(poly);
				
				QPainterPath path=new QPainterPath();
				path.moveTo(x2,y1+1);
				path.lineTo(xtop2,y3);
				path.lineTo(xtop,y3);
				path.lineTo(x,y1+1);
				painter.setPen(penText);
				painter.drawPath(path);
				}
			if(i==0 && ep.p.orientation==Orientation.REVERSE)
				{
				double xtop=x+arrowshift;
				double xtop2=x2+arrowshift;
				double y3=y2+arrowh;
				maxy=Math.max(maxy,y3);
				QPolygonF poly=new QPolygonF();
				poly.add(x,y2);
				poly.add(xtop,y3);
				poly.add(xtop2,y3);
				poly.add(x2,y2);
				painter.setPen(penNone);
				painter.drawConvexPolygon(poly);
				
				QPainterPath path=new QPainterPath();
				path.moveTo(x,y2);
				path.lineTo(xtop,y3);
				path.lineTo(xtop2,y3);
				path.lineTo(x2,y2);
				painter.setPen(penText);
				painter.drawPath(path);
				}
			}
		
		//Draw sequence
		if(fontSequence.pointSizeF()>5)
			{
			painter.setFont(fontSequence);
			painter.setPen(penText);
			for(int i=renderfrom;i<renderto;i++)
				{
				int cpos=ep.getLeftAnchor() + i - cposLeft;
				double y1=baseY+fonth();
				if(!ep.corresponds(i))
					y1+=corresponddy;
				double x=view.mapCharToX(cpos);
				painter.setPen(penText);
				painter.drawText(new QPointF(x, y1), ""+ep.rotsequence.charAt(i));
				}
			}
		
		//Draw name of primer
		QFont fontName=new QFont();
		fontName.setItalic(true);
		painter.setFont(fontName);
		double xmid=view.mapCharToX(ep.getLeftAnchor() + (renderfrom+renderto)/2 - cposLeft);
		QFontMetricsF fm=new QFontMetricsF(fontName);
		painter.drawText(new QPointF(xmid-fm.boundingRect(ep.p.name).width()/2,maxy+fm.height()), ep.p.name);
		}



	@Override
	public QRectF boundingRect()
		{
		int renderfrom=getRenderFrom();
		int renderto=getRenderTo();

		int cposFrom=ep.getLeftAnchor() + renderfrom - cposLeft;
		int cposTo=ep.getLeftAnchor() + renderto - cposLeft;

		double x=view.mapCharToX(cposFrom);
		double x2=view.mapCharToX(cposTo);
		
		int delta=5;

		int primerh=55;


		return new QRectF(x-delta,currentY, x2-x+delta*2, primerh); ///////////// note, primer height not computed!
		}
	}
