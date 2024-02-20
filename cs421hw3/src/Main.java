import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    // dao (Database Access Object) for all database interactions
    static AllDAO allDao;
    // arraylist of every day when a flight is scheduled with a passenger
    static ArrayList<Day> days;
    // arraylist used to store all the schedule information for a passenger
    // used for swapping passengers (Vip kicks off luxury) and seating passengers when loaded from the database
    static ArrayList<Passenger> passengers;
    // variable used to identify which day is being accessed in the 'days' arraylist
    // global because it's used in multiple methods at the same time
    static int index = 0;
    // total is the total amount of money across all days and all planes
    static int total;
    public static void main(String[] args) throws SQLException, ClassNotFoundException, FileNotFoundException, ParseException {
        total = 0;
        // access the database
        allDao = new AllDAO();
        // create the arraylist to store days and add the first day (always 12/01/2023)
        days = new ArrayList<>();
        Day day = new Day("12/01/2023", 0);
        days.add(day);
        // get the existing passengers scheduled in the database
        passengers = allDao.getSchedule();
        // seat the existing passenger in memory
        for (int i = 0; i < passengers.size(); i++)
            placement(passengers.get(i));
        // a method that constantly runs while the app is running
        running();

    }

    // a method that constantly runs while the app is running
    // waits for user input and performs one of the following options:
    //Read new file, reset the database, generate a report, or exit the app
    public static void running() throws SQLException, FileNotFoundException, ParseException, ClassNotFoundException {
        while(true) {
            // create scanner to listen for user input
            Scanner scanner = new Scanner(System.in);
            System.out.println("Use one of the following numbers to proceed:");
            System.out.println("1: Enter a File");
            System.out.println("2: Reset Database");
            System.out.println("3. Show Report");
            System.out.println("4. Exit App");
            int input = Integer.parseInt(scanner.nextLine());
            if (input == 1) {
                // if the user wants to enter a file, have them enter the absolute path so there's no confusion
                System.out.println("Enter the path to your file:");
                // method that reads in the file and seats passengers
                String in = scanner.nextLine();
                readFile(new File(in));
                // method to clear the schedule table before adding new information to it.
                // this prevents adding a user twice. This could happen if you load a file, add them to the database,
                // then load a new file and a passenger's seat from the previous file is updated.
                allDao.clearSchedule();
                // when the passengers are finished being seated, insert them into the database
                for (int i = 0; i < days.size(); i++) {
                    days.get(i).planeOne.insert(days.get(i).date);
                    days.get(i).planeTwo.insert(days.get(i).date);
                    days.get(i).planeThree.insert(days.get(i).date);
                }
                // when the user wants to reset the database, delete and recreate all the tables
                // I saw no need to delete the whole database, so I just delete the tables.
                // After the tables are recreated, clear the existing passengers in the 'passengers' arraylist
                // and delete all existing days which resets all the info stored in the planes.
                // After that, remake the first day
            } else if (input == 2) {
                allDao.dropDatabase();
                allDao.buildDatabase();
                passengers.clear();
                days.clear();
                Day day = new Day("12/01/2023", 0);
                days.add(day);
            }
            // If the user wants to generate a report, print the seating chart along with the financial information
            else if (input == 3) {
                total =0;
                // loop for each date
                for (int i = 0; i < days.size(); i++) {
                    // print the seating chart
                    days.get(i).print();
                    // get the money totals for each plane on each day
                    total += days.get(i).planeOne.getMoney();
                    total += days.get(i).planeTwo.getMoney();
                    total += days.get(i).planeThree.getMoney();
                }
                System.out.println("Overall Profit: $" + total);
            }
            // when the user wants to exit the ask, ask them if they want to reset the database
            else if(input == 4){
                System.out.println("Do you want to reset the database?");
                System.out.println("1: Yes");
                System.out.println("2. No");
                input = scanner.nextInt();
                if(input == 1){
                    allDao.dropDatabase();
                    allDao.buildDatabase();
                }
                System.exit(0);
            }
        }
    }

    // method that reads in the file provided by the user
    // when the file is read in, separate the passenger information from the scheduling information.
    // this is done by splitting the input line and looking at the first character.
    // if in input is passenger information, add them to the database,
    // if its scheduling information, pass it into the 'placement' method which assigns the passenger a seat
    public static void readFile(File file) throws FileNotFoundException, SQLException, ClassNotFoundException, ParseException {
        Scanner input = new Scanner(file);
        while(input.hasNextLine()){
            // split the input at every space
            String[] temp = input.nextLine().split(" ");
            if(String.valueOf(temp[0]).equals("P")){
                // temp[1] = tuid, temp[2] = first initial, temp[3] = middle initial, temp[4] = last name, and temp[5] = phone number
                allDao.addPassenger(Integer.parseInt(temp[1]), temp[2], temp[3],temp[4], temp[5]);
            }
            else if(String.valueOf(temp[0]).equals("S")){
                // temp [1] = customerTUID, temp[2] = requestedPlane, temp[3] = requestedSeatType, temp[4] = requestedFlightDate
                Passenger p = new Passenger(Integer.parseInt(temp[1]), temp[2], temp[3], temp[4]);
                // add the passenger to the arraylist containing schedule information for all passengers
                passengers.add(p);
                // seat the passenger
                placement(p);
            }
        }
    }
    // method that takes in a passenger. It will first find the date the passenger wants to fly out on. If it doesn't exist
    // it will create the date (and every date in between).It will then find the plane they want to fly out on.
    // plane1 = RC707, plane2 = TR707, plane3 = KR381.
    // if they want to fly out on plane1, and that nothing is available there, it will check plane2, if nothing is available there it will go to plane3.
    // if they want plane2, it will check plane2, and then plane3.
    // if nothing is available for that day, it will create a new day and start at plane 1.
    // it will continue this pattern until they are seated
    public static void placement(Passenger p) throws SQLException, ParseException, ClassNotFoundException {
        // boolean to check if the passenger is seated.
        boolean seated = false;
        // index is the index in the 'days' arraylist
        index = 0;
        // loop to find the date the passenger wants in the 'days' arraylist
        // if it's not there, add it.
        while(!days.get(index).date.equals(p.date)){
            incrementDate();
        }
        // loop that continues until the passenger is seated
        while(!seated) {
            // if the passenger wants plane1 or doesn't care, use this order to seat
            if(p.plane.equals("1") || p.plane.equals("X")){
                seated = findSeat(p.seatType, p, days.get(index).planeOne);
                if (seated) break;
                seated = findSeat(p.seatType, p,days.get(index).planeTwo);
                if (seated) break;
                seated = findSeat(p.seatType, p, days.get(index).planeThree);
                if (seated) break;
                // if they aren't seated that day, increment the date
                incrementDate();
            }
            // if the passenger wants plane2, use this order to seat
            if(p.plane.equals("2")){
                seated = findSeat(p.seatType,  p,days.get(index).planeTwo);
                if (seated) break;
                seated = findSeat(p.seatType,  p,days.get(index).planeThree);
                if (seated) break;
                // if they aren't seated, set the preferred plane to "1" because they will look to be seated on the
                // first available plane the next day.
                p.plane = "1";
                // if they aren't seated that day, increment the date
                incrementDate();
            }
            // if the passenger wants plane3, use this order to seat
            if(p.plane.equals("3")){
                seated = findSeat(p.seatType, p,days.get(index).planeThree);
                if (seated) break;
                // if they aren't seated, set the preferred plane to "1" because they will look to be seated on the
                // first available plane the next day.
                p.plane = "1";
                // if they aren't seated that day, increment the date
                incrementDate();
            }
        }
    }
    // method that takes in the users preferred seat, preferred plane, and the passenger information
    // it will first check if they are a vip.
    // if they are, check if the planes vip section is full, if it's not, add them.
    // if it is full, check the luxury section of that plane, if it is not full add them
    // if the luxury section is full, check to see if a passengers requestSeat is luxury
    // if it is, boot them and reseat them
    // if there is not, check the next plane (return)
    // if all days are full of vips for the day, go to the next day
    // if they request a luxury seat, check if the planes has room in luxury, if there is, add them.
    // if there is not, check the next plane (return)
    public static boolean findSeat(String seat, Passenger p,Plane plane) throws SQLException, ParseException, ClassNotFoundException {
        // check if the passenger is a vip
        if(seat.equals("V")) {
            // check if this is the plane the passenger wants
            // check if the vip section is full
            if (plane.vip.size() != plane.maxVip) {
                // if it's not, add them
                return plane.vip.add(p);
            }// if the vip section is full, check the luxury section
            if (plane.luxury.size() != plane.maxLuxury) {
                // if it's not full, add them
                return plane.luxury.add(p);
            }// if the luxury section is full, check if there is any non-vips.
            for (int i = plane.luxury.size()-1; i >= 0; i--) {
                // if there is a non-vip, boot them and reseat them
                if (plane.luxury.get(i).seatType.equals("L")) {
                    Passenger temp = plane.luxury.get(i);
                    // replace them
                    plane.luxury.set(i, p);
                    // reseat them
                    placement(temp);
                    return true;
                }
            }
        }
        /// LUXURY
        // if a passenger requests luxury, check if they can be added
            if (plane.luxury.size() != plane.maxLuxury) {
               return plane.luxury.add(p);
            }
            // if they can't be added return false, which will advance the algorithm to the next plane/day
        return false;
    }

    // method to increment the index for the date. The dates are in order, so if the date at the current
    // index is equal to the date at the last index, add a new date.
    // Then increment the index
    public static void incrementDate() throws SQLException, ParseException, ClassNotFoundException {
        if (days.get(index).date.equals(days.get(days.size()-1).date))
            days.add(new Day(days.get(days.size() - 1).date, 1));
        index ++;
    }
}