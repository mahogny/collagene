package gui.digest;

import java.util.LinkedList;

import com.trolltech.qt.core.Qt.BrushStyle;
import com.trolltech.qt.core.Qt.ScrollBarPolicy;
import com.trolltech.qt.gui.QBrush;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QFont;
import com.trolltech.qt.gui.QGraphicsRectItem;
import com.trolltech.qt.gui.QGraphicsScene;
import com.trolltech.qt.gui.QGraphicsView;
import com.trolltech.qt.gui.QPen;

/**
 * 
 * Diffusion rates
 * http://www.ncbi.nlm.nih.gov/pmc/articles/PMC1300351/pdf/10388779.pdf
 * 
 * D ~  1/sqrt(bp)
 * 
 * @author Johan Henriksson
 *
 */
public class SimulatedGel extends QGraphicsView
	{

	
	private double scaling=50;
	
	
	LinkedList<SimulatedLane> lane=new LinkedList<SimulatedLane>();
	
	
	
	
	public SimulatedGel()
		{
		setScene(new QGraphicsScene());
		updategraph();
//		setSizePolicy(QSizePolicy.Policy.Expanding,QSizePolicy.Policy.Expanding);
//		setSizePolicy(QSizePolicy.Policy.Expanding,QSizePolicy.Policy.Expanding);
    setHorizontalScrollBarPolicy(ScrollBarPolicy.ScrollBarAlwaysOff);  
    setVerticalScrollBarPolicy(ScrollBarPolicy.ScrollBarAlwaysOff);
//    setbac

    //TODO black background
    
    updategraph();
		}


	private void updategraph()
		{
		QGraphicsScene scene=scene();
		scene.clear();
		
		QPen penSelect=new QPen();
		penSelect.setColor(new QColor(0,0,0));
		penSelect.setWidth(2);
	
		QBrush brush=new QBrush();
		brush.setColor(QColor.fromRgb(0));
		brush.setStyle(BrushStyle.SolidPattern);
		
		
		QFont font=new QFont();
		font.setFamily("Courier");
		font.setPointSize(10);

		int lanespacing=30;
		int lanewidth=20;
		
		Double minbp=null;
		for(int i=0;i<getNumLanes();i++)
			{
			SimulatedLane thelane=lane.get(i);
			if(!thelane.mapPosWeight.isEmpty())
				{
				double d=thelane.mapPosWeight.firstKey();
				if(minbp==null || d<minbp)
					minbp=d;
				}
			}
		System.out.println("minbp "+minbp);
		if(minbp!=null)
			scaling=500*Math.sqrt(minbp); 
		
		for(int i=0;i<getNumLanes();i++)
			{
			SimulatedLane thelane=lane.get(i);
			for(double size:thelane.mapPosWeight.keySet())
				{
				QGraphicsRectItem ri=new QGraphicsRectItem();
				int x1=i*lanespacing;
				int x2=i*lanespacing+lanewidth;
				int y1=mapSize(size)+1;
				int y2=mapSize(size)-1;
				ri.setRect(x1, y1, x2-x1, y2-y1);
				ri.setPen(penSelect);
				ri.setBrush(brush);
				scene.addItem(ri);
				}
			}
		
		
			
				/*
				QGraphicsTextItem tiUpper=new QGraphicsTextItem();
				tiUpper.setPlainText(""+enz.sequence.charAt(i));
				tiUpper.setFont(font);
				tiUpper.setPos(10+charw*i, 10);
				scene.addItem(tiUpper);
				*/
		setSceneRect(0, 0, getNumLanes()*lanespacing,500);
		}


	private int mapSize(double bp)
		{
		return (int)(scaling/Math.sqrt(bp)); 
		}
	
	private int getNumLanes()
		{
		return lane.size();
		}


	public void addLane(SimulatedLane simulatedLane)
		{
		lane.add(simulatedLane);
		updategraph();
		}


	
	}
