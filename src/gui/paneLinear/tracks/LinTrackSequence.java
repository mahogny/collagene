package gui.paneLinear.tracks;

import gui.paneCircular.CircView;
import gui.paneLinear.ViewLinearSequence;
import gui.paneRestriction.EventSelectedRestrictionEnzyme;
import gui.qt.QTutil;
import gui.sequenceWindow.EventSelectedAnnotation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

import restrictionEnzyme.RestrictionEnzyme;
import seq.AnnotatedSequence;
import seq.RestrictionSite;
import seq.SeqAnnotation;
import seq.SequenceRange;

import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.core.QRectF;
import com.trolltech.qt.gui.QBrush;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QContextMenuEvent;
import com.trolltech.qt.gui.QFont;
import com.trolltech.qt.gui.QGraphicsLineItem;
import com.trolltech.qt.gui.QGraphicsRectItem;
import com.trolltech.qt.gui.QGraphicsScene;
import com.trolltech.qt.gui.QGraphicsTextItem;
import com.trolltech.qt.gui.QMouseEvent;
import com.trolltech.qt.gui.QPen;

/**
 * 
 * Linear track: Sequence and restriction sites
 * 
 * @author Johan Henriksson
 *
 */
public class LinTrackSequence implements LinTrack
	{
	public ArrayList<Integer> sequenceLineY=new ArrayList<Integer>();

	private RestrictionSite hoveringRestrictionSite=null;
	private HashMap<RestrictionSite, QRectF> resSitePosition=new HashMap<RestrictionSite, QRectF>();
	public EventSelectedRestrictionEnzyme selectedEnz=new EventSelectedRestrictionEnzyme();

	double charHeight;
	private ViewLinearSequence view;
	public int currentReadingFrame=0;
	public boolean showProteinTranslation=true;

	
	
	public LinTrackSequence(ViewLinearSequence view)
		{
		this.view=view;
		}
	
	public void init()
		{
		sequenceLineY.clear();
		resSitePosition.clear();
		}
	
	public int place(QGraphicsScene scene, int currentY, int cposLeft, int cposRight)
		{
		AnnotatedSequence seq=view.getSequence();
		
		QFont fontRestriction=new QFont();
		fontRestriction.setPointSize(10);

		QPen penSequence=new QPen();
		penSequence.setColor(new QColor(100,100,100));
		penSequence.setWidth(2);

		//Update font choices
		QFont fontSequence=new QFont();
		fontSequence.setFamily("Courier");
		fontSequence.setPointSizeF(view.charWidth);
		charHeight=fontSequence.pointSizeF()*2;

		//////////////////////////////////////////////// Place enzymes
		
		//Find all relevant restriction enzymes, and sort by position
		LinkedList<RestrictionSite> rsites=new LinkedList<RestrictionSite>();
		for(RestrictionEnzyme enz:seq.restrictionSites.keySet())
			{
			Collection<RestrictionSite> sites=seq.restrictionSites.get(enz);
			if(view.settings.allowsRestrictionSiteCount(enz,sites.size()) || selectedEnz.enzymes.contains(enz))
				{
				for(RestrictionSite site:sites)
					if(site.cuttingUpperPos>=cposLeft && site.cuttingUpperPos<=cposRight)
						rsites.add(site);
				}
			}
		Collections.sort(rsites,new Comparator<RestrictionSite>()
			{
			public int compare(RestrictionSite o1, RestrictionSite o2)
				{
				return Double.compare(o1.cuttingUpperPos, o2.cuttingUpperPos);
				}
			});
		
		//Render the enzymes
		int siteHeightPx=20;
		int maxSiteHeight=0;
		int siteDx=-2;
		HashMap<RestrictionSite, QRectF> revsitePositionPerLine=new HashMap<RestrictionSite, QRectF>();
		for(RestrictionSite site:rsites)
			{
			QGraphicsTextItem it=new QGraphicsTextItem();
			it.setFont(fontRestriction);
			if(selectedEnz.enzymes.contains(site.enzyme))
				it.setDefaultTextColor(QColor.fromRgb(255,0,0));
			else
				it.setDefaultTextColor(QColor.fromRgb(0,0,0));
			it.setPlainText(site.enzyme.name);
			it.setPos(view.mapCharToX(site.cuttingUpperPos-cposLeft)+siteDx, currentY);
			
			//If it overlaps with other sites, move it down
			QRectF thisbr=CircView.textBR(it);
			thisbr.adjust(0, 0, 40, 0);
			int thish=1;
			sitesretry: for(;;)
				{
				for(QRectF otherbr:revsitePositionPerLine.values())
					{
					if(otherbr.intersects(thisbr))
						{
						thish++;
						it.setY(it.y()+siteHeightPx);
						thisbr=CircView.textBR(it);
						thisbr.adjust(0, 0, 40, 0);
						continue sitesretry;
						}
					}
				break;
				}
			scene.addItem(it);
			resSitePosition.put(site,thisbr);
			revsitePositionPerLine.put(site,thisbr);
			maxSiteHeight=Math.max(maxSiteHeight,thish);
			}

		//Allocate space for the restriction sites
		currentY+=maxSiteHeight*siteHeightPx+2;			
		sequenceLineY.add(currentY); //reference to sequence line

		//Draw arrows down onto sequence
		for(QRectF r:revsitePositionPerLine.values())
			{
			QGraphicsLineItem li=new QGraphicsLineItem();
			li.setPen(penSequence);
			li.setLine(r.left()-siteDx,r.bottom(),r.left()-siteDx,currentY);
			scene.addItem(li);
			}

		//Draw the sequence text
		QGraphicsLinSequenceItem titem=new QGraphicsLinSequenceItem();
		//titem.curline=curline;
		titem.cposLeft=cposLeft;
		titem.currentY=currentY;
		titem.seq=seq;
		titem.view=view;
		titem.charHeight=charHeight;
		titem.fontSequence=fontSequence;
		titem.track=this;
		scene.addItem(titem);
		currentY+=titem.boundingRect().height();
		return currentY;
		}


	@Override
	public boolean mousePressEvent(QMouseEvent event, QPointF pos)
		{
		hoveringRestrictionSite=getRestrictionSiteAt(pos);
		if(hoveringRestrictionSite!=null)
			{
			EventSelectedRestrictionEnzyme s=new EventSelectedRestrictionEnzyme();
			if(QTutil.addingKey(event))
				{
				selectedEnz.add(hoveringRestrictionSite.enzyme);
				s.enzymes.addAll(selectedEnz.enzymes);
				}
			else
				s.add(hoveringRestrictionSite.enzyme);
			view.signalUpdated.emit(s);
			return true;
			}
		else
			return false;
		}

	@Override
	public boolean contextMenuEvent(QContextMenuEvent event, QPointF pos)
		{
		return false;
		}

	@Override
	public void updateSelectionGraphics(Collection<Object> selectionItems)
		{
		int charsPerLine=view.charsPerLine;
		//double charHeight=charHeight;
		SequenceRange selection=view.selection;
		AnnotatedSequence seq=view.getSequence();
		QGraphicsScene scene=view.scene();
		
		//Draw selection
		if(view.selection!=null)
			{
			selection=selection.toNormalizedRange(seq);
			int selectFrom=selection.from;
			int selectTo=selection.to;

			QPen penSelect=new QPen();
			penSelect.setColor(new QColor(200,100,200));
			penSelect.setWidth(2);

			//this could be made a lot more efficient
			for(int curline=0;curline<sequenceLineY.size();curline++)
				{
				int cposLeft=curline*charsPerLine;
				int cposRight=(curline+1)*charsPerLine;

				int lyUpper=sequenceLineY.get(curline)+3; 
				int lyLower=lyUpper+3;

				//From-|
				if(cposLeft<=selectFrom && cposRight>selectFrom)
					{
					double x1=view.mapCharToX(selectFrom-cposLeft);
					QGraphicsLineItem li=new QGraphicsLineItem();
					li.setPen(penSelect);
					li.setLine(x1, lyUpper, x1, lyLower);
					li.setZValue(10000);
					scene.addItem(li);
					selectionItems.add(li);
					}
				//To-|
				if(cposLeft<=selectTo && cposRight>selectTo)
					{
					double x1=view.mapCharToX(selectTo-cposLeft);
					QGraphicsLineItem li=new QGraphicsLineItem();
					li.setPen(penSelect);
					li.setLine(x1, lyUpper, x1, lyLower);
					li.setZValue(10000);
					scene.addItem(li);
					selectionItems.add(li);
					}

				//Horizontal line
				if(selection.from<=selection.to)
					{
					//There can only be one line here
					if(cposLeft<selectTo && cposRight>selectFrom)
						{
						//Find boundaries, and label them
						double x1,x2;
						if(selectFrom<cposLeft)
							x1=view.mapCharToX(0);
						else
							x1=view.mapCharToX(selectFrom-cposLeft);
						if(selectTo>cposRight)
							x2=view.mapCharToX(charsPerLine);
						else
							x2=view.mapCharToX(selectTo-cposLeft);
						
						QGraphicsLineItem li=new QGraphicsLineItem();
						li.setPen(penSelect);
						li.setLine(x1, lyUpper, x2, lyUpper);
						li.setZValue(10000);
						scene.addItem(li);
						selectionItems.add(li);
						}
					}
				else
					{
					int rightmax=charsPerLine;
					if(cposRight>seq.getLength())
						rightmax=seq.getLength()-cposLeft;
					//If selection is wrapped, there can be up to two lines.
					//The left horizontal line
					if(cposLeft<selectTo) 
						{
						//Find boundaries, and label them
						double x1=view.mapCharToX(0),x2;
						if(selectTo>cposRight)
							x2=view.mapCharToX(rightmax);
						else
							x2=view.mapCharToX(selectTo-cposLeft); 
						
						QGraphicsLineItem li=new QGraphicsLineItem();
						li.setPen(penSelect);
						li.setLine(x1, lyUpper, x2, lyUpper);
						li.setZValue(10000);
						scene.addItem(li);
						selectionItems.add(li);
						}
					//The right horizontal line
					if(cposRight>selectFrom)
						{
						//Find boundaries, and label them
						double x1,x2=view.mapCharToX(rightmax);
						if(selectFrom<cposLeft)
							x1=view.mapCharToX(0);
						else
							x1=view.mapCharToX(selectFrom-cposLeft);
						
						QGraphicsLineItem li=new QGraphicsLineItem();
						li.setPen(penSelect);
						li.setLine(x1, lyUpper, x2, lyUpper);
						li.setZValue(10000);
						scene.addItem(li);
						selectionItems.add(li);
						}
					}
				}
			}
		
		
		
		//Draw restriction site if hovering
		if(hoveringRestrictionSite!=null)
			{			
			QPen penRS=new QPen();
			penRS.setColor(new QColor(255,50,50));
			penRS.setWidth(2);

			//Draw upper cut position
			if(hoveringRestrictionSite.cuttingUpperPos!=null)
				{
				int lineIndex=hoveringRestrictionSite.cuttingUpperPos/charsPerLine;
				int cposLeft=lineIndex*charsPerLine;
				double localUpper=view.mapCharToX(hoveringRestrictionSite.cuttingUpperPos-cposLeft);
				double y1=sequenceLineY.get(lineIndex);
				double y2=y1+charHeight+4;

				QGraphicsLineItem liUpper=new QGraphicsLineItem();
				liUpper.setPen(penRS);
				liUpper.setLine(localUpper, y1, localUpper, y2);
				liUpper.setZValue(10000);
				scene.addItem(liUpper);
				selectionItems.add(liUpper);				
				
				//Draw horizontal line
				if(hoveringRestrictionSite.cuttingLowerPos!=null)
					{
					double localLower=view.mapCharToX(Math.max(0,Math.min(charsPerLine,hoveringRestrictionSite.cuttingLowerPos-cposLeft)));
					QGraphicsLineItem liMid=new QGraphicsLineItem();
					liMid.setPen(penRS);
					liMid.setLine(localUpper, y2, localLower, y2);
					liMid.setZValue(10000);
					scene.addItem(liMid);
					selectionItems.add(liMid);
					}
				}

			//Draw lower cut position
			if(hoveringRestrictionSite.cuttingLowerPos!=null)
				{
				int lineIndex=hoveringRestrictionSite.cuttingLowerPos/charsPerLine;
				int cposLeft=lineIndex*charsPerLine;
				double localLower=view.mapCharToX(hoveringRestrictionSite.cuttingLowerPos-cposLeft);
				double y1=sequenceLineY.get(lineIndex);
				double y2=y1+charHeight+4;
				double y3=y1+charHeight*2;

				QGraphicsLineItem liLower=new QGraphicsLineItem();
				liLower.setPen(penRS);
				liLower.setLine(localLower, y2, localLower, y3);
				liLower.setZValue(10000);
				scene.addItem(liLower);
				selectionItems.add(liLower);
				
				//Draw horizontal line
				if(hoveringRestrictionSite.cuttingUpperPos!=null)
					{
					double localUpper=view.mapCharToX(Math.max(0,Math.min(charsPerLine,hoveringRestrictionSite.cuttingUpperPos-cposLeft)));
					QGraphicsLineItem liMid=new QGraphicsLineItem();
					liMid.setPen(penRS);
					liMid.setLine(localUpper, y2, localLower, y2);
					liMid.setZValue(10000);
					scene.addItem(liMid);
					selectionItems.add(liMid);
					}
				}

			//Also draw a background around motif position
			for(SequenceRange segment:hoveringRestrictionSite.motif.segmentRanges(seq, charsPerLine))
				{
				int curline=segment.from/charsPerLine;
				int cposLeft=curline*charsPerLine;
				double local1=view.mapCharToX(segment.from-cposLeft);
				double local2=view.mapCharToX(segment.to-cposLeft);
				double y1=sequenceLineY.get(curline);
				double y3=y1+charHeight*2;
				
				QGraphicsRectItem rect=new QGraphicsRectItem();
				rect.setZValue(-10);
				rect.setBrush(new QBrush(QColor.fromRgb(230,230,200)));
				rect.setPen(new QPen(QColor.fromRgba(0)));
				rect.setRect(local1, y1, local2-local1, y3-y1);
				scene.addItem(rect);
				selectionItems.add(rect);
				}
			}
		}

	

	private RestrictionSite getRestrictionSiteAt(QPointF pos)
		{
		for(RestrictionSite s:resSitePosition.keySet())
			{
			QRectF r=resSitePosition.get(s);
			if(r.contains(pos))
				return s;
			}
		return null;
		}

	@Override
	public void mouseMoveEvent(QMouseEvent event, QPointF pos)
		{
		//Check if hovering a restriction site
		RestrictionSite lastHover=hoveringRestrictionSite;
		hoveringRestrictionSite=getRestrictionSiteAt(pos);
		if(lastHover!=hoveringRestrictionSite)
			view.updateSelectionGraphics();
		}

	@Override
	public void handleEvent(Object ob)
		{
		if(ob instanceof EventSelectedAnnotation)
			{
			SeqAnnotation annot=((EventSelectedAnnotation)ob).annot;
			if(annot!=null)
				{
				currentReadingFrame=annot.getFrame();
				view.buildSceneFromDoc();
				}
			}		
		else if(ob instanceof EventSelectedRestrictionEnzyme)
			{
			EventSelectedRestrictionEnzyme enz=(EventSelectedRestrictionEnzyme)ob;
			selectedEnz=enz;
			view.buildSceneFromDoc();
			}
		}
	}
