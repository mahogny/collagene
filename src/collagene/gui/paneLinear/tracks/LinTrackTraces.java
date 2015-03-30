package collagene.gui.paneLinear.tracks;

import java.util.Collection;
import java.util.HashMap;

import collagene.gui.paneLinear.ViewLinearSequence;
import collagene.seq.AnnotatedSequence;

import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.core.QRectF;
import com.trolltech.qt.gui.QContextMenuEvent;
import com.trolltech.qt.gui.QGraphicsScene;
import com.trolltech.qt.gui.QMouseEvent;

/**
 * 
 * Linear track: Traces
 * 
 * @author Johan Henriksson
 *
 */
public class LinTrackTraces implements LinTrack
	{
	private HashMap<QRectF,PlacedTrace> mapAnnotations=new HashMap<QRectF, PlacedTrace>();

	

	ViewLinearSequence view;
	public LinTrackTraces(ViewLinearSequence view)
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
		/*
		try
			{
			if(seq.traces.isEmpty())
				{
				PlacedTrace t=new PlacedTrace();
				t.from=3;
				t.trace=ScfFile.readFile(new File("/home/mahogny/Dropbox/ebi/_my protocols/retrovirus/sangerseq/pbabe/T205_data_pbabe.w2kseq1.scf"));
				seq.traces.add(t);
				}
			}
		catch (IOException e)
			{
			e.printStackTrace();
			}
		*/
		//LinkedList<QRectF> prevPlaced=new LinkedList<QRectF>();
		
		int lasty=currentY;
		for(PlacedTrace annot:seq.traces)
			{
			//Check if this feature is in range
			if(annot.getTo()>cposLeft && annot.getFrom()<cposRight)
				{
				QGraphicsLinTraceItem item=new QGraphicsLinTraceItem();
				item.charHeight=17; //TODo
				item.cposLeft=cposLeft;
				item.currentY=currentY;
				item.seq=seq;
				item.trace=annot;
				item.view=view;
				item.track=this;
				
				
				lasty=Math.max(lasty, (int)item.boundingRect().bottom());
				
				/*

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
				*/
				scene.addItem(item);
				}
			}
//		currentY+=(maxannoth)*oneannoth;
		return lasty;//currentY+250; //TODO
		}


	/**
	 * Find if cursor overlaps a future
	 */
	private PlacedTrace getAnnotationAt(QPointF pos)
		{
		for(QRectF region:mapAnnotations.keySet())
			if(region.contains(pos))
				return mapAnnotations.get(region);
		return null;
		}


	@Override
	public boolean mousePressEvent(QMouseEvent event, QPointF pos)
		{
		PlacedTrace curAnnotation=getAnnotationAt(pos);
		if(curAnnotation!=null)
			{
			//view.signalUpdated.emit(new EventSelectedAnnotation(curAnnotation));
			return true;
			}
		else
			return false;
		}

	@Override
	public boolean contextMenuEvent(QContextMenuEvent event, QPointF pos)
		{
		PlacedTrace curAnnotation=getAnnotationAt(pos);
		if(curAnnotation!=null)
			{
			//MenuAnnotation mPopup=new MenuAnnotation(view.w, view.getSequence(), curAnnotation);
			//mPopup.exec(event.globalPos());
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
