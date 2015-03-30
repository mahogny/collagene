package collagene.gui.paneCircular;

import collagene.seq.AnnotatedSequence;
import collagene.seq.Orientation;
import collagene.seq.SeqAnnotation;

import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.core.QRectF;
import com.trolltech.qt.core.Qt.BrushStyle;
import com.trolltech.qt.gui.QBrush;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QFont;
import com.trolltech.qt.gui.QGraphicsEllipseItem;
import com.trolltech.qt.gui.QGraphicsTextItem;
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
		return view.plasmidRadius-(view.plasmidRadius*0.11)*(height+1)/getZoom();
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
		
		double arrowWidth=view.plasmidRadius*0.08/circZoom;
		double arrowsizeAngle=0.01;
		
		QPolygonF poly=new QPolygonF();
		
		
		double dAngle=1.0/100/view.circZoom; 
		
		int numdiv=(int)((ang2-ang1)/dAngle);
		if(numdiv==0)
			numdiv=1;
		double sf=(ang2-ang1)/(double)(numdiv);
		for(int i=0;i<=numdiv;i++)
			{
			double rad=r+arrowWidth;
			double ang=ang1 + i*sf;
			poly.add(rad*Math.cos(2*Math.PI*ang), rad*Math.sin(2*Math.PI*ang));
			}
		if(annot.orientation==Orientation.FORWARD)
			{
			double rad=r+arrowWidth/2;
			double ang=ang2+arrowsizeAngle/circZoom;
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
			double rad=r+arrowWidth/2;
			double ang=ang1-arrowsizeAngle/circZoom;
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
		font.setPointSizeF(view.plasmidRadius*0.04/circZoom);
		font.setFamily("Arial");
		painter.setFont(font);

		
		double totalTextWidth=painter.fontMetrics().width(annot.name);
		
		double textoffset=view.plasmidRadius*0.03/circZoom;
		
		double textRadius=r+view.plasmidRadius*0.02/circZoom;
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
			while(angle<0)
					angle+=Math.PI*2;
			angle/=2*Math.PI;

			double ang1=getAng1();
			double ang2=getEstAng2wtext();

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
			font.setPointSizeF(view.plasmidRadius*0.04/circZoom);
			font.setFamily("Arial");
			QGraphicsTextItem ti=new QGraphicsTextItem();
			ti.setFont(font);
			ti.setPlainText(annot.name);
			textwest=ti.boundingRect().width();		
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

		double textoffset=view.plasmidRadius*0.03/circZoom;
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
			double angspacing=5.0/360.0/getZoom();
			double thisAng1=getAng1()-angspacing;
			double thisAng2=getEstAng2wtext()+angspacing;  //TODO handle ang1>ang2
			
			double oAng1=o.getAng1()-angspacing;
			double oAng2=o.getEstAng2wtext()+angspacing;
			
			return thisAng1 <= oAng2 && thisAng2>=oAng1;
			}
		else
			return false;
		}

	
	@Override
	public QRectF boundingRect()
		{
		double r=view.plasmidRadius;  //this could be improved
		return new QRectF(-r,-r,2*r,2*r);
		}

	}
