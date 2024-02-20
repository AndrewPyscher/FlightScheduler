import java.sql.*;
import java.util.ArrayList;

public class AllDAO {

    // get connection to access database
    private static Connection getConnection() throws  SQLException {
        Connection connection;
        connection = DriverManager.getConnection ("jdbc:sqlite:Plane.db");
        return connection;
    }
    // delete the tables in the database
    public void dropDatabase() throws SQLException, ClassNotFoundException {
        Connection connection = getConnection();
        if(connection == null)
            return;

        PreparedStatement ps = connection.prepareStatement("DROP TABLE IF EXISTS Fee_Table; ");
        ps.executeUpdate();
        ps = connection.prepareStatement("DROP TABLE IF EXISTS Flight_Table;");
        ps.executeUpdate();
        ps = connection.prepareStatement("DROP TABLE IF EXISTS Passenger_Table;");
        ps.executeUpdate();
        ps = connection.prepareStatement("DROP TABLE IF EXISTS Plane_Table;");
        ps.executeUpdate();
        ps = connection.prepareStatement("DROP TABLE IF EXISTS Schedule_Table;");
        ps.executeUpdate();
        connection.close();
    }

    // read all the tables in the database
    public void buildDatabase() throws SQLException {
        Connection connection = getConnection();
        ResultSet rs;
        PreparedStatement ps;

        ps = connection.prepareStatement("CREATE TABLE Fee_Table (TUID INTEGER NOT NULL, Seat_Type TEXT NOT NULL, Fee INTEGER NOT NULL, PRIMARY KEY(TUID));");
        ps.executeUpdate();
        ps = connection.prepareStatement("CREATE TABLE Schedule_Table (TUID INTEGER NOT NULL, Passenger_TUID INTEGER NOT NULL, Flight_TUID TEXT NOT NULL, Requested_Section TEXT NOT NULL, Flight_Date TEXT NOT NULL, Seated_Section TEXT, Seat_Num INTEGER, FOREIGN KEY(Requested_Section) REFERENCES Fee_Table(Seat_Type), FOREIGN KEY(Passenger_TUID) REFERENCES Passenger_Table(TUID), PRIMARY KEY(TUID AUTOINCREMENT), FOREIGN KEY(Flight_TUID) REFERENCES Flight_Table(TUID));");
        ps.executeUpdate();
        ps= connection.prepareStatement("CREATE TABLE Plane_Table (TUID INTEGER NOT NULL, Plane_ID TEXT NOT NULL, Max_VIP INTEGER NOT NULL, Max_Luxury INTEGER NOT NULL, FOREIGN KEY(TUID) REFERENCES Flight_Table(Plane_ID), PRIMARY KEY(TUID));");
        ps.executeUpdate();
        ps = connection.prepareStatement("CREATE TABLE Passenger_Table (TUID INTEGER NOT NULL, First_Initial TEXT NOT NULL, Middle_Initial TEXT, Last_Name TEXT NOT NULL, Phone_Number TEXT NOT NULL, PRIMARY KEY(TUID));");
        ps.executeUpdate();
        ps = connection.prepareStatement("CREATE TABLE Flight_Table (TUID INTEGER NOT NULL, Plane_ID INTEGER NOT NULL, Airport_Code TEXT NOT NULL,Departure_Gate INTEGER NOT NULL, Depart_Time TEXT NOT NULL, PRIMARY KEY(TUID));");
        ps.executeUpdate();

        ps = connection.prepareStatement("INSERT INTO Plane_Table (TUID, Plane_ID, Max_VIP, Max_Luxury) VALUES (1, 'RC407', 4, 6);");
        ps.executeUpdate();
        ps = connection.prepareStatement("INSERT INTO Plane_Table (TUID, Plane_ID, Max_VIP, Max_Luxury) VALUES (2, 'TR707', 3, 5);");
        ps.executeUpdate();
        ps = connection.prepareStatement("INSERT INTO Plane_Table (TUID, Plane_ID, Max_VIP, Max_Luxury) VALUES (3, 'KR381', 6, 8);");
        ps.executeUpdate();

        ps = connection.prepareStatement("INSERT INTO Flight_Table (TUID, Plane_ID, Airport_Code, Departure_Gate, Depart_Time) VALUES (1, 1, 'MBS', 3, '07:00')");
        ps.executeUpdate();
        ps = connection.prepareStatement("INSERT INTO Flight_Table (TUID, Plane_ID, Airport_Code, Departure_Gate, Depart_Time) VALUES (2, 2, 'MBS', 1, '13:00')");
        ps.executeUpdate();
        ps = connection.prepareStatement("INSERT INTO Flight_Table (TUID, Plane_ID, Airport_Code, Departure_Gate, Depart_Time) VALUES (3, 3, 'MBS', 2, '21:00')");
        ps.executeUpdate();
        ps = connection.prepareStatement("INSERT INTO Fee_Table (TUID, Seat_Type, Fee) VALUES (1, 'V', 4000)");
        ps.executeUpdate();
        ps = connection.prepareStatement("INSERT INTO Fee_Table (TUID, Seat_Type, Fee) VALUES (2, 'L', 2500)");
        ps.executeUpdate();
        connection.close();
    }

    // add the passenger information to the database
    // the data for this comes from the readFile() method
    public void addPassenger(int TUID, String first, String middle, String last, String phone) throws SQLException, ClassNotFoundException {
        Connection connection = getConnection();
        if(connection == null)
            return;
        PreparedStatement ps = connection.prepareStatement("INSERT INTO Passenger_Table VALUES(?,?,?,?,?)");
        ps.setInt(1, TUID);
        ps.setString(2,first);
        ps.setString(3,middle);
        ps.setString(4,last);
        ps.setString(5, phone);
        ps.executeUpdate();
        connection.close();
    }
    // add the passenger seating information to the database
    // the data for this comes from the readFile() method
    public synchronized void addSeatRecord(int passTUID, String flightTUID, String requestedSection, String date, String seatedSection, int seatNum) throws SQLException, ClassNotFoundException {
        Connection connection = getConnection();
        if(connection == null)
            return;
        PreparedStatement ps = connection.prepareStatement("INSERT INTO Schedule_Table (Passenger_TUID, Flight_TUID, Requested_Section, Flight_Date, Seated_Section, Seat_Num) VALUES(?,?,?,?,?,?)");
        ps.setInt(1, passTUID);
        ps.setString(2,flightTUID);
        ps.setString(3,requestedSection);
        ps.setString(4,date);
        ps.setString(5, seatedSection);
        ps.setInt(6,seatNum);
        ps.executeUpdate();
        connection.close();
    }
    // get the info regarding the 3 planes
    public ArrayList<Plane> getPlaneInfo() throws SQLException {
        Connection connection = getConnection();
        if(connection == null)
            return null;

        ArrayList<Plane> plane = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM Plane_Table");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Plane temp = new Plane(
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getInt(3),
                    rs.getInt(4)
            );
            plane.add(temp);
        }
        rs.close();
        connection.close();
        return plane;
    }

    // get the existing seating data from the database when the app first launches
    public ArrayList<Passenger> getSchedule() throws SQLException {
        Connection connection = getConnection();
        if(connection == null)
            return null;

        ArrayList<Passenger> p = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM Schedule_Table ORDER BY TUID");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Passenger temp = new Passenger(
                    rs.getInt(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5)
            );
            p.add(temp);
        }
        rs.close();
        connection.close();
        return p;
    }

    // clear the data in the schedule table
    public void clearSchedule() throws SQLException {
        Connection connection = getConnection();
        if(connection == null)
            return;
        PreparedStatement ps = connection.prepareStatement("DELETE FROM Schedule_Table");
        ps.executeUpdate();
        connection.close();
    }

    public int[] getFees() throws SQLException {
        Connection connection = getConnection();
        if(connection == null)
            return null;
        PreparedStatement ps = connection.prepareStatement("SELECT Fee FROM Fee_Table");
        ResultSet rs = ps.executeQuery();
        int i = 0;
        int[] temp = new int[2];
        while (rs.next()){
            temp[i] = rs.getInt(1);
            i++;
        }
        connection.close();
        return temp;
    }
}