package gui.paneCircular;

import seq.AnnotatedSequence;
import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.core.QRectF;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QFont;
import com.trolltech.qt.gui.QGraphicsEllipseItem;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QPen;
import com.trolltech.qt.gui.QStyleOptionGraphicsItem;
import com.trolltech.qt.gui.QWidget;

/**
 * 
 * The plasmid
 * 
 * @author Johan Henriksson
 *
 */
public class QGraphicsCircPlasmidSequence extends QGraphicsEllipseItem
	{
	public CircView view;
	
	
	public void paint(QPainter painter, QStyleOptionGraphicsItem option, QWidget widget) 
		{
		double plasmidRadius=view.plasmidRadius;

		
		
		
		QPen pen=new QPen();
		pen.setColor(new QColor(100,100,100));
		painter.setPen(pen);
		
		//The plasmid circle
		painter.drawEllipse(new QRectF(-plasmidRadius, -plasmidRadius, 2*plasmidRadius, 2*plasmidRadius));

		//The plasmid 0-position
		double angPlasmid0=view.circPan*2*Math.PI;
		double rPlasmid0=plasmidRadius-5;
		painter.drawLine(
				new QPointF(rPlasmid0*Math.cos(angPlasmid0), rPlasmid0*Math.sin(angPlasmid0)),
				new QPointF(plasmidRadius*Math.cos(angPlasmid0), plasmidRadius*Math.sin(angPlasmid0)));
		
		QFont font=new QFont();
		font.setPointSizeF(4.0/view.circZoom);
		font.setFamily("Arial");
		painter.setFont(font);

		
		AnnotatedSequence seq=view.seq;
		double textdelta=Math.PI*2.0/seq.getLength();
		double textRadius=plasmidRadius - font.pointSizeF()*1.2;
		
		double charw=painter.fontMetrics().width("A");
		double textoffset=(textdelta - charw/textRadius)/2;
		
		if(textdelta*textRadius>charw*1.5)
			{
			for(int i=0;i<seq.getLength();i++)
				{
				double curang=textdelta*i + view.circPan*2*Math.PI + textoffset;
				painter.save();
				painter.translate(textRadius*Math.cos(curang), textRadius*Math.sin(curang));
				painter.rotate(curang*360/(2.0*Math.PI)+90);
				painter.drawText(0, 0, ""+seq.getSequence().charAt(i));
				painter.restore();
				}
			}
		
		}


	
	@Override
	public QRectF boundingRect()
		{
		double r=view.plasmidRadius+10;  //this could be improved
		return new QRectF(-r,-r,2*r,2*r);
		}

	}
