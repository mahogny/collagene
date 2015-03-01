package gui.paneLinear;

import gui.paneCircular.CircView;
import gui.sequenceWindow.AnnotationWindow;
import gui.sequenceWindow.SeqViewSettingsMenu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

import restrictionEnzyme.RestrictionEnzyme;
import seq.AnnotatedSequence;
import seq.Orientation;
import seq.RestrictionSite;
import seq.SequenceRange;
import seq.SeqAnnotation;

import com.trolltech.qt.QSignalEmitter;
import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.core.QRectF;
import com.trolltech.qt.core.Qt.BrushStyle;
import com.trolltech.qt.core.Qt.MouseButton;
import com.trolltech.qt.core.Qt.ScrollBarPolicy;
import com.trolltech.qt.gui.QAction;
import com.trolltech.qt.gui.QBrush;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QContextMenuEvent;
import com.trolltech.qt.gui.QFont;
import com.trolltech.qt.gui.QGraphicsLineItem;
import com.trolltech.qt.gui.QGraphicsPolygonItem;
import com.trolltech.qt.gui.QGraphicsScene;
import com.trolltech.qt.gui.QGraphicsTextItem;
import com.trolltech.qt.gui.QGraphicsView;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QMouseEvent;
import com.trolltech.qt.gui.QPen;
import com.trolltech.qt.gui.QPolygonF;
import com.trolltech.qt.gui.QResizeEvent;
import com.trolltech.qt.gui.QSizePolicy;

/**
 * Display sequence, broken over several lines
 * 
 * @author Johan Henriksson
 *
 */
public class ViewLinearSequence extends QGraphicsView
	{
	public AnnotatedSequence seq=new AnnotatedSequence();
	
	public int widthInChars=-1; //-1 means to choose automatically. 

	//Current annotation, if a context menu is opened
	private SeqAnnotation curAnnotation=null;

	
	private HashMap<QRectF,SeqAnnotation> mapAnnotations=new HashMap<QRectF, SeqAnnotation>();
	
	public SeqViewSettingsMenu settings=new SeqViewSettingsMenu();

	
	public QSignalEmitter.Signal1<SequenceRange> signalSelectionChanged=new Signal1<SequenceRange>();
	public QSignalEmitter.Signal0 signalUpdated=new Signal0();

	private Collection<QGraphicsLineItem> selectionItems=new LinkedList<QGraphicsLineItem>();
	private SequenceRange selection=null;
	private boolean isSelecting=false;

	QFont fontSequence=new QFont();
	private ArrayList<Integer> sequenceLineY=new ArrayList<Integer>();
	int charsPerLine;
	public double charWidth=10;
	public double charHeight=17;

	/**
	 * Set current sequence
	 */
	public void setSequence(AnnotatedSequence seq)
		{
		this.seq=seq;
		buildSceneFromDoc();
		}


	/**
	 * Get current selection
	 */
	public SequenceRange getSelection()
		{
		return selection;
		}
	
	/**
	 * Set current selection
	 */
	public void setSelection(SequenceRange s)
		{
		selection=s;
		updateSelectionGraphics();
		}
	
	
	/**
	 * Constructor
	 */
	public ViewLinearSequence()
		{
		setBackgroundBrush(new QBrush(QColor.fromRgb(255,255,255)));
		
    setHorizontalScrollBarPolicy(ScrollBarPolicy.ScrollBarAlwaysOff);  //is there a newer version??
    setVerticalScrollBarPolicy(ScrollBarPolicy.ScrollBarAlwaysOn);

		setMouseTracking(true);
		setEnabled(true);

		setSizePolicy(QSizePolicy.Policy.Expanding,QSizePolicy.Policy.Expanding);

		charsPerLine=80; //not proper
		
		setScene(new QGraphicsScene());
		
		buildSceneFromDoc();
		}

	
	
	private HashMap<RestrictionSite, QRectF> revsitePosition=new HashMap<RestrictionSite, QRectF>();

	
	/**
	 * Build the scene from the document. This is equivalent to repainting
	 */
	public void buildSceneFromDoc()
		{
		QGraphicsScene scene=scene();
		scene.clear();
		mapAnnotations.clear();
		
		//Note - it is good to have a separate scene builder class, for making PDFs
				
		QPen penSequence=new QPen();
		penSequence.setColor(new QColor(100,100,100));
		penSequence.setWidth(2);
		
		fontSequence.setFamily("Courier");
		fontSequence.setPointSizeF(charWidth);
		
		QFont fontAnnotation=new QFont();
		fontAnnotation.setPointSizeF(10);

		QFont fontRestriction=new QFont();
		fontRestriction.setPointSize(10);
		
		//Update how many chars fit on each line
		charsPerLine=widthInChars;
		if(charsPerLine==-1)
			charsPerLine=80; //TODO try and figure out optimum
		
		setMinimumWidth((int)mapCharToX(charsPerLine)+100);    //TODO need to be redone once in a while!

		//Render each line. Remember position
		sequenceLineY.clear();
		int currentY=10;
		revsitePosition.clear();
		for(int curline=0;curline<seq.getLength()/charsPerLine+1;curline++)
			{
			int cposLeft=curline*charsPerLine;
			int cposRight=(curline+1)*charsPerLine;
			
			
			
			//Find all relevant restriction enzymes
			LinkedList<RestrictionSite> rsites=new LinkedList<RestrictionSite>();
			for(RestrictionEnzyme enz:seq.restrictionSites.keySet())
				{
				Collection<RestrictionSite> sites=seq.restrictionSites.get(enz);
				if(settings.allowsRestrictionSiteCount(enz,sites.size()))
					{
					for(RestrictionSite site:sites)
						if(site.cuttingUpperPos>=cposLeft && site.cuttingUpperPos<=cposRight)
							rsites.add(site);
					}
				}
			
			//Sort sites left to right
			Collections.sort(rsites,new Comparator<RestrictionSite>()
				{
				public int compare(RestrictionSite o1, RestrictionSite o2)
					{
					return Double.compare(o1.cuttingUpperPos, o2.cuttingUpperPos);
					}
				});
			
			
			//Place enzymes
			int siteHeightPx=20;
			int maxSiteHeight=0;
			int siteDx=-2;
			HashMap<RestrictionSite, QRectF> revsitePositionPerLine=new HashMap<RestrictionSite, QRectF>();
			for(RestrictionSite site:rsites)
				{
				QGraphicsTextItem it=new QGraphicsTextItem();
				it.setFont(fontAnnotation);
				it.setPlainText(site.enzyme.name);
				it.setPos(mapCharToX(site.cuttingUpperPos-cposLeft)+siteDx, currentY);
				
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
				revsitePosition.put(site,thisbr);
				revsitePositionPerLine.put(site,thisbr);
				maxSiteHeight=Math.max(maxSiteHeight,thish);
				}
			
			//Allocate space for the restriction sites
			currentY+=maxSiteHeight*siteHeightPx+2;			
			
			//Draw arrows down onto sequence
			for(QRectF r:revsitePositionPerLine.values())
				{
				QGraphicsLineItem li=new QGraphicsLineItem();
				li.setPen(penSequence);
				li.setLine(r.left()-siteDx,r.bottom(),r.left()-siteDx,currentY);
				scene.addItem(li);
				}
			
			//Draw the sequence text
			sequenceLineY.add(currentY);
			
			QGraphicsLinSeqTextAnnotationItem titem=new QGraphicsLinSeqTextAnnotationItem();
			titem.curline=curline;
			titem.currentY=currentY;
			titem.seq=seq;
			titem.view=this;
			scene.addItem(titem);
			
			currentY+=charHeight/2; ///??????
			
			//Draw annotation
			int currentAnnotationHeight=0;
			for(SeqAnnotation annot:seq.annotations)
				{
				//Check if this feature is in range
				if(annot.to>cposLeft && annot.from<cposRight)
					{
					//Annotation should go beneath sequence, above position line
					int basey=currentY+currentAnnotationHeight*20;
					int polyyup=basey+30;
					int polyydown=basey+48;
					int polyymid=basey+36;
					
					QPolygonF poly=new QPolygonF();
					
					double frompos;
					if(annot.from>=cposLeft)
						{
						//Annotation starts here
						frompos=mapCharToX(annot.from-cposLeft);
						poly.add(frompos, polyydown);
						if(annot.orientation==Orientation.REVERSE)
							poly.add(mapCharToX(annot.from-1-cposLeft)+5, polyymid);
						poly.add(frompos, polyyup); 
						}
					else
						{
						//Annotation continued from before
						frompos=mapCharToX(0);
						poly.add(frompos, polyydown); 
						poly.add(frompos-3, polyyup+7); 
						poly.add(frompos+3, polyyup+3); 
						poly.add(frompos, polyyup); 
						}

					double topos;
					if(annot.to<=cposRight)
						{
						//Annotation ends here
						topos=mapCharToX(annot.to+1-cposLeft);
						poly.add(topos, polyyup);  
						if(annot.orientation==Orientation.FORWARD)
							poly.add(mapCharToX(annot.to+1-cposLeft)+5, polyymid);
						poly.add(mapCharToX(annot.to+1-cposLeft), polyydown); 
						}
					else
						{
						//Continues on next line
						topos=mapCharToX(charsPerLine+1);
						poly.add(topos, polyyup); 
						poly.add(topos+3, polyyup+3); 
						poly.add(topos-3, polyyup+7); 
						poly.add(topos, polyydown); 
						}
					
					QRectF newrect=new QRectF();
					newrect.setRect(frompos, polyyup, topos-frompos, polyydown-polyyup);
					mapAnnotations.put(newrect, annot);

					QPen pen=new QPen();
					pen.setColor(QColor.fromRgb(0,0,0));
					
					QBrush brush=new QBrush();
					brush.setStyle(BrushStyle.SolidPattern);
					brush.setColor(QColor.fromRgbF(annot.colorR, annot.colorG, annot.colorB));
					
					QGraphicsPolygonItem pi=new QGraphicsPolygonItem();
					pi.setPolygon(poly);
					pi.setPen(pen);
					pi.setBrush(brush);
					scene.addItem(pi);
					
					QGraphicsTextItem ti=new QGraphicsTextItem();
					ti.setPlainText(annot.name);
					ti.setPos(frompos+2, polyyup-2);
					scene.addItem(ti);
					
					currentAnnotationHeight++;
					}
				}
			
			
			
			//Account for the additions of all features
			currentY+=(currentAnnotationHeight+1)*20;
			
			//Move to next line
			currentY+=40;
			}
		

		
		
		setSceneRect(0, 0, mapCharToX(charsPerLine)+30, currentY+50);
		
		selectionItems.clear();
		updateSelectionGraphics();
		}	
	
	

	
	
	private void updateSelectionGraphics()
		{
		//Remove previous selection
		for(QGraphicsLineItem i:selectionItems)
			scene().removeItem(i);
		selectionItems.clear();

		//Draw selection
		if(selection!=null)
			{
			int selectFrom=selection.getLower();
			int selectTo=selection.getUpper();

			QPen penSelect=new QPen();
			penSelect.setColor(new QColor(200,100,200));
			penSelect.setWidth(2);

			//TODO this could be made a lot more efficient
			for(int curline=0;curline<sequenceLineY.size();curline++)
				{
				int cposLeft=curline*charsPerLine;
				int cposRight=(curline+1)*charsPerLine;

				if(cposLeft<selectTo && cposRight>selectFrom)
					{
					int ly=sequenceLineY.get(curline);

					//Find boundaries, and label them
					double x1,x2;
					if(selectFrom<cposLeft)
						x1=mapCharToX(0);
					else
						{
						x1=mapCharToX(selectFrom-cposLeft);
						int ly2=ly+5;
						QGraphicsLineItem li=new QGraphicsLineItem();
						li.setPen(penSelect);
						li.setLine(x1, ly, x1, ly2);
						li.setZValue(10000);
						scene().addItem(li);
						selectionItems.add(li);
						}
					if(selectTo>cposRight)
						x2=mapCharToX(charsPerLine)-1;
					else
						{
						x2=mapCharToX(selectTo-cposLeft)-1;
						int ly2=ly+5;
						QGraphicsLineItem li=new QGraphicsLineItem();
						li.setPen(penSelect);
						li.setLine(x2, ly, x2, ly2);
						li.setZValue(10000);
						scene().addItem(li);
						selectionItems.add(li);
						}
					
					//Draw the horizontal line
					QGraphicsLineItem li=new QGraphicsLineItem();
					li.setPen(penSelect);
					li.setLine(x1, ly, x2, ly);
					li.setZValue(10000);
					scene().addItem(li);
					selectionItems.add(li);
					}
				}
			}
		
		//Draw restriction site if hovering
		if(hoveringRestrictionSite!=null)
			{
			
			int lineIndex=hoveringRestrictionSite.cuttingUpperPos/charsPerLine;
			int cposLeft=lineIndex*charsPerLine;
			double localUpper=mapCharToX(hoveringRestrictionSite.cuttingUpperPos-cposLeft);
			
			QPen penSelect=new QPen();
			penSelect.setColor(new QColor(0,0,255));
			penSelect.setWidth(2);

			double y1=sequenceLineY.get(lineIndex);
			double y2=y1+charHeight+4;
			double y3=y1+charHeight*2;

			QPen penRS=penSelect;
			
			QGraphicsLineItem liUpper=new QGraphicsLineItem();
			liUpper.setPen(penRS);
			liUpper.setLine(localUpper, y1, localUpper, y2);
			liUpper.setZValue(10000);
			scene().addItem(liUpper);
			selectionItems.add(liUpper);

			if(hoveringRestrictionSite.cuttingLowerPos!=null)
				{
				double localLower=mapCharToX(hoveringRestrictionSite.cuttingLowerPos-cposLeft);

				QGraphicsLineItem liMid=new QGraphicsLineItem();
				liMid.setPen(penRS);
				liMid.setLine(localUpper, y2, localLower, y2);
				liMid.setZValue(10000);
				scene().addItem(liMid);
				selectionItems.add(liMid);
				
				QGraphicsLineItem liLower=new QGraphicsLineItem();
				liLower.setPen(penRS);
				liLower.setLine(localLower, y2, localLower, y3);
				liLower.setZValue(10000);
				scene().addItem(liLower);
				selectionItems.add(liLower);
				}

			
			//TODO should draw a background below full recognition sequence
			}
		}
	

	
	double mapCharToX(int pos)
		{
		return 10+pos*charWidth;
		}
	private int mapXtoChar(double x)
		{
		return (int)((x-10)/charWidth);
		}
	private int mapXYtoPos(double x, double y)
		{
//		System.out.println();
//		System.out.println("# "+sequenceLineY.size());
		//Find which line
		for(int i=0;i<sequenceLineY.size();i++)
			{
			int y1=sequenceLineY.get(i);
			int y2=y1+30; //sequenceLineY.get(i+1)
//			System.out.println(y+"   "+y1+"   "+y2);
			if(y>y1 && y<y2)
				{
				//Find where on line
				int c=mapXtoChar(x);
				if(c>=0 && c<charsPerLine)
					{
					int total=i*charsPerLine + c;
					if(total>seq.getLength())
						return -1;
					else
						return total;  
					}
				else
					return -1;
				}
			}
		return -1;
		}



	
	
	
	
	/**
	 * Create a context menu if right-clicking
	 */
	@Override
	protected void contextMenuEvent(QContextMenuEvent event)
		{
		QPointF pos=mapToScene(event.pos());
		curAnnotation=getAnnotationAt(pos);
		if(curAnnotation!=null)
			{
			QMenu mPopup=new QMenu();
			QAction miEdit=mPopup.addAction("Edit annotation");
			QAction miDeleteAnnot=mPopup.addAction("Delete annotation");
			
			miEdit.triggered.connect(this,"actionEditAnnotation()");
			miDeleteAnnot.triggered.connect(this,"actionDeleteAnnotation()");
			
			mPopup.exec(event.globalPos());
			}
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
	
	/**
	 * Action: Edit current annotation
	 */
	public void actionEditAnnotation()
		{
		AnnotationWindow w=new AnnotationWindow();
		SeqAnnotation a=curAnnotation;
		w.setAnnotation(a);
		w.exec();
		if(w.getAnnotation()!=null)
			{
			//seq.annotations.add(w.getAnnotation());
			signalUpdated.emit();
			//selectionItems.updateSequence();
			}
		
		}
	
	public void actionDeleteAnnotation()
		{
		seq.annotations.remove(curAnnotation);
		signalUpdated.emit();
		}
	
	
	/**
	 * Handle mouse button pressed events 
	 */
	public void mousePressEvent(QMouseEvent event)
		{
		QPointF pos=mapToScene(event.pos());
		if(event.button()==MouseButton.RightButton)
			{

			}
		
		if(event.button()==MouseButton.LeftButton)
			{
			curAnnotation=getAnnotationAt(pos);
			if(curAnnotation!=null)
				{
				selection=new SequenceRange();
				selection.from=curAnnotation.from;
				selection.to=curAnnotation.to;
				isSelecting=false;
				signalSelectionChanged.emit(selection);
				updateSelectionGraphics();  
				}
			else
				{
				//Try to select a region
				int curindex=mapXYtoPos(pos.x(), pos.y());
				if(curindex!=-1)
					{
					selection=new SequenceRange();
					selection.from=selection.to=curindex;
					isSelecting=true;
					signalSelectionChanged.emit(selection);
					updateSelectionGraphics();  
					}
				}
			}
		
		}

	/**
	 * Handle mouse button release
	 */
	public void mouseReleaseEvent(QMouseEvent event)
		{
		isSelecting=false;
		}

	private RestrictionSite hoveringRestrictionSite=null;


	/**
	 * Handle mouse move events
	 */
	public void mouseMoveEvent(QMouseEvent event)
		{
		QPointF pos=mapToScene(event.pos());
		
		//Update selection if currently selecting
		int curindex=mapXYtoPos(pos.x(), pos.y());
		if(isSelecting && curindex!=-1)
			{
			selection.to=curindex;
			signalSelectionChanged.emit(selection);
			updateSelectionGraphics();
			}
		

		//Check if hovering a restriction site
		RestrictionSite lastHover=hoveringRestrictionSite;
		hoveringRestrictionSite=null;
		for(RestrictionSite s:revsitePosition.keySet())
			{
			QRectF r=revsitePosition.get(s);
			if(r.contains(pos))
				hoveringRestrictionSite=s;
			}
		if(lastHover!=hoveringRestrictionSite)
			updateSelectionGraphics();
		
		}
	

	/**
	 * Handle mouse wheel events
	 */
	/*
	public void wheelEvent(QWheelEvent event)
		{
		}
*/
	
	/**
	 * Handle resize events
	 */
	public void resizeEvent(QResizeEvent event)
		{
		// Call the subclass resize so the scrollbars are updated correctly
		super.resizeEvent(event);
		}

	
	
	
	}
