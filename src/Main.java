import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

public class Main extends JFrame {
    int width = 800, height = 800;
    public static GraphicsPanel panel;
    public static Timer[] timers;
    private static ArrayList<Population> populations, populationStarter;
    private static int numIslands;
    public Main(){
        super("Scheduler");
        setSize(width, height);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        panel = new GraphicsPanel(width, height, this);
        add(panel);
        panel.repaint();

    }
    public static void main(String[] args) {
        Schedule.numWeeks = 26;
        numIslands = 5;
        populations = new ArrayList<>();
        populationStarter = new ArrayList<>();
        timers = new Timer[numIslands];
//        int numDocs = 11;
//        ArrayList<Integer> temp = new ArrayList<>();
//        HashMap<Integer, Integer> tempRequiredList = new HashMap<>();
//        HashMap<Integer, Integer> reversedRequiredList = new HashMap<>(); //lazy way of avoiding for loops
//        HashMap<Integer, Participant> requiredList = new HashMap<>();
//        for (int i = 0; i < Schedule.numWeeks; i++) {
//            temp.add(i);
//        }
//        for (int i = 0; i < 4; i++) {
//            int docNum = (int)(Math.random() * numDocs), weekNum = temp.remove((int)(Math.random() * temp.size()));
//            tempRequiredList.put(docNum, weekNum);
//            reversedRequiredList.put(weekNum, docNum);
//        }
//        for (int i = 0; i < numDocs; i++) {
//            ArrayList<Integer> constraints = new ArrayList<>();
//            ArrayList<Integer> poss = new ArrayList<>();
//            for (int j = 0; j < Schedule.numWeeks; j++) {
//                if(reversedRequiredList.get(j) == null)
//                    poss.add(j);
//            }
//            for (int j = 0; j < 15; j++) {
//                constraints.add(poss.remove((int)(Math.random() * poss.size())));
//            }
//            Participant p = new Participant("Doctor" + i, constraints);
//            if(tempRequiredList.get(i)!=null)
//                requiredList.put(tempRequiredList.get(i), p);
//        }
//        Schedule.requiredDocs = requiredList;
//        Schedule.avgNumCalls = Schedule.numWeeks/Schedule.participantHashMap.keySet().size();
//        schedules = new Population(200);
//        System.out.print("Required Dates: ");
//        for(Integer i : tempRequiredList.keySet()){
//            System.out.print("Doctor" + i + ", " + tempRequiredList.get(i) + "; ");
//        }
        new Main();
//        schedules.setPanel(panel);
//        t = new Timer(20, new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                if(schedules.getNoChange() > 500 || schedules.getLastCost() == 200) {
//                    schedules.printBestSchedule();
//                    t.stop();
//                }
//                else
//                    schedules.nextGen();
//            }
//        });
//        t.start();
    }

    public static void startTimer(){

        for (int i = 0; i < numIslands; i++) {
            Population p = new Population(200);
            populations.add(p);
            populationStarter.add(p);

            Timer t = new Timer(20, new ActionListener() {
                Population pop = populationStarter.remove(0);
                final int lowestScore = 100 * (Schedule.numWeeks % Schedule.participantHashMap.size());
                @Override
                public void actionPerformed(ActionEvent e) {

                    if(pop.getNoChange() > 500 || pop.getLastCost() == lowestScore) {
                        pop.printBestSchedule();
                        for (Timer timer : timers) {
                            timer.stop();
                        }
                    }
                    else{
                        if(pop.getGeneration() % 10 == 0){
                            pop.mate(populations.get((int)(Math.random() * populations.size())));
                        }
                        pop.nextGen();
                    }

                }
            });
            timers[i] = t;
            t.start();
        }

        
    }




}
