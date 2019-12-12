package controllers;

import play.mvc.Result;

// Rental Vehicle manager interface
public interface RentalVehicleManager {

    public Result addVehicle();
    public Result deleteVehicle(String plateNo);
    public Result listAll();
    public Result reserveVehicle();
    public Result isAvailable(String plateNo, String pickUpDay, String pickUpMonth, String pickUpYear, String dropOffDay, String dropOffMonth, String dropOffYear);
    public Result isAvailableBool(String plateNo, String pickUpDay, String pickUpMonth, String pickUpYear, String dropOffDay, String dropOffMonth, String dropOffYear);
}
