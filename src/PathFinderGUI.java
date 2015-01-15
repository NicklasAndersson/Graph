import graphs.Edge;
import graphs.GraphMethods;
import graphs.ListGraph;


import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Nicklas on 2015-01-04.
 *
 * Inlämningsuppgift PROG2
 * Betygsanspråk: A
 * 
 */
class PathFinderGUI extends JFrame{
    private String currentFile = null;
    private JFileChooser jfcN;
    private JFileChooser jfcSO;

    private ListGraph<City> graph = new ListGraph<City>();

    private boolean stateChanged = false;
    private Map map;

    ArrayList<Line> lines = new ArrayList<Line>();

    private City c1 = null, c2 = null;

    private MouseLis ml = new MouseLis();
    private MouseLisCity mlC = new MouseLisCity();

    private MouseLineLis mll = new MouseLineLis();

    PathFinderGUI() {
        super("PathFinder 0.0.1");
        setLayout(new BorderLayout());

        //Init file IO
        String str = System.getProperty("user.dir");
        jfcN = new JFileChooser(str);
        jfcSO = new JFileChooser(str);

        FileNameExtensionFilter filter =
                new FileNameExtensionFilter("Pathfinder", "sav", "save", "spara");
        jfcSO.addChoosableFileFilter(filter);

        FileNameExtensionFilter filter2 =
                new FileNameExtensionFilter("Bilder", "jpg", "png", "gif");
        jfcN.addChoosableFileFilter(filter2);

        //Header
        JPanel head = new JPanel();
        head.setLayout(new BoxLayout(head, BoxLayout.X_AXIS));
        add(head, BorderLayout.NORTH);

        JButton find = new JButton("Hitta väg");
        find.addActionListener(new findLis());
        head.add(find);
        JButton show = new JButton("Visa förbindelse");
        show.addActionListener(new showLis());
        head.add(show);
        JButton newPlace = new JButton("Ny plats");
        newPlace.addActionListener(new newPlaceLis());
        head.add(newPlace);
        JButton newEdge = new JButton("Ny förbindelse");
        newEdge.addActionListener(new newEdgeLis());
        head.add(newEdge);
        JButton changeEdge = new JButton("Ändra förbindelse");
        changeEdge.addActionListener(new changeEdgeLis());
        head.add(changeEdge);

        //Menubar
        JMenuBar menu = new JMenuBar();
        setJMenuBar(menu);

        JMenu arkiv = new JMenu("Arkiv");
        menu.add(arkiv);

        JMenuItem newItem = new JMenuItem("Ny");
        arkiv.add(newItem);
        newItem.addActionListener(new NewLis());
        JMenuItem openItem = new JMenuItem("Öppna");
        arkiv.add(openItem);
        openItem.addActionListener(new OpenLis());
        JMenuItem saveItem = new JMenuItem("Spara");
        arkiv.add(saveItem);
        saveItem.addActionListener(new SaveLis());
        JMenuItem saveAsItem = new JMenuItem("Spara som");
        arkiv.add(saveAsItem);
        saveItem.addActionListener(new SaveAsLis());
        JMenuItem exitItem = new JMenuItem("Avsluta");
        arkiv.add(exitItem);
        exitItem.addActionListener(new ExitLis());

        JMenu ops = new JMenu("Operationer");
        menu.add(ops);

        JMenuItem findItem = new JMenuItem("Hitta väg");
        ops.add(findItem);
        findItem.addActionListener(new findLis());
        JMenuItem showConnItem = new JMenuItem("Visa förbindelse");
        ops.add(showConnItem);
        showConnItem.addActionListener(new showLis());
        JMenuItem newPlaceItem = new JMenuItem("Ny plats");
        ops.add(newPlaceItem);
        newPlaceItem.addActionListener(new newPlaceLis());
        JMenuItem newConnItem = new JMenuItem("Ny förbindelse");
        ops.add(newConnItem);
        newConnItem.addActionListener(new newEdgeLis());
        JMenuItem changeConnItem = new JMenuItem("Ändra förbindelse");
        ops.add(changeConnItem);
        changeConnItem.addActionListener(new changeEdgeLis());

        //Main window
        pack();
        addWindowListener(new CloseLis());
        setLocationRelativeTo(null);
        setVisible(true);
    }//PathFinderGUI()

    public static void main(String[] args) {
        new PathFinderGUI();
    }

    private void nCity(int x, int y) {
        final String regexLetters = "^[a-zA-ZåöäåÖÅÄ]+$";
        String name;

        final String text = "Platsens namn: ";

        City nCity;

        try {
            boolean run = false;
            do {
                Form form = new Form("", text);
                int r = JOptionPane.showConfirmDialog(PathFinderGUI.this, form, "Ny Plats: ", JOptionPane.OK_CANCEL_OPTION);
                if (r != JOptionPane.OK_OPTION)
                    return;

                name = form.getOne();

                if (!name.matches(regexLetters)) {
                    run = true;
                    JOptionPane.showMessageDialog(PathFinderGUI.this, (text + " Innehåller inte bara bokstäver"));
                    Form form1 = new Form(name, text);
                } else
                    run = false;
            } while (run);

            nCity = new City(x, y, name);

            Lab l = new Lab(name, x, y);
            map.add(l);

            graph.addNode(nCity);
            nCity.addMouseListener(mlC);
            map.add(nCity);
            stateChanged = true; //changes made

        } catch (NumberFormatException nfe) {
            err("Fel vid inmating av nummer");
        }

    }//nCity

    private void close() {
        if (stateChanged) {
            int response = JOptionPane.showConfirmDialog(PathFinderGUI.this, "Ändringar ha skett vill du avsluta ändå?", "Ändrat, avsluta ändå?", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                System.exit(0);
            } else {
                save();
            }
        } else {
            System.exit(0);
        }
    }

    private void save() {
        int r = jfcSO.showSaveDialog(PathFinderGUI.this);
        if (r == JFileChooser.APPROVE_OPTION) {
            File f = jfcSO.getSelectedFile();
            String filename = f.getAbsolutePath();
            currentFile = filename;

            try {
                FileOutputStream fos = new FileOutputStream(filename);
                ObjectOutputStream oos = new ObjectOutputStream(fos);

                oos.writeObject(graph);
                oos.writeObject(lines);
                oos.writeObject(map.getFilename());

                oos.close();
                fos.close();

                stateChanged = false; //all changes saved
            } catch (FileNotFoundException fnf) {
                err("Filen kunde inte hittas");
            } catch (IOException Ioe) {
                err("Något gick fel när filen försöktes sparas");
                System.err.print(Ioe.toString()); //Details for uncommon errors.
            }
        }
    }

    private void debug(String s) {
        System.out.println(s);
    }

    private void err(String s) {
        JOptionPane.showMessageDialog(PathFinderGUI.this, ("Fel: \n" + s), "FEL", JOptionPane.ERROR_MESSAGE);
    }

    private void resetSelection() {
        if (c1 != null && c2 != null) {
            c1.setSelected(false);
            c2.setSelected(false);
        } else if (c1 != null) {
            c1.setSelected(false);
        } else if (c2 != null) {
            c2.setSelected(false);
        }

        c1 = c2 = null;
    }

    private class MouseLisCity extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent mev) {
            City c = (City) mev.getSource();
            if (c1 == null) {
                c1 = c;
                c1.setSelected(true);
            } else if (c2 == null && c != c1) {
                c2 = c;
                c2.setSelected(true);
            } else {
                //Not c1 or c2
                if (c1 == c2 || c2 != null) {
                    c1.setSelected(false);
                    c2.setSelected(false);
                }

                c1 = null;
                c2 = null;
            }
            repaint();
        }
    }

    private class MouseLineLis extends MouseAdapter{
        @Override
        public void mouseClicked(MouseEvent mev){
            if(map==null){
                return;
            }
           Line l = (Line) mev.getSource();
            c1 = l.getC1();
            c2 = l.getC2();

            c1.setSelected(true);
            c2.setSelected(true);

            //Change edge
            final String regexLetters = "^[a-zA-ZåöäåÖÅÄ]+$";
            final String text = "Namn: ";
            final String text2 = "Tid: ";

            String time = "";
            String name = "";

            try {
                boolean run = false;
                do {
                    if (null == graph.getEdgeBetween(c1, c2))
                        return;
                    name = graph.getEdgeBetween(c1, c2).getName();
                    time = graph.getEdgeBetween(c1, c2).getWeight().toString();

                    Form form = new Form(name, time, text, text2, true, false);
                    int r = JOptionPane.showConfirmDialog(PathFinderGUI.this, form, ("Ändra förbindelse: " + c1.toString() + " - " + c2.toString()), JOptionPane.YES_NO_OPTION);
                    if (r == JOptionPane.YES_OPTION) {
                        time = form.getTwo();

                        if (time.matches(regexLetters)) {
                            run = true;
                            JOptionPane.showMessageDialog(PathFinderGUI.this, (text2 + " Innehåller inte bara siffror"));
                            Form form1 = new Form(name, time, text, text2);
                        } else
                            run = false;

                    }

                } while (run);

                time = time.trim();
                Integer t = new Integer(time);

                graph.setConnectionWeight(c1, c2, t);

                stateChanged = true; //changes made

            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(PathFinderGUI.this, ("Fel Inmatning: " + nfe.toString()));
            }

            resetSelection();
        }
    }


    private class MouseLis extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent mev) {
            if (map == null)
                return;

            int x = mev.getX();
            int y = mev.getY();

            //Create city
            nCity(x, y);

            map.validate();
            map.repaint();
            map.removeMouseListener(ml);
            map.setCursor(Cursor.getDefaultCursor());
        }
    }//MouseLis

    //Handle close of program.
    private class CloseLis extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent wev) {
            close();
        }
    }//CloseLis


    private class SaveLis implements ActionListener {
        public void actionPerformed(ActionEvent ave) {
            if (stateChanged && currentFile == null) {
                debug("p1: " + "stateChanged" + "currentFile = null");
                save();
            }
            try {
                FileOutputStream fos = new FileOutputStream(currentFile);
                ObjectOutputStream oos = new ObjectOutputStream(fos);

                oos.writeObject(graph);
                oos.writeObject(lines);
                //oos.writeObject(map.getFilename());
                ImageIcon i = map.getImg();
                oos.writeObject(map.getImg());

                oos.close();
                fos.close();

                stateChanged = false; //all changes saved
            } catch (FileNotFoundException fnf) {
                err("Filen kunde inte hittas");
                save();
            } catch (IOException Ioe) {
                err("Något gick fel när filen försöktes sparas");
                System.err.print(Ioe.toString()); //Details for uncommon errors.
            }


        }
    }//SaveLis

    private class OpenLis implements ActionListener {
        @Override
        @SuppressWarnings("unchecked")
        public void actionPerformed(ActionEvent ave) {
            if (stateChanged){
                int response = JOptionPane.showConfirmDialog(PathFinderGUI.this, "Ändringar ha skett vill du spara först?", "Ändrat, spara först?", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    save();
                }
            }

                try {
                    int r = jfcSO.showOpenDialog(PathFinderGUI.this);
                    if (r == JFileChooser.APPROVE_OPTION) {
                        File f = jfcSO.getSelectedFile();
                        String filename = f.getAbsolutePath();
                        currentFile = filename;
                        FileInputStream fis = new FileInputStream(filename);
                        ObjectInputStream ois = new ObjectInputStream(fis);

                        graph = (ListGraph<City>) ois.readObject();
                        lines = (ArrayList<Line>) ois.readObject();

                        if (map != null)
                            remove(map);
                        map = new Map((ImageIcon)ois.readObject());

                        ois.close();
                        fis.close();

                        add(map);

                        Set<City> n = graph.getNodes();
                        for (City c : n) {
                            c.addMouseListener(mlC);
                            map.add(c);

                            Lab l = new Lab(c.getName(), c.getX(), c.getY());
                            map.add(l);
                        }

                        for(Line l : lines){
                            l.addMouseListener(mll);
                            map.add(l);
                        }

                        resetSelection();

                        map.repaint();

                        validate();
                        pack();
                        repaint();


                    }
                } catch (FileNotFoundException fne) {
                    err("Filen kunde inte hittas");
                } catch (ClassNotFoundException cnf) {
                    err("Filen var av fel typ");
                } catch (IOException ioe) {
                    err("Fel vid inläsning av fil");
                    System.err.print(ioe.toString());
                }
        }
    }//OpenLis

    private class NewLis implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ave) {
            if (stateChanged) {
                int r1 = JOptionPane.showConfirmDialog(PathFinderGUI.this, "Ändringar har skett vill du spara först?", "Vill du spara?", JOptionPane.YES_NO_OPTION);
                if (r1 == JOptionPane.YES_OPTION) {
                    save();
                }
            }

            int r = jfcN.showOpenDialog(PathFinderGUI.this);
            if (r == JFileChooser.APPROVE_OPTION) {
                File f = jfcN.getSelectedFile();

                String lname = f.getAbsolutePath();

                if (map != null)
                    remove(map);
                map = new Map(lname);

                add(map, BorderLayout.CENTER);

                validate();
                pack();
                repaint();

                //RESET STUFF
                graph = new ListGraph<City>();
                if (c1 != null)
                    c1.setSelected(false);
                if (c2 != null)
                    c2.setSelected(false);
                c1 = c2 = null;


                stateChanged = false; //no changes made in just opened state
            }


        }
    }//NewLis

    private class findLis implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (map == null)
                return;

            //Hitta väg
            String t = "";
            if (c1 != null && c2 != null && c1 != c2) {
                if (GraphMethods.pathExists(c1, c2, graph)) {
                    t = t + c1.toString() + " - " + c2.toString() + "\n";
                    t = t + GraphMethods.getPath(c1, c2, graph).toString();
                } else {
                    t = t + "Det finns ingen möjlig väg mellan noderna";
                }
            } else {
                return;
            }

            Integer total = 0;
            for(Edge edge: GraphMethods.getPath(c1,c2,graph)){
                total = total + edge.getWeight();
            }
            t = t + "\n Total tid:  " + total.toString();

            TextOut to = new TextOut(t);
            JOptionPane.showMessageDialog(PathFinderGUI.this, to, "Hitta väg", JOptionPane.INFORMATION_MESSAGE);
            resetSelection();
        }
    }//findLis

    private class showLis implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (map == null)
                return;

            final String text = "Namn";
            final String text2 = "Tid: ";
            if (c1 != null && c2 != null) {
                if (null != graph.getEdgeBetween(c1, c2)) {
                    String name = graph.getEdgeBetween(c1, c2).getName();
                    String time = graph.getEdgeBetween(c1, c2).getWeight().toString();

                    Form form = new Form(name, time, text, text2, true);

                    JOptionPane.showMessageDialog(PathFinderGUI.this, form, "Förbindelse " + c1.toString() + " - " + c2.toString(), JOptionPane.INFORMATION_MESSAGE);

                    resetSelection();
                }
            } else {
                JOptionPane.showMessageDialog(PathFinderGUI.this, ("Det finns ingen direkt förbindelse mellan noderna"));
            }
        }
    }//showLis

    private class newPlaceLis implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (map == null)
                return;

            map.addMouseListener(ml);
            map.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        }
    }//newPlaceLis

    private class newEdgeLis implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (map == null)
                return;

            if (c1 != null && c2 != null) {
                if(graph.getEdgeBetween(c1,c2) != null){
                    JOptionPane.showMessageDialog(PathFinderGUI.this, "Det finns redan en koppling!");
                    return;
                }

                //New connection
                final String regexLetters = "^[a-zA-ZåöäåÖÅÄ]+$";
                final String text = "Namn: ";
                final String text2 = "Tid: ";

                String name = "";
                String time = "";

                try {
                    boolean run = false;
                    do {
                        Form form = new Form(name, time, text, text2);
                        int r = JOptionPane.showConfirmDialog(PathFinderGUI.this, form, ("Ny förbindelse: " + c1.toString() + " - " + c2.toString()), JOptionPane.YES_NO_OPTION);
                        if (r != JOptionPane.YES_OPTION)
                            return; //Simplyfies logic

                        name = form.getOne();
                        time = form.getTwo();

                        if (!name.matches(regexLetters)) {
                            run = true;
                            JOptionPane.showMessageDialog(PathFinderGUI.this, (text + " Innehåller inte bara bokstäver"));

                        } else if (time.matches(regexLetters)) {
                            run = true;
                            JOptionPane.showMessageDialog(PathFinderGUI.this, (text2 + " Innehåller inte bara siffror"));

                        } else if(0 > new Integer(time)){
                            run = true;
                            JOptionPane.showMessageDialog(PathFinderGUI.this, (text2 + " Negativ tid"));

                        }else {
                            run = false;
                        }
                    } while (run);

                    time = time.trim();
                    Integer t = new Integer(time);
                    graph.connect(c1, c2, name, t);

                    Line l = new Line(c1.getX(), c1.getY(), c2.getX(), c2.getY(), c1, c2);
                    l.addMouseListener(mll);
                    lines.add(l);
                    map.add(l);

                    map.repaint();

                    stateChanged = true; //changes made

                } catch (NumberFormatException nfe) {
                    err("Fel vid nummerinmantning");
                    System.err.print(nfe.toString());
                }catch (IllegalStateException ise){
                    err("Det finns redan en koppling!");
                }

                //End new connection

                resetSelection();
            } else {
                JOptionPane.showMessageDialog(PathFinderGUI.this, "Välj två städer!");
            }

        }
    }//newEdgeLis

    private class changeEdgeLis implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (map == null)
                return;

            if(c1 == null || c2 == null)
                return;

            //Change edge
            final String regexLetters = "^[a-zA-ZåöäåÖÅÄ]+$";
            final String text = "Namn: ";
            final String text2 = "Tid: ";

            String time = "";
            String name = "";

            try {
                boolean run = false;
                do {
                    if (null == graph.getEdgeBetween(c1, c2))
                        return;
                    name = graph.getEdgeBetween(c1, c2).getName();
                    time = graph.getEdgeBetween(c1, c2).getWeight().toString();

                    Form form = new Form(name, time, text, text2, true, false);
                    int r = JOptionPane.showConfirmDialog(PathFinderGUI.this, form, ("Ändra förbindelse: " + c1.toString() + " - " + c2.toString()), JOptionPane.YES_NO_OPTION);
                    if (r == JOptionPane.YES_OPTION) {
                        time = form.getTwo();

                        if (time.matches(regexLetters)) {
                            run = true;
                            JOptionPane.showMessageDialog(PathFinderGUI.this, (text2 + " Innehåller inte bara siffror"));
                            Form form1 = new Form(name, time, text, text2);
                        } else
                            run = false;

                    }

                } while (run);

                time = time.trim();
                Integer t = new Integer(time);

                graph.setConnectionWeight(c1, c2, t);

                stateChanged = true; //changes made
                resetSelection();
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(PathFinderGUI.this, ("Fel Inmatning: " + nfe.toString()));
            }


        }
    }


    private class Lab extends JComponent {
        String s;
        int x;
        int y;

        Lab(String s, int x, int y) {
            this.s = s;
            this.x = x;
            this.y = y;
            setBounds(x, y, 200, 200);
            setLocation(x, y);
            setVisible(true);

            setMinimumSize(new Dimension(200, 200));
            setPreferredSize(new Dimension(200, 200));

        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(Color.BLUE);
            g2.setFont(new Font("TimesRoman", Font.BOLD, 15));
            g2.drawString(s, 15, 15);

            super.paintComponent(g);
        }

    }

    private class Map extends JPanel implements Serializable {
        private static final long serialVersionUID = -8415292115466373066L;
        private ImageIcon background;
        private String filename;

        public Map() {
            setLayout(null);
            background = new ImageIcon("europa1.gif");
            int h = background.getIconHeight();
            int w = background.getIconWidth();

            setPreferredSize(new Dimension(w, h));
            setMaximumSize(new Dimension(w, h));
            setMinimumSize(new Dimension(w, h));
        }

        public Map(String filename) {
            setLayout(null);
            this.filename = filename;
            background = new ImageIcon(filename);
            int h = background.getIconHeight();
            int w = background.getIconWidth();

            setPreferredSize(new Dimension(w, h));
            setMaximumSize(new Dimension(w, h));
            setMinimumSize(new Dimension(w, h));

            //lcomp.setPreferredSize(new Dimension(w, h));
        }


        public Map(ImageIcon img) {
            setLayout(null);
            background = img;
            int h = background.getIconHeight();
            int w = background.getIconWidth();

            setPreferredSize(new Dimension(w, h));
            setMaximumSize(new Dimension(w, h));
            setMinimumSize(new Dimension(w, h));

           // lcomp.setPreferredSize(new Dimension(w, h));
        }

        private ImageIcon getImg() {
            return background;
        }

        public void changeImg(String filename) {
            this.filename = filename;
            background = new ImageIcon(filename);
            int h = background.getIconHeight();
            int w = background.getIconWidth();

            setPreferredSize(new Dimension(w, h));
            setMaximumSize(new Dimension(w, h));
            setMinimumSize(new Dimension(w, h));

           // lcomp.setPreferredSize(new Dimension(w, h));

            pack();
            validate();
            repaint();
        }

        public String getFilename() {
            return filename;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(background.getImage(), 0, 0, this);

            for(Line l:lines){
                l.paintComponent(g);
            }

        }

    }

    private class Form extends JPanel {

        JTextField one;
        JTextField two;

        public Form(String initialValue1, String text1) {
            one = new JTextField(initialValue1, 10);

            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            JPanel row1 = new JPanel();
            row1.add(new JLabel(text1));
            row1.add(one);
            add(row1);

            JPanel row4 = new JPanel();
            add(row4);
            row4.add(new JLabel("Spara?"));
        }

        public Form(String initialValue1, String initialValue2, String text1, String text2) {
            one = new JTextField(initialValue1, 10);
            two = new JTextField(initialValue2, 5);

            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            JPanel row1 = new JPanel();
            row1.add(new JLabel(text1));

            row1.add(one);
            add(row1);

            JPanel row2 = new JPanel();
            row2.add(new JLabel(text2));
            row2.add(two);
            add(row2);

            JPanel row3 = new JPanel();
            add(row3);
            row3.add(new JLabel("Spara?"));
        }

        public Form(String initialValue1, String initialValue2, String text1, String text2, boolean locked) {
            one = new JTextField(initialValue1, 10);
            two = new JTextField(initialValue2, 5);

            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            JPanel row1 = new JPanel();
            row1.add(new JLabel(text1));
            row1.add(one);
            add(row1);

            JPanel row2 = new JPanel();
            row2.add(new JLabel(text2));
            row2.add(two);
            add(row2);

            if (locked) {
                one.setEditable(!locked);
                two.setEditable(!locked);
            } else {
                JPanel row3 = new JPanel();
                add(row3);
                row3.add(new JLabel("Spara?"));
            }
        }

        public Form(String initialValue1, String initialValue2, String text1, String text2, boolean locked1, boolean locked2) {
            one = new JTextField(initialValue1, 10);
            two = new JTextField(initialValue2, 5);

            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            JPanel row1 = new JPanel();
            row1.add(new JLabel(text1));
            row1.add(one);
            add(row1);

            JPanel row2 = new JPanel();
            row2.add(new JLabel(text2));
            row2.add(two);
            add(row2);


            one.setEditable(!locked1);
            two.setEditable(!locked2);

            JPanel row3 = new JPanel();
            add(row3);
            row3.add(new JLabel("Spara?"));
        }

        public String getOne() {
            return one.getText();
        }

        public String getTwo() {
            return two.getText();
        }


    }//form

    private class TextOut extends JPanel {

        public TextOut(String t) {
            setLayout(new GridLayout(1, 1));
            JTextArea TArea = new JTextArea();
            JScrollPane sp = new JScrollPane(TArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            add(sp);
            TArea.setText(t);
        }

    }

    private class SaveAsLis implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            save();
        }
    }//SaveAsLis

    private class ExitLis implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close();
        }
    }
}//Class


