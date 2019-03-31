import javax.swing.*;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class GraphicsPanel extends JPanel {

    private int generation = 0 , score = 100000, w, h;
    private GregorianCalendar calendar;
    private enum State {
        START, ACTIVE
    }
    private State curr_state;
    public GraphicsPanel(int w, int h){
        super(null);
        setSize(w, h);
        this.w = w;
        this.h = h;

        setState(State.START);
    }

    public void setGeneration(int generation) {
        this.generation = generation;
    }

    public void setScore(int score) {
        this.score = score;
    }

    private void addDoctors(int w, int h){
        //doctorsPanel
        JPanel doctorsPanel = new JPanel(null);
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

        //guarantee buttons visible
        doctorsPanel.revalidate();
        doctorsPanel.repaint();
    }

    private void setState(State state){
        removeAll();
        curr_state = state;
        switch(state){
            case START:
                //Start Screen
                buildStartScreen(w, h);
                break;
            case ACTIVE:
                //Active Screen
                buildActiveScreen(w, h);
                break;
        }
        revalidate();
        repaint();
    }

    private void buildStartScreen(int w, int h){
        //add title
        JLabel title = new JLabel("Call Scheduler");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("TimesRoman", Font.BOLD, 76));
        title.setBounds(50, h/2 - 150, w - 100, h/10);
        add(title);

        //add file choosing button
        JFileChooser fileChooser = new JFileChooser();
        JButton fileButton = new JButton("Choose File");
        fileButton.setBounds(w/2 - 100, h/2, 200, 50);
        add(fileButton);
        fileButton.addActionListener(e -> { //choose file functionality
            //filter results to txt or csv only
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Readable Text Files", "txt", "csv");
            fileChooser.setFileFilter(filter);

            //open choosing window
            int returnVal = fileChooser.showOpenDialog(this);
            if(returnVal == JFileChooser.APPROVE_OPTION){
                fileButton.setText(fileChooser.getSelectedFile().getName());
            }
        });

        /*----------------------------Add Week Fields------------------------*/

        ArrayList<JTextField> dateInputs = new ArrayList<>(); //store fields for later access

        //create container element for ease of positioning
        JPanel container = new JPanel();
        container.setLayout(null);
        container.setBounds(w/2 - 150, h/2 + 100, 300, 50);

        //add initial label to container
        JLabel week = new JLabel("Starts the week of: ");
        week.setBounds(10, 10, 150, 25);
        container.add(week);

        //add number input fields
        for (int i = 0; i < 3; i++) {
            //number input
            JTextField input = new JTextField();
            input.setBounds( week.getWidth() + 50 * i, 5, 35, 35);
            container.add(input);
            dateInputs.add(input);

            //separator labels
            if(i < 2){
                JLabel separator = new JLabel("/");
                separator.setFont(new Font("TimesRoman", Font.PLAIN, 36));
                separator.setBounds( week.getWidth() + 50 * (i+1) - 10, 7, 25, 35);
                container.add(separator);
            }

            else{
                Rectangle r = input.getBounds();
                r.width += 15;
                input.setBounds(r);
            }
        }

        //add container
        add(container);

        /*------------------Add Error Label-------------------*/
        JLabel error = new JLabel();
        error.setBounds(h/2 - 200, container.getY() + 75, 400, 50);
        error.setHorizontalAlignment(SwingConstants.CENTER);
        add(error);

        /*------------------Add Go Button---------------------*/
        JButton go = new JButton("Schedule!");
        go.setFont(new Font("TimesRoman", Font.BOLD, 48));
        go.setBounds(h/2 - 200, container.getY() + 150, 400, 100);
        add(go);
        go.addActionListener(e -> {
            error.setText("");
            //input checks
            try{
                //create calendar with given date
                 int[] date = new int[3];
                 for (int i = 0; i < 3; i++) {
                     String text = dateInputs.get(i).getText();
                     if(i == 2 && text.length() < 4){
                         text = "20" + text;
                     }
                    date[i] = Integer.parseInt(text);
                 }
                 calendar = new GregorianCalendar(date[2], date[0] - 1, date[1]);
                 System.out.println(calendar.getTime());

                 //read input file for doctor schedules
                File schedule = fileChooser.getSelectedFile();
                readFile(schedule);

                //Activate algorithm
                Main.startTimer();
                setState(State.ACTIVE);
            }
            catch(Exception exception){
                exception.printStackTrace();
                error.setText("Input is incorrect or incomplete");
            }
        });

    }

    private void buildActiveScreen(int w, int h){
        //add doctor panel
        addDoctors(w, h);

        //add preliminary genAlg display panel
        JPanel genPanel = new JPanel(){
            @Override
            public void paintComponent(Graphics g){
                Graphics2D g2 = (Graphics2D)g;
                g2.drawString("Generation: " + generation, 0, 20);
                g2.drawString("Score: " + score, 0, 40);
            }
        };
        genPanel.setBounds(w - 200, 20, 180, 40);
        add(genPanel);

    }

    private void readFile(File file) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(file));

        HashMap<String, ArrayList<Integer>> doctors = new HashMap<>(); //temp list of doctors and constraints
        String st = br.readLine();//first line is doctor names
        String[] list = st.split(",");
        for(String s : list){doctors.put(s, new ArrayList<>());}

        HashMap<Integer, String> tempRequired = new HashMap<>();

        /*---------------------Loop over Dates------------------*/
        while((st = br.readLine()) != null) {
            String[] dates = st.split(","); //split on values
            for (int i = 0; i < dates.length; i++) {
                String[] value = dates[i].split("-"); //is it a constraint or a request?
                if (value.length > 1) { //constraint
                    doctors.get(list[i]).add(interpretDate(value[1])); //add week integer to doctor constraint arraylist
                } else { //request
                    tempRequired.put(interpretDate(value[0]), list[i]);
                }
            }
        }

        for(String s : doctors.keySet()){
            new Participant(s, doctors.get(s));
        }

        for(Integer i : tempRequired.keySet()){
            Schedule.requiredDocs.put(i, Schedule.participantHashMap.get(tempRequired.get(i)));
        }

        Schedule.avgNumCalls = Schedule.numWeeks/Schedule.participantHashMap.keySet().size();


    }

    private int interpretDate(String date) throws Exception{

        /*-----------------Correct Format---------------*/
        String[] vals = date.split("/");
        for (int i = 0; i < 2; i++) {
            if(vals[i].length() < 2)
                vals[i] = "0"+vals[i];
        }
        if(vals[2].length() == 2)
            vals[2] = "20" + vals[2];

        date = "";
        for (int i = 0; i < 2; i++) {
            date += vals[i] + "/";
        }
        date += vals[2];
        /*---------------Get Week Value---------------------*/
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        Date d1 = sdf.parse(date);
        long diffInMillies = Math.abs(calendar.getTime().getTime() - d1.getTime());
        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        return ((int)diff)/7;
    }

}
