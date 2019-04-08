import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class CalendarChart extends JPanel {

    private int weeks, boxHeight, boxWidth;
    private JPanel panel;
    private boolean populated;
    private ArrayList<WeekBox> weekBoxes;
    public CalendarChart(int x, int y, int width, int height){
        super(null);
        setBounds(x, y, width, height);
        init();
    }

    public CalendarChart(Rectangle r){
        super(null);
        setBounds(r);
        init();
    }

    public void panelAdd(Component comp){
        panel.add(comp);
    }

    public void init(){

        /*--------------------Set Drawing Parameters--------------------*/
        weeks = Schedule.numWeeks;
        boxHeight = 100;
        boxWidth = getWidth() / 7;
        populated = false;

        /*--------------------Create JPanel--------------------*/
        panel = new JPanel(null){
            @Override
            public void paintComponent(Graphics g){
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;

                /*--------------------Fill for participants--------------------*/
                for (WeekBox w : weekBoxes){
                    w.draw(g2);
                }

                /*--------------------Draw boxes--------------------*/
                g2.setColor(Color.black);
                for (int i = 0; i < weeks + 1; i++) {
                    g2.drawLine(0, i*boxHeight, getWidth(), i*boxHeight);
                }

                g2.setColor(new Color(200, 200, 200));
                for (int i = 0; i < 8; i++) {
                    g2.drawLine(i*boxWidth, 0, i * boxWidth, getHeight());
                }

                //repaint all
                Main.panel.repaint();
            }
        };
        panel.setBounds(0, 0, getWidth(), weeks * boxHeight);
        panel.setBackground(Color.white);
        add(panel);

        /*--------------------Add Scrolling--------------------*/
        CustomScroll scroll = new CustomScroll(panel, getWidth()-50, 0, 50, 100, getHeight());
        add(scroll);
        setComponentZOrder(scroll, 0);
        setComponentZOrder(panel, 1);
        System.out.println("Panel Z: " + getComponentZOrder(panel) + "; Scroll Z: " + getComponentZOrder(scroll));

        /*--------------------Add Week Box Data Structures--------------------*/
        weekBoxes = new ArrayList<>();
        for (int i = 0; i < weeks; i++) {
            weekBoxes.add(new WeekBox(i, new Rectangle(0, i*boxHeight, getWidth(), boxHeight)));
        }
    }

    public void activateParticipant(Participant participant){
        clearAll();
        participant = Schedule.participantHashMap.get(participant.getName()); //clear up reference issues
        for (int i = 0; i < weekBoxes.size(); i++) {
            WeekBox weekBox = weekBoxes.get(i);
            weekBox.setAssigned(participant.isAssigned(i));
            weekBox.setConflicted(participant.hasConflict(i));
            weekBox.setRequested(participant.hasRequested(i));
        }

        repaint();
    }

    public void populate(Schedule schedule){

    }

    private void clearAll(){
        for (WeekBox w : weekBoxes){
            w.clear();
        }
    }

    public ArrayList<WeekBox> getWeekBoxes() {
        return weekBoxes;
    }
}
