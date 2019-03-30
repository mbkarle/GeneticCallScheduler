import javax.swing.*;
import java.awt.*;

public class GraphicsPanel extends JPanel {

    private int generation = 0 , score = 100000;
    private JPanel doctorsPanel;
    public GraphicsPanel(int w, int h){
        super(null);
        setSize(w, h);

        //doctorsPanel
        doctorsPanel = new JPanel(null);
        add(doctorsPanel);
        doctorsPanel.setBounds(0, 40, w/5, h - 80);
        doctorsPanel.setVisible(true);
        doctorsPanel.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        doctorsPanel.setBackground(Color.white);

        //Add buttons for each doctor
        int numDocs = Schedule.participantHashMap.keySet().size();
        int count = 0;
        for(String s : Schedule.participantHashMap.keySet()){
            ParticipantButton button = new ParticipantButton(Schedule.participantHashMap.get(s));
            doctorsPanel.add(button);
            int height = (h-100)/numDocs;
            button.setBounds(0, count * height + 10, w/5, height);
            count++;
        }
        doctorsPanel.revalidate();
        doctorsPanel.repaint();
    }

    public void paintComponent(Graphics g){
        Graphics2D g2 = (Graphics2D)g;
        g2.drawString("Generation: " + generation, 0, 20);
        g2.drawString("Score: " + score, 0, 40);
    }

    public void setGeneration(int generation) {
        this.generation = generation;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
