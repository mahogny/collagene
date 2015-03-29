package gui.digest;

import java.util.LinkedList;

import com.trolltech.qt.core.QRectF;
import com.trolltech.qt.core.Qt.BrushStyle;
import com.trolltech.qt.gui.QBrush;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QFont;
import com.trolltech.qt.gui.QPaintEvent;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QPen;
import com.trolltech.qt.gui.QWidget;

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
public class SimulatedGel extends QWidget
	{
	
	private LinkedList<SimulatedLane> lane=new LinkedList<SimulatedLane>();
	
	private Double current=null;
	public double zoom=1;
	private int offsetSide=20;
	
	
	public SimulatedGel()
		{
		repaint();
		}

	double scaling=50;
	private int lanespacing=30;

	@Override
	protected void paintEvent(QPaintEvent event)
		{
		super.paintEvent(event);
		
		QPainter pm=new QPainter(this);
		pm.fillRect(0, 0, width(), height(), QColor.fromRgb(255,255,255));
		
		QPen penBand=new QPen();
		penBand.setColor(new QColor(0,0,0));
		penBand.setWidth(2);
	
		QBrush brush=new QBrush();
		brush.setColor(QColor.fromRgb(0));
		brush.setStyle(BrushStyle.SolidPattern);
	
	
		QFont font=new QFont();
		font.setFamily("Courier");
		font.setPointSize(10);
	
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
		if(minbp!=null)
			scaling=height()*zoom*Math.sqrt(minbp); 

		if(current!=null)
			{
			QPen penCurrent=new QPen();
			penCurrent.setColor(new QColor(255,0,0));
			pm.setPen(penCurrent);
			pm.drawLine(0, mapSize(current), width(), mapSize(current));
			}

		for(int i=0;i<getNumLanes();i++)
			{
			SimulatedLane thelane=lane.get(i);
			for(double size:thelane.mapPosWeight.keySet())
				{
				int x1=offsetSide+i*lanespacing;
				int x2=x1+lanewidth;
				int y1=mapSize(size)+1;
				int y2=mapSize(size)-1;
				
				pm.setPen(penBand);
				pm.setBrush(brush);
				pm.drawRect(new QRectF(x1, y1, x2-x1, y2-y1));
				}
			}
	
		pm.end();
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
		setMinimumWidth(offsetSide*2+getNumLanes()*lanespacing);
		repaint();
		}

	public void setCurrent(Double current)
		{
		this.current=current;
		repaint();
		}


	public void clearLanes()
		{
		lane.clear();
		repaint();
		}


	
	}
