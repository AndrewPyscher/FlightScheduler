import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


// Day class represents a day where flights can be scheduled.
// each day has 3 planes and a date
public class Day {
    Plane planeOne, planeTwo, planeThree;
    String date;


    public Day(String date, int add) throws SQLException, ClassNotFoundException, ParseException {
        AllDAO allDao = new AllDAO();
        ArrayList<Plane> planes = allDao.getPlaneInfo();
        this.planeOne = planes.get(0);
        this.planeTwo = planes.get(1);
        this.planeThree = planes.get(2);
        this.date = setDate(date, add);
    }
    // method to add a specified number of days to a string (ie, (12/01/2023,0)  would make 12/02/2023)
    public String setDate(String date, int add) throws ParseException {
        SimpleDateFormat d = new SimpleDateFormat("MM/dd/yyyy");
        Calendar temp = Calendar.getInstance();
        temp.setTime(d.parse(date));
        temp.add(Calendar.DATE, add);
        return d.format(temp.getTime());
    }

    // a not so fun method which prints out all 3 plane data for each day
    // for each plane it will first print out the seats that exist (by printing until the end of the arraylist),
    // it will then print the remaining days as "e". So if there is 3 people in the RC407 arraylist,
    // it will print the first 3 passenger id's then it will print 1 "e"
    public void print(){
        System.out.println();
        System.out.println("RC 407: " + this.date);
        System.out.println("Total Cost: $" + this.planeOne.getMoney());
        System.out.print("+--------------+");
        for(int i=0; i<planeOne.vip.size(); i++) {
            if(i%2 == 0) {
                System.out.println();
            }
            System.out.printf("| %4s |", this.planeOne.vip.get(i).customerTUID);
        }
        for(int i=planeOne.vip.size(); i<4; i++) {
            if(i%2 == 0) {
                System.out.println();
            }
            System.out.printf("| %4s |", "e");
        }
        System.out.println("\n----------------");
        for(int i=0; i<planeOne.luxury.size(); i++) {
            if(i != 0 && i%2 == 0) {
                System.out.println();
            }
            System.out.printf("| %4s |", this.planeOne.luxury.get(i).customerTUID);
        }
        for(int i=planeOne.luxury.size(); i<6; i++) {
            if(i != 0 && i % 2 == 0) {
                System.out.println();
            }
            System.out.printf("| %4s |", "e");
        }
        System.out.print("\n+--------------+\n");
        System.out.println();
        System.out.println("TR 707: " + this.date);
        System.out.println("Total Cost: $" + this.planeTwo.getMoney());
        System.out.print("+--------------+");
        for(int i=0; i<planeTwo.vip.size(); i++) {
            if(i % 2 == 0) {
                System.out.println();
            }
            if (i == 2) {
                System.out.printf("| %4s |", "X");
            }
            System.out.printf("| %4s |", this.planeTwo.vip.get(i).customerTUID);
        }
        for (int i=planeTwo.vip.size(); i<3; i++) {
            if (i % 2 == 0) {
                System.out.println();
            }
            System.out.printf("| %4s |", "e");
            if(i==1) {
                System.out.printf("\n| %4s |", "X");
                System.out.printf("| %4s |", "e");
                break;
            }
        }
        System.out.println("\n----------------");
        for(int i=0; i<planeTwo.luxury.size(); i++){
            if(i%2== 1)
                System.out.println();
            if(i==0)
                System.out.printf("| %4s |", "X");

            System.out.printf("| %4s |", this.planeTwo.luxury.get(i).customerTUID);
        }
        for (int i=planeTwo.luxury.size(); i<5; i++) {
            if(i==0)
                System.out.printf("| %4s |", "X");
            if (i % 2 == 1) {
                System.out.println();
            }
            System.out.printf("| %4s |", "e");

        }
        System.out.print("\n+--------------+\n");
        System.out.println();
        System.out.println("KR 381: " + this.date);
        System.out.println("Total Cost: $" + this.planeThree.getMoney());
        System.out.print("+--------------+");
        for(int i=0; i<planeThree.vip.size(); i++) {
            if(i%2 == 0) {
                System.out.println();
            }
            System.out.printf("| %4s |", this.planeThree.vip.get(i).customerTUID);
        }
        for(int i=planeThree.vip.size(); i<6; i++) {
            if(i%2 == 0) {
                System.out.println();
            }
            System.out.printf("| %4s |", "e");
        }
        System.out.println("\n----------------");
        for(int i=0; i<planeThree.luxury.size(); i++) {
            if(i != 0 && i%2 == 0) {
                System.out.println();
            }
            System.out.printf("| %4s |", this.planeThree.luxury.get(i).customerTUID);
        }
        for(int i=planeThree.luxury.size(); i<8; i++) {
            if(i != 0 && i % 2 == 0) {
                System.out.println();
            }
            System.out.printf("| %4s |", "e");
        }
        System.out.print("\n+--------------+\n");
    }
}