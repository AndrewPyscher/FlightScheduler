import java.sql.SQLException;
import java.util.ArrayList;

// plane class that represents 1 of the 3 planes in the day class
// each plane has a vip and luxury section, each of those has a maximum number which is from the database

public class Plane {
    String plane_ID;
    ArrayList<Passenger> vip;
    ArrayList<Passenger> luxury;
    int maxVip;
    int maxLuxury;
    int TUID;
    AllDAO allDao;
    int fees[];

    public Plane(int TUID, String plane_ID, int maxVip, int maxLuxury) throws SQLException {
        this.TUID = TUID;
        this.plane_ID = plane_ID;
        this.vip = new ArrayList<>();
        this.luxury = new ArrayList<>();
        this.maxVip = maxVip;
        this.maxLuxury = maxLuxury;
        allDao = new AllDAO();
        fees = allDao.getFees();
    }

    // method to get the total for each plane,
    // it adds up the vip section, then the luxury section
    // it checks the passengers seat type in luxury because someone in there could be vip
    public int getMoney(){
        int total = 0;
        for(int i=0; i<vip.size(); i++){
            total += fees[0];
        }
        for(int i=0; i<luxury.size(); i++){
            if(this.luxury.get(i).seatType.equals("V"))
                total += fees[0];
            if(this.luxury.get(i).seatType.equals("L"))
                total += fees[1];
        }
        return total;
    }
    // insert everyone in the plane to the database.
    // i do it this way because it does it in an order that makes it easier to reseat people
    // when getting them from the database.
    public void insert(String date) throws SQLException, ClassNotFoundException {
        for(int i=0; i<this.vip.size(); i++){
            allDao.addSeatRecord(vip.get(i).customerTUID, String.valueOf(this.TUID),this.vip.get(i).seatType, date ,"V", i+1);
        }
        for(int i=0; i<this.luxury.size(); i++){
            allDao.addSeatRecord(luxury.get(i).customerTUID, String.valueOf(this.TUID),this.luxury.get(i).seatType, date ,"L", i+1);
        }
    }
}