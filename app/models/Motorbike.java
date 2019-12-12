package models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.ebean.Finder;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class Motorbike extends Vehicle {
    private boolean isWithABS;
    private int noOfGears;

    @OneToMany(cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Schedule> reservations;

    public static Finder<String, Motorbike> find = new Finder<String, Motorbike>(Motorbike.class);

    public Motorbike(String plateNumber, String make, String model, boolean isWithABS, int noOfGears) {
        super(plateNumber, make, model);
        this.isWithABS = isWithABS;
        this.noOfGears = noOfGears;
    }

    public boolean isWithABS() {
        return isWithABS;
    }

    public void setWithABS(boolean withABS) {
        isWithABS = withABS;
    }

    public int getNoOfGears() {
        return noOfGears;
    }

    public void setNoOfGears(int noOfGears) {
        this.noOfGears = noOfGears;
    }

    public List<Schedule> getReservations() {
        return reservations;
    }

    public void setReservations(List<Schedule> reservations) {
        this.reservations = reservations;
    }
}
