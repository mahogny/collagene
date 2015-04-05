package collagene.gui.paneCircular;

import collagene.primer.Primer;
import collagene.seq.AnnotatedSequence;
import collagene.seq.Orientation;
import collagene.seq.SequenceRange;

import com.trolltech.qt.core.QRectF;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QGraphicsEllipseItem;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QPainterPath;
import com.trolltech.qt.gui.QPen;
import com.trolltech.qt.gui.QStyleOptionGraphicsItem;
import com.trolltech.qt.gui.QWidget;

/**
 * 
 * One primer
 * 
 * @author Johan Henriksson
 *
 */
public class QGraphicsCircPrimer extends QGraphicsEllipseItem
	{
	public CircView view;
	public Primer p;
	
	
	public void paint(QPainter painter, QStyleOptionGraphicsItem option, QWidget widget) 
		{
		double plasmidRadius=view.plasmidRadius;

		AnnotatedSequence seq=view.getSequence();
		double circPan=view.circPan;
		
		QPen penPrimer=new QPen(QColor.blue);
		painter.setPen(penPrimer);
		double fzoom=view.getFeatureZoom();

		
		double pdist=plasmidRadius*0.015/fzoom;
		double primerRadius=plasmidRadius;
		if(p.orientation==Orientation.FORWARD)
			primerRadius+=pdist;
		else
			primerRadius-=pdist;
		
		SequenceRange r=p.getRange().toUnwrappedRange(seq);
		
		double rfrom=r.from/(double)seq.getLength();
		double rto=  r.to  /(double)seq.getLength();
		
		int numstep=10;
		QPainterPath path=new QPainterPath();
		
		//Reverse arrow
		if(p.orientation==Orientation.REVERSE)
			{
			double primerRadius2=primerRadius-pdist;
			double ang=rfrom+circPan+pdist/(primerRadius2*2*Math.PI);
			double x=Math.cos(Math.PI*2*ang)*primerRadius2;
			double y=Math.sin(Math.PI*2*ang)*primerRadius2;
			path.moveTo(x, y);
			}
		else
			{
			double ang=rfrom+circPan;
			double x=Math.cos(Math.PI*2*ang)*primerRadius;
			double y=Math.sin(Math.PI*2*ang)*primerRadius;
			path.moveTo(x, y);
			}
		
		//Midsegments
		for(int i=0;i<=numstep;i++)
			{
			double ang=(rto-rfrom)*i/(double)numstep+circPan+rfrom;
			double x=Math.cos(Math.PI*2*ang)*primerRadius;
			double y=Math.sin(Math.PI*2*ang)*primerRadius;
			path.lineTo(x, y);
			}
		
		//Forward arrow
		if(p.orientation==Orientation.FORWARD)
			{
			double primerRadius2=primerRadius+pdist;
			double ang=rto+circPan-pdist/(primerRadius2*2*Math.PI);
			double x=Math.cos(Math.PI*2*ang)*primerRadius2;
			double y=Math.sin(Math.PI*2*ang)*primerRadius2;
			path.lineTo(x, y);
			}

		painter.drawPath(path);
		}


	
	@Override
	public QRectF boundingRect()
		{
		double r=view.plasmidRadius*1.1;  //this could be improved
		return new QRectF(-r,-r,2*r,2*r);
		}

	}
