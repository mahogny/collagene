package gui.paneCircular;

import seq.AnnotatedSequence;
import seq.Orientation;
import seq.SeqAnnotation;

import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.core.QRectF;
import com.trolltech.qt.core.Qt.BrushStyle;
import com.trolltech.qt.gui.QBrush;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QFont;
import com.trolltech.qt.gui.QGraphicsEllipseItem;
import com.trolltech.qt.gui.QGraphicsTextItem;
import com.trolltech.qt.gui.QMouseEvent;
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
	public int height;
	
	
	private double getZoom()
		{
		return view.getFeatureZoom();
		}
	
	private double getRadius()
		{
		return getRadius(height);
		}
	private double getRadius(int height)
		{
		return view.plasmidRadius-11*(height+1)/getZoom();
		}
	
	
	public void paint(QPainter painter, QStyleOptionGraphicsItem option, QWidget widget) 
		{
		double circZoom=getZoom();
		double r=getRadius();
		
		double ang1=(double)annot.getFrom()/seq.getLength()+view.circPan;
		double ang2=(double)annot.getTo()/seq.getLength()+view.circPan; //will fail over border?

		QColor bordercolor=QColor.fromRgb(0,0,0);
		QColor bgcolor=QColor.fromRgb(annot.color.r,annot.color.g,annot.color.b);
		
		QPen pen=new QPen();
		
		double radw=8.0/circZoom;
		double arrowsize=0.01;
		
		QPolygonF poly=new QPolygonF();
		
		
		double dAngle=1.0/100/view.circZoom; 
		
		int numdiv=(int)((ang2-ang1)/dAngle);
		if(numdiv==0)
			numdiv=1;
		double sf=(ang2-ang1)/(double)(numdiv);
		for(int i=0;i<=numdiv;i++)
			{
			double rad=r+radw;
			double ang=ang1 + i*sf;
			poly.add(rad*Math.cos(2*Math.PI*ang), rad*Math.sin(2*Math.PI*ang));
			}
		if(annot.orientation==Orientation.FORWARD)
			{
			double rad=r+radw/2;
			double ang=ang2+arrowsize/circZoom;
			poly.add(rad*Math.cos(2*Math.PI*ang), rad*Math.sin(2*Math.PI*ang));
			}
		for(int i=numdiv;i>=0;i--)
			{
			double rad=r;
			double ang=ang1 + i*sf;
			poly.add(rad*Math.cos(2*Math.PI*ang), rad*Math.sin(2*Math.PI*ang));
			}
		if(annot.orientation==Orientation.REVERSE)
			{
			double rad=r+radw/2;
			double ang=ang1-arrowsize/circZoom;
			poly.add(rad*Math.cos(2*Math.PI*ang), rad*Math.sin(2*Math.PI*ang));
			}
		QBrush brush=new QBrush();
		brush.setColor(bgcolor);
		brush.setStyle(BrushStyle.SolidPattern);
		pen.setColor(bordercolor);
		painter.setPen(pen);
		painter.setBrush(brush);
		painter.drawPolygon(poly);


		pen.setColor(QColor.fromRgb(0,0,0));
		painter.setPen(pen);
		
		QFont font=new QFont();
		font.setPointSizeF(4.0/circZoom);
		font.setFamily("Arial");
		painter.setFont(font);

		
		double totalTextWidth=painter.fontMetrics().width(annot.name);
		
		double textoffset=3.0/circZoom;
		
		double textRadius=r+2.0/circZoom;
		double curang=ang1+textoffset/(2*Math.PI*textRadius);
		if(totalTextWidth+textoffset>(ang2-ang1)*2*Math.PI*textRadius) //If the text does not fit then draw it afterwards
			curang=ang2+0.01/circZoom;
		
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

	
	/*
	public double getHeight()
		{
		
		}*/

	public boolean pointWithin(QPointF p)
		{
		double rad2=p.x()*p.x()+p.y()*p.y();
		
		double router2=getRadius(height-1);
		double rinner2=getRadius(height);
		router2*=router2;
		rinner2*=rinner2;
		
		if(rad2>=rinner2 && rad2<=router2)
			{
			double angle=Math.atan2(p.y(), p.x());
//				angle-=circPan*2*Math.PI;
			while(angle<0)
					angle+=Math.PI*2;
			angle/=2*Math.PI;

			/*
			System.out.println();
			System.out.println(annot.name);
			System.out.println("r "+rinner2+"  "+rad2+"   "+router2);*/
//			double angle=view.getAngle(p)/2.0/Math.PI;
			double ang1=getAng1();
			double ang2=getEstAng2wtext();

//			System.out.println("a "+ang1+"  "+angle+"   "+ang2);

			if(ang1<=ang2)
				return angle>=ang1 && angle<=ang2;
			else
				return angle<=ang2 || angle>=ang1;
			
			}
		return false;
		}
	
	Double textwest=null;
	public double getEstimatedTextWidth()
		{
		double circZoom=getZoom();
		if(textwest==null)
			{
//			textwest=4.0/view.circZoom*annot.name.length(); //estimator
			
			QFont font=new QFont();
			font.setPointSizeF(4.0/circZoom);
			font.setFamily("Arial");
			QGraphicsTextItem ti=new QGraphicsTextItem();
			ti.setFont(font);
			ti.setPlainText(annot.name);
			textwest=ti.boundingRect().width();		
			//System.out.println("---- "+bah+"\t"+textwest);
			
			}
		return textwest;
		}
	
	public double getEstAng2wtext()
		{
		double circZoom=getZoom();
		//TODO may want to cache for speed. also, recomputing stuff here. ugly
		//TODO handle boundary
		double ang1=getAng1();
		double ang2=(double)annot.getTo()/seq.getLength()+view.circPan;

		double textoffset=3.0/circZoom;
		double textRadius=getRadius()+2.0/circZoom;
		if(getEstimatedTextWidth()+textoffset>(ang2-ang1)*2*Math.PI*textRadius) //If the text does not fit then draw it afterwards
			ang2+=0.01/circZoom + getEstimatedTextWidth()/(textRadius*2*Math.PI);
		return ang2;
		}
	
	public double getAng1()
		{
		return (double)annot.getFrom()/seq.getLength()+view.circPan;
		}
	
	
	public boolean isOverlapping(QGraphicsCircSeqAnnotationItem o)
		{
		if(o.height==height)
			{
			double x=5.0/360.0/getZoom();
			double thisAng1=getAng1()-x;
			double thisAng2=getEstAng2wtext()+x;  //TODO handle ang1>ang2
			
			double oAng1=o.getAng1()-x;
			double oAng2=o.getEstAng2wtext()+x;
			
			return thisAng1 <= oAng2 && thisAng2>=oAng1;
			}
		else
			return false;
		}

	
	@Override
	public QRectF boundingRect()
		{
		int r=100;  //this could be improved
		return new QRectF(-r,-r,2*r,2*r);
		}

	}
