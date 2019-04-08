import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class Population {

    private ArrayList<Schedule> chromosomes = new ArrayList<>();
    private double elitism, lastCost = (int)Math.pow(2, 30);
    private int size, generation = 0, noChange = 0;
    private GraphicsPanel panel = Main.panel;
    public Population(int size){
        this.size = size;
        for (int i = 0; i < size; i++) {
            chromosomes.add(Schedule.randomSchedule());
        }
        elitism = .2;
    }

    public void sort(){
        Collections.sort(chromosomes, Comparator.comparingInt(Schedule::calcScore));
    }

    public void kill(){
        int target = (int)(elitism * chromosomes.size());
        while(chromosomes.size() > target){
            chromosomes.remove(chromosomes.size() - 1);
        }
    }

    public void fill(){
        while(chromosomes.size() < size){
            Schedule newS;
            newS = chromosomes.get((int)(Math.random() * chromosomes.size())).clone();
            newS.mutate(.3);
            chromosomes.add(newS);
        }
    }

    public void nextGen(){
        sort();
        Schedule best = chromosomes.get(0);
        if(lastCost > best.calcScore()){
            lastCost = best.calcScore();
            noChange = 0;
        }
        else
            noChange++;
        if(generation % 10 == 0) {
            System.out.println("Generation: " + generation + " | " + best.calcScore());
            panel.setGeneration(generation);
            panel.setScore(best.calcScore());
            panel.repaint();
        }
        kill();
        fill();
        generation++;
    }

    public int getNoChange() {
        return noChange;
    }

    public double getLastCost(){
        return lastCost;
    }

    public void printBestSchedule(){
        sort();
        String sched = "", closeCalls = "Close weeks: ", topCallers = "Average calls = " + Schedule.avgNumCalls + " Extra calls: ", conflicts = "Conflicts: ";
        Schedule best = chromosomes.get(0);
        ArrayList<String> schedule = best.getSchedule();
        HashMap<String, Participant> participants = best.getParticipants();
        for(String s : participants.keySet()){
            participants.get(s).reset();
        }
        populateCalendar(best);
        for (int i = 0; i < schedule.size(); i++) {
            Participant scheduled = participants.get(schedule.get(i));
            if(scheduled.hasConflict(i))
                conflicts += "Week " + i + ": " + scheduled.getName() + "; ";
            int weeksPassed = i - scheduled.getLastWeek();
            if(weeksPassed < 4)
                closeCalls += weeksPassed + " passed for " + scheduled.getName() + " – on call on Weeks " + i + " and " + scheduled.getLastWeek() + "; ";
            scheduled.setLastWeek(i);
            scheduled.setNumCalls(scheduled.getNumCalls()+1);

            sched += scheduled.getName() + "; ";
        }
        for(String s : participants.keySet()){
            Participant scheduled = participants.get(s);
            int extraCalls = scheduled.getNumCalls() - Schedule.avgNumCalls;
            if(extraCalls > 0) {
                topCallers += scheduled.getName() + " is on call " +  scheduled.getNumCalls() + " times; ";
            }
            else if(extraCalls < 0){
                System.out.println(scheduled.getName() + " only was on call " + scheduled.getNumCalls() + " times! ");
            }
        }
        System.out.println("Generation: " + generation + " | " + best.calcScore() + "\n");
        System.out.println(sched + "\n");
        System.out.println(conflicts + "\n");
        System.out.println(closeCalls + "\n");
        System.out.println(topCallers + "\n");


    }

    private void populateCalendar(Schedule best){
        ArrayList<WeekBox> weekBoxes = panel.getMainCalendar().getWeekBoxes();
        ArrayList<String> schedule = best.getSchedule();
        for (int i = 0; i < schedule.size(); i++) {
            Schedule.participantHashMap.get(schedule.get(i)).addAssigned(i);
            weekBoxes.get(i).setAssignedName(schedule.get(i));
        }

        panel.repaint();
    }

    public void mate(Population other){
        sort();
        for (int i = 0; i < chromosomes.size()/10; i++) {
            if(Math.random() < .8){
                Schedule temp = chromosomes.get(i);
                chromosomes.set(i, other.getChromosomes().get(i));
                other.getChromosomes().set(i, temp);
            }
        }
    }

    public void setPanel(GraphicsPanel panel) {
        this.panel = panel;
    }

    public ArrayList<Schedule> getChromosomes() {
        return chromosomes;
    }

    public int getGeneration() {
        return generation;
    }
}
