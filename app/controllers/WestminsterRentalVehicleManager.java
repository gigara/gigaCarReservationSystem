package controllers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import models.Car;
import models.Transmission;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Motorbike;
import models.Schedule;
import models.Vehicle;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// westminster car rental  implementation
public class WestminsterRentalVehicleManager extends Controller implements RentalVehicleManager {

    @Override
    public Result addVehicle() {
        ObjectNode response = Json.newObject();
        // get data from the post request
        Map<String, String[]> body = request().body().asFormUrlEncoded();

        try {
            //check database
            if (body != null) {
                if (body.containsKey("plateNo") && isAvailable(body.get("plateNo")[0])) {
                    response.put("error", "Item already available");

                } else if (isExceeding()) {
                    response.put("error", "Size Limit Exceeds");

                } else if (body.containsKey("type")) {
                    if (body.containsKey("type") && body.containsKey("make") && body.containsKey("model")) {
                        String plateNo = body.get("plateNo")[0];
                        String make = body.get("make")[0];
                        String model = body.get("model")[0];

                        if (body.get("type")[0].equals("car") && body.containsKey("noOfSeats") && body.containsKey("withAC") && body.containsKey("transmission")) {
                            int noOfSeats = Integer.parseInt(body.get("noOfSeats")[0]);
                            boolean withAC = Boolean.parseBoolean(body.get("withAC")[0]);
                            Transmission transmission = Transmission.valueOf(body.get("transmission")[0]);

                            Car car = new Car(plateNo, make, model, noOfSeats, withAC, transmission);
                            car.save();
                            response.put("success", "Car Added Successfully");

                        } else if (body.get("type")[0].equals("bike") && body.containsKey("isWithABS") && body.containsKey("noOfGears")) {
                            int noOfGears = Integer.parseInt(body.get("noOfGears")[0]);
                            boolean isWithABS = Boolean.parseBoolean(body.get("isWithABS")[0]);

                            Motorbike motorbike = new Motorbike(plateNo, make, model, isWithABS, noOfGears);
                            motorbike.save();
                            response.put("success", "Motorbike Added Successfully");
                        }
                    }
                } else {
                    response.put("error", "error");
                }
            } else {
                response.put("error", "error");
            }
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ok(response);
        }
        if (response.size() == 0) {
            response.put("error", "error");
        }
        return ok(response);
    }

    //delete item
    //@param plateNo - plate number
    @Override
    public Result deleteVehicle(String plateNo) {
        ObjectNode response = Json.newObject();

        if (Car.find.query().where().eq("plate_number", plateNo).findCount() == 1) {
            Car car = Car.find.byId(plateNo);
            car.delete();

            response.put("status", "Car: " + car.getPlateNumber() + " deleted successfully");

        } else if (Motorbike.find.query().where().eq("plate_number", plateNo).findCount() == 1) {
            Motorbike motorbike = Motorbike.find.byId(plateNo);
            motorbike.delete();

            response.put("status", "MotorBike: " + motorbike.getPlateNumber() + " deleted successfully");

        } else {
            response.put("status", "Item Not Found");
        }

        response.put("Available", availableSpace());
        return ok(response);
    }

    // list all vehicles
    @Override
    public Result listAll() {
        List<Car> cars = Car.find.all();
        List<Motorbike> motorbikes = Motorbike.find.all();

        ArrayNode node1 = ((ArrayNode) Json.toJson(cars));
        ArrayNode node2 = ((ArrayNode) Json.toJson(motorbikes));

        Map<String, ArrayNode> map = new HashMap<>();
        map.put("Cars", node1);
        map.put("Motorbikes", node2);

        return ok(Json.toJson(map));
    }

    // reserve a vehicle
    @Override
    public Result reserveVehicle() {
        ObjectNode response = Json.newObject();

        // get data from the post request
        Map<String, String[]> body = request().body().asFormUrlEncoded();

        try {
            String plateNo = body.get("plateNo")[0];

            int pickUpDay = Integer.parseInt(body.get("pickUpDate")[0]);
            int pickUpMonth = Integer.parseInt(body.get("pickUpMonth")[0]);
            int pickUpYear = Integer.parseInt(body.get("pickUpYear")[0]);
            int dropOffDay = Integer.parseInt(body.get("dropOffDate")[0]);
            int dropOffMonth = Integer.parseInt(body.get("dropOffMonth")[0]);
            int dropOffYear = Integer.parseInt(body.get("dropOffYear")[0]);

            LocalDate pickUpDate = LocalDate.of(pickUpYear, pickUpMonth, pickUpDay);
            LocalDate dropOffDate = LocalDate.of(dropOffYear, dropOffMonth, dropOffDay);

            Schedule schedule = new Schedule(pickUpDate, dropOffDate);

            // Car
            if (Car.find.query().where().eq("plate_number", plateNo).findCount() == 1) {

                Car car = Car.find.byId(plateNo);

                if (isReserved(car, pickUpDate, dropOffDate)) {
                    response.put("status", "Car has been already reserved by someone.");

                } else {
                    car.getReservations().add(schedule);
                    car.update();

                    response.put("status", "Car: " + car.getPlateNumber() + " has been successfully marked as reserved ");
                }

                //Motorbike
            } else if (Motorbike.find.query().where().eq("plate_number", plateNo).findCount() == 1) {

                Motorbike motorbike = Motorbike.find.byId(plateNo);

                if (isReserved(motorbike, pickUpDate, dropOffDate)) {
                    response.put("status", "Motorbike has been already reserved by someone.");

                } else {
                    motorbike.getReservations().add(schedule);
                    motorbike.update();

                    response.put("status", "Motorbike: " + motorbike.getPlateNumber() + " has been successfully marked as reserved ");
                }
            } else {
                response.put("status", "Vehicle Not Found");
            }


        } catch (Exception e) {
            response.put("status", e.getMessage());
            return ok(response);
        }

        return ok(response);
    }

    @Override
    public Result isAvailable(String plateNo, String pickUpDay, String pickUpMonth, String pickUpYear, String dropOffDay, String dropOffMonth, String dropOffYear) {
        ObjectNode response = Json.newObject();

        try {
            LocalDate pickUpDate = LocalDate.of(Integer.parseInt(pickUpYear), Integer.parseInt(pickUpMonth), Integer.parseInt(pickUpDay));
            LocalDate dropOffDate = LocalDate.of(Integer.parseInt(dropOffYear), Integer.parseInt(dropOffMonth), Integer.parseInt(dropOffDay));
            if (Car.find.query().where().eq("plate_number", plateNo).findCount() == 1) {
                Car car = Car.find.byId(plateNo);
                if (isReserved(car, pickUpDate, dropOffDate)) {
                    response.put("status", "The vehicle is not available to reserve");
                    return ok(response);
                }
            } else if (Motorbike.find.query().where().eq("plate_number", plateNo).findCount() == 1) {
                Motorbike motorbike = Motorbike.find.byId(plateNo);
                if (isReserved(motorbike, pickUpDate, dropOffDate)) {
                    response.put("status", "The vehicle is not available to reserve");
                    return ok(response);
                }
            }
        } catch (Exception e) {
            response.put("error", e.getLocalizedMessage());
            return ok(response);
        }
        response.put("status", "The vehicle is available to reserve");
        return ok(response);
    }

    @Override
    public Result isAvailableBool(String plateNo, String pickUpDay, String pickUpMonth, String pickUpYear, String dropOffDay, String dropOffMonth, String dropOffYear) {
        ObjectNode response = Json.newObject();

        try {
            LocalDate pickUpDate = LocalDate.of(Integer.parseInt(pickUpYear), Integer.parseInt(pickUpMonth), Integer.parseInt(pickUpDay));
            LocalDate dropOffDate = LocalDate.of(Integer.parseInt(dropOffYear), Integer.parseInt(dropOffMonth), Integer.parseInt(dropOffDay));
            if (Car.find.query().where().eq("plate_number", plateNo).findCount() == 1) {
                Car car = Car.find.byId(plateNo);
                if (isReserved(car, pickUpDate, dropOffDate)) {
                    return ok("false");
                }
            } else if (Motorbike.find.query().where().eq("plate_number", plateNo).findCount() == 1) {
                Motorbike motorbike = Motorbike.find.byId(plateNo);
                if (isReserved(motorbike, pickUpDate, dropOffDate)) {
                    return ok("false");
                }
            }
        } catch (Exception e) {
            response.put("error", e.getLocalizedMessage());
            return ok(response);
        }
        return ok("true");
    }

    //check availability of a vehicle in the database
    private boolean isAvailable(String plateNo) {
        if (Car.find.query().where().eq("plateNumber", plateNo).findCount() == 1) {
            return true;

        } else return Motorbike.find.query().where().eq("plateNumber", plateNo).findCount() == 1;
    }

    private boolean isReserved(Vehicle vehicle, LocalDate pickUpdate, LocalDate dropOffDate) {
        List<Schedule> list = null;

        if (vehicle instanceof Car) {
            list = ((Car) vehicle).getReservations();
        } else if (vehicle instanceof Motorbike) {
            list = ((Motorbike) vehicle).getReservations();
        }

        if ((!list.isEmpty())) {

            for (Schedule reservation : list) {
                if (((pickUpdate.isAfter(reservation.getPickUpDate()) && pickUpdate.isBefore(reservation.getDropOffDate())) || pickUpdate.isEqual(reservation.getPickUpDate()))
                        || ((dropOffDate.isAfter(reservation.getPickUpDate()) && dropOffDate.isBefore(reservation.getDropOffDate())) || dropOffDate.isEqual(reservation.getPickUpDate()))
                        || (pickUpdate.isBefore(reservation.getPickUpDate())) && dropOffDate.isAfter(reservation.getDropOffDate())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isExceeding() {
        return (Car.find.all().size() + Motorbike.find.all().size()) >= 50;
    }

    private int availableSpace() {
        return 50 - (Car.find.all().size() + Motorbike.find.all().size());
    }
}
