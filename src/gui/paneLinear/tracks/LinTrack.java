package gui.paneLinear.tracks;

import java.util.Collection;

import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.gui.QContextMenuEvent;
import com.trolltech.qt.gui.QGraphicsScene;
import com.trolltech.qt.gui.QMouseEvent;

/**
 *
 * One track in the linear sequence view
 * 
 * @author Johan Henriksson
 *
 */
public interface LinTrack
	{
	public void initPlacing();

	public int place(QGraphicsScene scene, int currentY, int cposLeft, int cposRight);

	public boolean mousePressEvent(QMouseEvent event, QPointF pos);

	public boolean contextMenuEvent(QContextMenuEvent event, QPointF pos);

	public void updateSelectionGraphics(Collection<Object> selectionItems);

	public void mouseMoveEvent(QMouseEvent event, QPointF pos);

	public void handleEvent(Object ob);
	}
