package planner.terminal;

import planner.app.AuthenticationException;
import planner.app.OperationNotAllowedException;
import planner.app.Planner;
import planner.domain.Activity;
import planner.domain.Project;

import java.util.Calendar;
import java.util.Scanner;

public class Terminal {

    //Terminal terminal;
    Planner planner = new Planner();
    Scanner input = new Scanner(System.in);

    String month = "";
    String day = "";
    String hour = "";
    String minute = "";

    public static void main(String[] args){
        Terminal terminal = new Terminal();
        terminal.startPrompt();
    }

    public void startPrompt() {
        System.out.println("Velkommen til SoftwareHuset A/S. Vælg venligst en funktion:");
        System.out.println("Log ind");
        logIn();
    }

    private void registerUser() {
        System.out.println("Hvad skal dit brugernavn være?");
        String username = input.next();
        System.out.println("Hvad skal dit password være?");
        String password = input.next();
        try {
            planner.createUser(username,password);
        } catch (OperationNotAllowedException | AuthenticationException e) {
            System.out.println(e.getMessage());
            registerUser();
        }
        startPrompt();
    }

    //TODO: Hvad er det vi har testet for i forhold til LOGIN ?
    public void logIn() {
        System.out.println("Indtast brugernavn:");
        String username = input.next();
        System.out.println("Indtast password:");
        String password = input.next();
        try {
            planner.userLogIn(username,password);
            
            if(planner.activeUser.isAdmin()){
                adminFeatureScreen();
            } else {
                userFeatureScreen();
            }

        } catch (OperationNotAllowedException | AuthenticationException e) {
            System.out.println(e.getMessage());
            startPrompt();
        }


    }

    private void adminFeatureScreen(){
        System.out.println("Indtast et nummer:");
        System.out.println("1: Registrer bruger");
        String in = input.next();

        if (in.equals("1")){
            registerUser();
        }

    }

    private void userFeatureScreen() {
        System.out.println("Indtast et nummer:");
        System.out.println("1: Registrer tid");
        System.out.println("2: Opret projekt");
        System.out.println("3: Opret aktivitet");
        System.out.println("4: Log ud");
        String in = input.next();

        if (in.equals("1")){
            registerTime();
        } else if (in.equals("2")) {
            createProject();
        } else if (in.equals("3")) {
            createActivity();
        } else if (in.equals("4")) {
            try {
                planner.userLogOut();
                startPrompt();
            } catch (OperationNotAllowedException e) {
                System.out.println(e.getMessage());
                userFeatureScreen();
            }
        } else {
            System.out.println("Ikke godt nok");
            userFeatureScreen();
        }
    }

    private void createActivity() {
        System.out.println("-1 for at gå tilbage");
        System.out.println("Opretter aktivitet...");
        System.out.println("Indtast titel:");
        String titel = input.next();
        if (titel.equals("-1")) {
            userFeatureScreen();
        }
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        System.out.println("Kendes start tidspunkt? Ja: 1, Nej: 2");
        String x = input.next();
        if (x.equals("1")){
            setMonthAndDay();
            start.set(2018,Integer.parseInt(month),Integer.parseInt(day));
            
        } else  if (x.equals("2")) {
            try {
                planner.getActiveUser().addActivity(new Activity(start, end, titel));
            } catch (OperationNotAllowedException e) {
                System.out.println(e.getMessage());
                createActivity();
            }
        } else {
            System.out.println("Forkerte information - prøv igen");
            createActivity();
        }
    }

    private void setMonthAndDay() {
        System.out.println("Indtast måned:");
        //System.out.println("Indtast -1 for at gå tilbage");
        System.out.println(" 0: Januar \n 1: Februar \n 2: Marts \n 3: April \n 4: Maj \n 5: Juni \n 6: Juli \n" +
                " 7: August \n 8: September \n 9: Oktober \n 10: November \n 11: December");
        month = input.next();

        if (month.matches("0|1|2|3|4|5|6|7|8|9|10|11")) {
            System.out.println("Hvilken dag i måneden?");
            System.out.println("1 - 31");
            day = input.next();
            if (!day.matches("0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31")) {
                System.out.println("Prøv igen");
                setMonthAndDay();
            }
        } else if (month.equals("-1")) {
            createActivity();
        } else {
            System.out.println("Prøv igen");
            setMonthAndDay();
        }

    }

    private void createProject() {
        System.out.println("-1 for at gå tilbage");
        System.out.println("Opretter projekt...");
        System.out.println("Indtast titel:");
        String titel = input.next();
        if (titel.equals("-1")) {
            userFeatureScreen();
        }
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        System.out.println("Kendes start tidspunkt? Ja: 1, Nej: 2");
        String x = input.next();
        if (x.equals("1")){
            setMonthAndDay();
            start.set(2018,Integer.parseInt(month),Integer.parseInt(day));
        } else if (x.equals("2")) {
            try {
                planner.createProject(new Project(titel,start,end));
            } catch (OperationNotAllowedException | AuthenticationException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Forkerte information - prøv igen");
            createProject();
        }
        userFeatureScreen();
    }

    private void registerTime() {
        System.out.println("-1 for at gå tilbage");
        System.out.println("Registrer tid på en aktivitet. Indtast et titel på aktiviteten:");
        for (Activity activity : planner.getActiveUser().getActivities()) {
            System.out.println(activity.getID());
        }
        String ID = input.next();
        if (ID.equals("-1")) {
            userFeatureScreen();
        }
        Activity activity;
        try {
            activity = planner.getActivity(ID);
            registerTimeStepTwo(activity);

        } catch (OperationNotAllowedException e) {
            System.out.println(e.getMessage());
            registerTime();
        }
    }

    private void registerTimeStepTwo(Activity activity) {
        System.out.println("Registrerer tid til aktiviteten " + activity.getID());
        System.out.println("Hvornår startede du?");
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        setMonthAndDay();
        start.set(2018,Integer.parseInt(month),Integer.parseInt(day),Integer.parseInt(hour),Integer.parseInt(minute));
        System.out.println("Hvornår sluttede du aktiviteten?");
        setMonthAndDay();
        setHourAndMinute();
        end.set(2018,Integer.parseInt(month),Integer.parseInt(day),Integer.parseInt(hour),Integer.parseInt(minute));
        try {
            planner.getActiveUser().registerTime(activity, start, end, planner.getActiveUser());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            
        }
        userFeatureScreen();
    }

    public void setHourAndMinute(){
        System.out.println("Indtast time på dagen (0-23:");
        System.out.println("Indtast -1 for at gå tilbage");
        hour = input.next();

        if (hour.equals("-1")) {
            createActivity();
        } else if (hour.matches("0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|19|20|21|22|23")) {
            System.out.println("Hvilket minut i timen?");
            System.out.println("0 - 59");
            day = input.next();
            if (!day.matches("0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31|32|33|34|35|36|37|38|39|40|41|42|43|44|45|46|47|48|49|50|51|52|53|54|55|56|57|58|59")) {
                System.out.println("Prøv igen");
                setHourAndMinute();
            }
        } else {
            System.out.println("Prøv igen");
            setHourAndMinute();
        }
    }

}
