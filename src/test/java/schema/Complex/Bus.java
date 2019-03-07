package schema.Complex;

public class Bus{
    private Tyre tyre;

    public String getSeatname() {
        return seatname;
    }

    public void setSeatname(String seatname) {
        this.seatname = seatname;
    }

    private String seatname;

    /**
     * @return the tyre
     */
    public Tyre getTyre() {
        return tyre;
    }

    /**
     * @param tyre the tyre to set
     */
    public void setTyre(Tyre tyre) {
        this.tyre = tyre;
    }

}