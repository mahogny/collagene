package gui.paneLinear.tracks;

import gui.paneCircular.CircView;
import gui.paneLinear.ViewLinearSequence;
import gui.primer.MenuPrimer;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import primer.Primer;
import seq.AnnotatedSequence;
import seq.Orientation;

import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.core.QRectF;
import com.trolltech.qt.gui.QContextMenuEvent;
import com.trolltech.qt.gui.QFont;
import com.trolltech.qt.gui.QGraphicsPathItem;
import com.trolltech.qt.gui.QGraphicsScene;
import com.trolltech.qt.gui.QGraphicsTextItem;
import com.trolltech.qt.gui.QMouseEvent;
import com.trolltech.qt.gui.QPainterPath;

/**
 * 
 * Linear track: Primers
 * 
 * @author Johan Henriksson
 *
 */
public class LinTrackPrimer implements LinTrack
	{
	private HashMap<Primer, QRectF> primerPosition=new HashMap<Primer, QRectF>();

	ViewLinearSequence view;
	public LinTrackPrimer(ViewLinearSequence view)
		{
		this.view=view;
		}
	
	public void init()
		{
		primerPosition.clear();
		}
	
	public int place(QGraphicsScene scene, int currentY, int cposLeft, int cposRight)
		{
		QFont font=new QFont();
		font.setPointSize(10);

		double charWidth=view.charWidth;
		double charHeight=view.charHeight;
		AnnotatedSequence seq=view.getSequence();
		
		//////////////////////////////////////////////// Place primers
		int primerh=0;
		LinkedList<QRectF> prevprimerplaced=new LinkedList<QRectF>();
		double oneprimerh=charHeight;
		for(Primer p:seq.primers)
			{
			if(p.targetPosition>=cposLeft && p.targetPosition<=cposRight)
				{
				int basey=currentY;

				double x1;
				double x2=view.mapCharToX(p.targetPosition-cposLeft);
				double x3;
				int arrowsize=5;
				double texty=basey-3;
				if(p.orientation==Orientation.FORWARD)
					{
					x1=x2-charWidth*p.sequence.length();
					x3=x2-arrowsize;
					}
				else //REVERSE
					{
					x1=x2+charWidth*p.sequence.length();
					x3=x2+arrowsize;
					}
		
				
				//Initial placement of text
				QGraphicsTextItem ptext=new QGraphicsTextItem();
				ptext.setPlainText(p.name);
				ptext.setFont(font);
				if(p.orientation==Orientation.FORWARD)
					ptext.setPos(x2-ptext.boundingRect().width()-arrowsize,texty);
				else
					ptext.setPos(x2+arrowsize,texty);

				//Find suitable height for primer
				QRectF thisbb=CircView.textBR(ptext);
				double minx=Math.min(x1, x2);
				double maxx=Math.max(x1, x2);
				if(thisbb.left()>minx)
					thisbb.setLeft(minx);
				if(thisbb.right()<maxx)
					thisbb.setRight(maxx);
				int curprimerh=1;
				retryplace: for(;;)
					{
					for(QRectF oldr:prevprimerplaced)
						{
						if(oldr.intersects(thisbb))
							{
							thisbb.adjust(0, oneprimerh, 0, oneprimerh);
							texty+=oneprimerh;
							ptext.setY(texty);
							basey+=oneprimerh;
							curprimerh+=1;
							continue retryplace;
							}
						}
					prevprimerplaced.add(thisbb);
					break;
					}
				if(curprimerh>primerh)
					primerh=curprimerh;
				primerPosition.put(p, thisbb);
				
				//Add arrow
				QPainterPath pp=new QPainterPath();
				pp.moveTo(x1, basey);
				pp.lineTo(x2, basey);
				pp.lineTo(x3, basey+arrowsize);
				QGraphicsPathItem path=new QGraphicsPathItem();
				path.setPath(pp);			
				scene.addItem(ptext);
				scene.addItem(path);
				}
			}
		currentY+=primerh*oneprimerh+2;
		return currentY;
		}


	/**
	 * Get primer at location
	 */
	private Primer getPrimerAt(QPointF pos)
		{
		for(Primer p:primerPosition.keySet())
			if(primerPosition.get(p).contains(pos))
				return p;
		return null;
		}

	@Override
	public boolean mousePressEvent(QMouseEvent event, QPointF pos)
		{
		Primer curPrimer=getPrimerAt(pos);
		if(curPrimer!=null)
			{
			view.signalUpdated.emit(curPrimer.getRange());
			//view.updateSelectionGraphics();  
			return true;
			}
		else
			return false;
		}

	@Override
	public boolean contextMenuEvent(QContextMenuEvent event, QPointF pos)
		{
		Primer curPrimer=getPrimerAt(pos);
		if(curPrimer!=null)
			{
			MenuPrimer mPopup=new MenuPrimer(view.w, view.getSequence(), curPrimer, true);
			mPopup.exec(event.globalPos());
			}
		return false;
		}

	@Override
	public void updateSelectionGraphics(Collection<Object> selectionItems)
		{
		}

	@Override
	public void mouseMoveEvent(QMouseEvent event, QPointF pos)
		{
		}

	}
