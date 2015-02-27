package gui.paneCircular;

import seq.AnnotatedSequence;
import seq.Orientation;
import seq.SeqAnnotation;

import com.trolltech.qt.core.QRectF;
import com.trolltech.qt.core.Qt.BrushStyle;
import com.trolltech.qt.gui.QBrush;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QFont;
import com.trolltech.qt.gui.QGraphicsEllipseItem;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QPen;
import com.trolltech.qt.gui.QPolygonF;
import com.trolltech.qt.gui.QStyleOptionGraphicsItem;
import com.trolltech.qt.gui.QWidget;

/**
 * 
 * Sequence annotation graphics item, circular view
 * 
 * @author Johan Henriksson
 *
 */
public class QGraphicsCircSeqAnnotationItem extends QGraphicsEllipseItem
	{
	public SeqAnnotation annot;
	public AnnotatedSequence seq;
	public CircView view;
	
//	private int r;
	public int height;
	
	
	private int getRadius()
		{
		return 70-15*height;
		}
	
	public void paint(QPainter painter, QStyleOptionGraphicsItem option, QWidget widget) 
		{
		int r=getRadius();
		
		double ang1=(double)annot.from/seq.getLength()+view.circPan;
		double ang2=(double)annot.to/seq.getLength()+view.circPan;

		QColor bordercolor=QColor.fromRgb(0,0,0);
		QColor bgcolor=QColor.fromRgb((int)(255*annot.colorR),(int)(255*annot.colorG),(int)(255*annot.colorB));
		
		QPen pen=new QPen();
		
		double radw=8;
		
		QPolygonF poly=new QPolygonF();
		for(int i=0;i<=100;i++)
			{
			double rad=r+radw;
			double ang=ang1 + (ang2-ang1)*i/100.0;
			poly.add(rad*Math.cos(2*Math.PI*ang), rad*Math.sin(2*Math.PI*ang));
			}
		if(annot.orientation==Orientation.FORWARD)
			{
			double rad=r+radw/2;
			double ang=ang2+0.01;
			poly.add(rad*Math.cos(2*Math.PI*ang), rad*Math.sin(2*Math.PI*ang));
			}
		for(int i=100;i>=0;i--)
			{
			double rad=r;
			double ang=ang1 + (ang2-ang1)*i/100.0;
			poly.add(rad*Math.cos(2*Math.PI*ang), rad*Math.sin(2*Math.PI*ang));
			}
		if(annot.orientation==Orientation.REVERSE)
			{
			double rad=r+radw/2;
			double ang=ang1-0.01;
			poly.add(rad*Math.cos(2*Math.PI*ang), rad*Math.sin(2*Math.PI*ang));
			}
		QBrush brush=new QBrush();
		brush.setColor(bgcolor);
		brush.setStyle(BrushStyle.SolidPattern);
		pen.setColor(bordercolor);
		painter.setPen(pen);
		painter.setBrush(brush);
		painter.drawPolygon(poly);


		
//		pen.setColor(QColor.fromRgb(255,255,255));
		pen.setColor(QColor.fromRgb(0,0,0));
		painter.setPen(pen);
		
		QFont font=new QFont();
		font.setPointSize(4);
		font.setFamily("Arial");
		painter.setFont(font);

		double totalTextWidth=painter.fontMetrics().width(annot.name);
		
		int textoffset=3;
		
		double textRadius=r+2;
		double curang=ang1+textoffset/(2*Math.PI*textRadius);
		if(totalTextWidth+textoffset>(ang2-ang1)*2*Math.PI*textRadius) //If the text does not fit then draw it afterwards
			curang=ang2+0.01;
		
		for(int i=0;i<annot.name.length();i++)
			{
			painter.save();
			painter.translate(textRadius*Math.cos(2*Math.PI*curang), textRadius*Math.sin(2*Math.PI*curang));
			painter.rotate(curang*360+90);
			painter.drawText(0, 0, ""+annot.name.charAt(i));
			painter.restore();
			
			double charWidth=painter.fontMetrics().width(annot.name.charAt(i));
			curang+=charWidth/(textRadius*2*Math.PI);
			}
		}

	public boolean isOverlapping(QGraphicsCircSeqAnnotationItem o)
		{
		return o.height==height &&
				annot.from-50<=o.annot.to+50 && annot.to+50>=o.annot.from-50;
				//TODO do proper
		}

	
	@Override
	public QRectF boundingRect()
		{
		int r=100;
		return new QRectF(-r,-r,2*r,2*r);
		}

	}
