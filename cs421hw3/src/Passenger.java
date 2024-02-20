// Class that's used to store a passenger
// each passenger has a TUID(customerTUID), a requestedPlane(plane), a requestedSeat(seatType), and a requestedFlightDate(date)
public class Passenger{
    int customerTUID;
    String plane;
    String seatType;
    String date;

    public Passenger(int customerTUID, String plane, String seatType, String date) {
        this.customerTUID = customerTUID;
        this.plane = plane;
        this.seatType = seatType;
        this.date = date;
    }
}