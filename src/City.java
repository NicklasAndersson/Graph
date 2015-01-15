import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.Serializable;

/**
 * Created by Nicklas on 2015-01-04.
 */
class City extends JComponent implements Comparable<City>, Serializable {
    private static final long serialVersionUID = 3770993100084039649L;

    private boolean movable;
    private boolean selected;
    int x, y;

    private String name;

    City(String nameS) {
        setName(nameS);
    }

    City(int x, int y, String nameS) {
        this.x = x;
        this.y = y;
        setName(nameS);
        setBounds(x-10, y-10, 200, 200);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        MoveLis ml = new MoveLis();
        addMouseListener(ml);
        addMouseMotionListener(ml);
        addKeyListener(new ArrowLis());
        addFocusListener(new FocusLis());
        setOpaque(false);

        setMaximumSize(new Dimension(20, 20));
        setMinimumSize(new Dimension(20, 20));
        setPreferredSize(new Dimension(20, 20));

        setMovable(false);//Default
        setSelected(false);
        repaint();
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setSelected(boolean b) {
        selected = b;
        repaint();
    }

    public boolean getSelected() {
        return selected;
    }

    void setMovable(boolean movable) {
        this.movable = movable;
    }

    public boolean getMovable() {
        return movable;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (!selected) {
            super.paintComponent(g);
            g.setColor(Color.BLUE);
            g.fillOval(0,0,20,20);

        } else {
            super.paintComponent(g);
            g.setColor(Color.RED);
            g.fillOval(0,0,20,20);

        }

    }

    @Override
    public int compareTo(@SuppressWarnings("NullableProblems") City c) {
        return name.compareTo(c.toString());
    }

    class MoveLis extends MouseAdapter {
        int dx = 0, dy = 0;

        @Override
        public void mousePressed(MouseEvent mev) {
            if (movable) {
                dx = mev.getX();
                dy = mev.getY();
                requestFocusInWindow();
            }
        }

        @Override
        public void mouseDragged(MouseEvent mev) {
            if (movable) {
                int x = getX() - dx + mev.getX();
                int y = getY() - dy + mev.getY();
                setLocation(x, y);
            }
        }
    }//MoveLis

    private class ArrowLis extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent kev) {
            if (movable) {
                int x = getX();
                int y = getY();
                switch (kev.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        x--;
                        break;
                    case KeyEvent.VK_RIGHT:
                        x++;
                        break;
                    case KeyEvent.VK_UP:
                        y--;
                        break;
                    case KeyEvent.VK_DOWN:
                        y++;
                        break;
                }
                setLocation(x, y);
            }
        }
    }//ArrowLis PilLyss

    private class FocusLis implements FocusListener {
        public void focusLost(FocusEvent fev) {
            repaint();
        }

        public void focusGained(FocusEvent fev) {
            repaint();
        }
    }//FocusLis


    @Override
    public boolean contains(int x, int y) {
        return x >= 0 && x <= 20 && y >= 0 && y <= 20;
    }

    @Override
    public String toString() {
        return name + " ";

    }
}
