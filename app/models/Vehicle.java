package models;

import io.ebean.Model;
import javax.persistence.*;
import java.util.List;

// Vehicle super class
@MappedSuperclass
public abstract class Vehicle extends Model{
    @Id
    private String plateNumber;
    private String make;
    private String model;

    public Vehicle(String plateNumber, String make, String model) {
        this.plateNumber = plateNumber;
        this.make = make;
        this.model = model;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
