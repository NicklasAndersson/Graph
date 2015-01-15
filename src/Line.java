import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.Serializable;

/**
 * Created by Nicklas on 2015-01-14.
 */
class Line extends JComponent implements Serializable{
    private static final long serialVersionUID = 1654615041397271455L;

    private int x1, y1, x2, y2;
    private City c1,c2;

    Line(int x1, int y1, int x2, int y2, City c1, City c2) {
        this.x1 = x1 + 10;
        this.y1 = y1 + 10;
        this.x2 = x2 + 10;
        this.y2 = y2 + 10;
        this.c1 = c1;
        this.c2 = c2;

        setVisible(true);
        addFocusListener(new FocusLis());
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.blue);
        g.drawLine(this.x1, this.y1, this.x2, this.y2);
    }

    private class FocusLis implements FocusListener {
        public void focusLost(FocusEvent fev) {
            repaint();
        }

        public void focusGained(FocusEvent fev) {
            repaint();
        }
    }//FocusLis


    public boolean contains(int x, int y) {
            if(pointToLineDistance(getPointA(),getPointB(),new Point(x,y))< 20){
                return true;
            }else{
                return false;
            }

    }

    public Point getPointA(){
        return new Point(this.x1,this.y1);
    }

    public Point getPointB(){
        return new Point(this.x2,this.y2);
    }

    public double pointToLineDistance(Point A, Point B, Point P) {
        double normalLength = Math.sqrt((B.x-A.x)*(B.x-A.x)+(B.y-A.y)*(B.y-A.y));
        return Math.abs((P.x-A.x)*(B.y-A.y)-(P.y-A.y)*(B.x-A.x))/normalLength;
    }

    public City getC1(){
        return c1;
    }

    public City getC2(){
        return c2;
    }
}
