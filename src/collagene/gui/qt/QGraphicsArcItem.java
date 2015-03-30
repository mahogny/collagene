package collagene.gui.qt;

import com.trolltech.qt.gui.QGraphicsEllipseItem;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QStyleOptionGraphicsItem;
import com.trolltech.qt.gui.QWidget;

public class QGraphicsArcItem extends QGraphicsEllipseItem
	{
	public void paint(QPainter painter, QStyleOptionGraphicsItem option, QWidget widget) 
		{
		painter.setPen(pen());
		painter.setBrush(brush());
		painter.drawArc(rect(), startAngle(), spanAngle());

		//  if (option->state & QStyle::State_Selected)
		//      qt_graphicsItem_highlightSelected(this, painter, option);
		}

	}
