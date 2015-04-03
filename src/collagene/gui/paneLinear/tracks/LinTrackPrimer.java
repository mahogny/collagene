package collagene.gui.paneLinear.tracks;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import collagene.gui.paneLinear.ViewLinearSequence;
import collagene.gui.primer.MenuPrimer;
import collagene.primer.Primer;
import collagene.seq.AnnotatedSequence;
import collagene.seq.Orientation;
import collagene.seq.SequenceRange;
import collagene.sequtil.NucleotideUtil;

import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.core.QRectF;
import com.trolltech.qt.gui.QContextMenuEvent;
import com.trolltech.qt.gui.QGraphicsScene;
import com.trolltech.qt.gui.QMouseEvent;

/**
 * 
 * Linear track: Primers
 * 
 * @author Johan Henriksson
 *
 */
public class LinTrackPrimer implements LinTrack
	{
	private HashMap<QRectF, Primer> primerPosition=new HashMap<QRectF, Primer>();
	private LinkedList<EvalPrimer> listeval=new LinkedList<LinTrackPrimer.EvalPrimer>();

	ViewLinearSequence view;
	public LinTrackPrimer(ViewLinearSequence view)
		{
		this.view=view;
		}
	
	public void initPlacing()
		{
		AnnotatedSequence seq=view.getSequence();
		primerPosition.clear();
		/*
		if(seq.primers.isEmpty())
			{
			Primer p=new Primer();
			p.sequence="ataTAAATTA";
			p.name="apa";
			p.targetPosition=40;
			p.orientation=Orientation.FORWARD;
			seq.primers.add(p);
			}*/

		//Pre-evaluate all primers
		listeval.clear();
		for(Primer p:seq.primers)
			listeval.add(new EvalPrimer(p, seq));
		}
	
	
	/**
	 * 
	 * Evaluated primer
	 * 
	 */
	public static class EvalPrimer
		{
		String corrsequence;
		String rotsequence;
		Primer p;
		SequenceRange r;
		public EvalPrimer(Primer p, AnnotatedSequence seq)
			{
			this.p=p;
			
			r=p.getRange();
			corrsequence=seq.getSequence(r);
			rotsequence=p.sequence;
			
			if(p.orientation==Orientation.REVERSE)
				{
				corrsequence=NucleotideUtil.complement(corrsequence);
				rotsequence=NucleotideUtil.reverse(rotsequence);
				}
			}
		
		public int getLeftAnchor()
			{
			return r.from;
			}
		
		public boolean corresponds(int i)
			{
			return corrsequence.charAt(i)==rotsequence.charAt(i);
			}

		public boolean overlaps(AnnotatedSequence seq, int cposLeft, int cposRight)
			{
			return r.intersects(seq, cposLeft, cposRight);
			}

		public int getRightAnchor()
			{
			return r.to;
			}
		
		}
	
	

	

	/**
	 * Place primers
	 */
	public int place(QGraphicsScene scene, int currentY, int cposLeft, int cposRight)
		{
		AnnotatedSequence seq=view.getSequence();
		LinkedList<QRectF> prevprimerplaced=new LinkedList<QRectF>();
		double maxy=currentY;
		for(EvalPrimer ep:listeval)
			{
			if(ep.overlaps(seq, cposLeft, cposRight))
				{
				QGraphicsLinPrimerItem it=new QGraphicsLinPrimerItem();
				it.cposLeft=cposLeft;
				it.charHeight=Math.max(5,view.charWidth*1.7); 
				it.currentY=currentY;
				it.view=view;
				it.ep=ep;
				scene.addItem(it);

				//Find suitable height for primer
				QRectF thisbb=it.boundingRect();
				retryplace: for(;;)
					{
					for(QRectF oldr:prevprimerplaced)
						{
						if(oldr.intersects(thisbb))
							{
							it.currentY+=thisbb.height();
							thisbb.adjust(0, thisbb.height(), 0, thisbb.height());
							continue retryplace;
							}
						}
					prevprimerplaced.add(thisbb);
					break;
					}
				if(thisbb.bottom()>maxy)
					maxy=thisbb.bottom();
				primerPosition.put(thisbb, ep.p);
				}
			}
		return (int)maxy;
		}


	/**
	 * Get primer at location
	 */
	private Primer getPrimerAt(QPointF pos)
		{
		for(QRectF bb:primerPosition.keySet())
			if(bb.contains(pos))
				return primerPosition.get(bb);
		return null;
		}

	@Override
	public boolean mousePressEvent(QMouseEvent event, QPointF pos)
		{
		Primer curPrimer=getPrimerAt(pos);
		if(curPrimer!=null)
			{
			view.emitNewSelection(curPrimer.getRange());
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

	@Override
	public void handleEvent(Object ob)
		{
		}

	}
