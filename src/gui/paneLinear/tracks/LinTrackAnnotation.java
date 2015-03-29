package gui.paneLinear.tracks;

import gui.paneCircular.CircView;
import gui.paneLinear.ViewLinearSequence;
import gui.sequenceWindow.EventSelectedAnnotation;
import gui.sequenceWindow.MenuAnnotation;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import seq.AnnotatedSequence;
import seq.Orientation;
import seq.SeqAnnotation;

import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.core.QRectF;
import com.trolltech.qt.core.Qt.BrushStyle;
import com.trolltech.qt.gui.QBrush;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QContextMenuEvent;
import com.trolltech.qt.gui.QGraphicsPolygonItem;
import com.trolltech.qt.gui.QGraphicsScene;
import com.trolltech.qt.gui.QGraphicsTextItem;
import com.trolltech.qt.gui.QMouseEvent;
import com.trolltech.qt.gui.QPen;
import com.trolltech.qt.gui.QPolygonF;

/**
 * 
 * Linear track: Primers
 * 
 * @author Johan Henriksson
 *
 */
public class LinTrackAnnotation implements LinTrack
	{
	private HashMap<QRectF,SeqAnnotation> mapAnnotations=new HashMap<QRectF, SeqAnnotation>();

	

	ViewLinearSequence view;
	public LinTrackAnnotation(ViewLinearSequence view)
		{
		this.view=view;
		}
	
	public void initPlacing()
		{
		mapAnnotations.clear();
		}

	
	public int place(QGraphicsScene scene, int currentY, int cposLeft, int cposRight)
		{
		AnnotatedSequence seq=view.getSequence();
		
		LinkedList<QRectF> prevPlaced=new LinkedList<QRectF>();
		
		int maxannoth=0;
		int oneannoth=20;
		for(SeqAnnotation annot:seq.annotations)
			{
			//Check if this feature is in range
			if(annot.getTo()>cposLeft && annot.getFrom()<cposRight)
				{
				//Annotation should go beneath sequence, above position line
//				int currentAnnotationHeight=0;
//				if(currentAnnotationHeight==-1)
//					currentAnnotationHeight=0;  //write a better allocator!
				int thisannoth=0;
				
				
				int basey=currentY+thisannoth*oneannoth;
				int polyyup=basey;
				int polyydown=basey+18;
				int polyymid=basey+9;
				
				QPolygonF poly=new QPolygonF();
				
				double frompos;
				if(annot.getFrom()>=cposLeft)
					{
					//Annotation starts here
					frompos=view.mapCharToX(annot.getFrom()-cposLeft);
					poly.add(frompos, polyydown);
					if(annot.orientation==Orientation.REVERSE)
						poly.add(view.mapCharToX(annot.getFrom()-1-cposLeft)+5, polyymid);
					poly.add(frompos, polyyup); 
					}
				else
					{
					//Annotation continued from before
					frompos=view.mapCharToX(0);
					poly.add(frompos, polyydown); 
					poly.add(frompos-3, polyyup+7); 
					poly.add(frompos+3, polyyup+3); 
					poly.add(frompos, polyyup); 
					}
				
				double topos;
				if(annot.getTo()<=cposRight)
					{
					//Annotation ends here
					topos=view.mapCharToX(annot.getTo()+1-cposLeft);
					poly.add(topos, polyyup);  
					if(annot.orientation==Orientation.FORWARD)
						poly.add(view.mapCharToX(annot.getTo()+1-cposLeft)+5, polyymid);
					poly.add(view.mapCharToX(annot.getTo()+1-cposLeft), polyydown); 
					}
				else
					{
					//Continues on next line
					topos=view.mapCharToX(view.charsPerLine+1);
					poly.add(topos, polyyup); 
					poly.add(topos+3, polyyup+3); 
					poly.add(topos-3, polyyup+7); 
					poly.add(topos, polyydown); 
					}
				
				QPen pen=new QPen();
				pen.setColor(QColor.fromRgb(0,0,0));
				
				QBrush brush=new QBrush();
				brush.setStyle(BrushStyle.SolidPattern);
				brush.setColor(QColor.fromRgb(annot.color.r, annot.color.g, annot.color.b));
				
				QGraphicsPolygonItem pi=new QGraphicsPolygonItem();
				pi.setPolygon(poly);
				pi.setPen(pen);
				pi.setBrush(brush);
				
				QGraphicsTextItem ti=new QGraphicsTextItem();
				ti.setPlainText(annot.name);
				ti.setPos(frompos+2, polyyup-2);

				QRectF newrect=poly.boundingRect();
				newrect.setRight(Math.max(newrect.right(),CircView.textBR(ti).right()));
				
				retry: for(;;)
					{
					for(QRectF oldrect:prevPlaced)
						{
						if(oldrect.intersects(newrect))
							{
							thisannoth++;
							pi.moveBy(0, oneannoth);
							ti.moveBy(0, oneannoth);
							newrect.adjust(0, oneannoth, 0, oneannoth);
							continue retry;
							}
						}
					break;
					}
				maxannoth=Math.max(maxannoth,thisannoth+1);
				mapAnnotations.put(newrect, annot);
				prevPlaced.add(newrect);
				
				scene.addItem(pi);
				scene.addItem(ti);
				}
			}
		currentY+=(maxannoth)*oneannoth;
		return currentY;
		}


	/**
	 * Find if cursor overlaps a future
	 */
	private SeqAnnotation getAnnotationAt(QPointF pos)
		{
		for(QRectF region:mapAnnotations.keySet())
			if(region.contains(pos))
				return mapAnnotations.get(region);
		return null;
		}


	@Override
	public boolean mousePressEvent(QMouseEvent event, QPointF pos)
		{
		SeqAnnotation curAnnotation=getAnnotationAt(pos);
		if(curAnnotation!=null)
			{
			view.signalUpdated.emit(new EventSelectedAnnotation(curAnnotation));
			return true;
			}
		else
			return false;
		}

	@Override
	public boolean contextMenuEvent(QContextMenuEvent event, QPointF pos)
		{
		SeqAnnotation curAnnotation=getAnnotationAt(pos);
		if(curAnnotation!=null)
			{
			MenuAnnotation mPopup=new MenuAnnotation(view.w, view.getSequence(), curAnnotation);
			mPopup.exec(event.globalPos());
			return true;
			}
		else
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

	@Override
	public void handleEvent(Object ob)
		{
		}

	}
