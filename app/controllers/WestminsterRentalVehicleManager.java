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

// This class is the implementation of the Westminster car rental system.
public class WestminsterRentalVehicleManager extends Controller implements RentalVehicleManager {

    // This method is used to add a new vehicle to the system.
    @Override
    public Result addVehicle() {
        // Create a new JSON object to store the response.
        ObjectNode response = Json.newObject();
        // Get the data from the POST request.
        Map<String, String[]> body = request().body().asFormUrlEncoded();

        try {
            // Check if the body is not null.
            if (body != null) {
                // Check if the vehicle with the given plate number is already available.
                if (body.containsKey("plateNo") && isAvailable(body.get("plateNo")[0])) {
                    response.put("error", "Item already available");

                // Check if the size limit is exceeded.
                } else if (isExceeding()) {
                    response.put("error", "Size Limit Exceeds");

                // Check if the body contains the type of the vehicle.
                } else if (body.containsKey("type")) {
                    // Check if the body contains the type, make, and model of the vehicle.
                    if (body.containsKey("type") && body.containsKey("make") && body.containsKey("model")) {
                        // Get the plate number, make, and model from the body.
                        String plateNo = body.get("plateNo")[0];
                        String make = body.get("make")[0];
                        String model = body.get("model")[0];

                        // Check if the vehicle is a car and if the body contains the number of seats, AC availability, and transmission type.
                        if (body.get("type")[0].equals("car") && body.containsKey("noOfSeats") && body.containsKey("withAC") && body.containsKey("transmission")) {
                            // Get the number of seats, AC availability, and transmission type from the body.
                            int noOfSeats = Integer.parseInt(body.get("noOfSeats")[0]);
                            boolean withAC = Boolean.parseBoolean(body.get("withAC")[0]);
                            Transmission transmission = Transmission.valueOf(body.get("transmission")[0]);

                            // Create a new car object and save it to the database.
                            Car car = new Car(plateNo, make, model, noOfSeats, withAC, transmission);
                            car.save();
                            response.put("success", "Car Added Successfully");

                        // Check if the vehicle is a bike and if the body contains the ABS availability and number of gears.
                        } else if (body.get("type")[0].equals("bike") && body.containsKey("isWithABS") && body.containsKey("noOfGears")) {
                            // Get the ABS availability and number of gears from the body.
                            int noOfGears = Integer.parseInt(body.get("noOfGears")[0]);
                            boolean isWithABS = Boolean.parseBoolean(body.get("isWithABS")[0]);

                            // Create a new motorbike object and save it to the database.
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
            // If an exception occurs, add the error message to the response.
            response.put("error", e.getMessage());
            return ok(response);
        }
        // If the response is empty, add an error message.
        if (response.size() == 0) {
            response.put("error", "error");
        }
        // Return the response.
        return ok(response);
    }

    // This method is used to delete a vehicle from the system.
    // The plate number of the vehicle is passed as a parameter.
    @Override
    public Result deleteVehicle(String plateNo) {
        // Create a new JSON object to store the response.
        ObjectNode response = Json.newObject();

        // Check if the vehicle is a car.
        if (Car.find.query().where().eq("plate_number", plateNo).findCount() == 1) {
            // Get the car from the database and delete it.
            Car car = Car.find.byId(plateNo);
            car.delete();

            // Add a success message to the response.
            response.put("status", "Car: " + car.getPlateNumber() + " deleted successfully");

        // Check if the vehicle is a motorbike.
        } else if (Motorbike.find.query().where().eq("plate_number", plateNo).findCount() == 1) {
            // Get the motorbike from the database and delete it.
            Motorbike motorbike = Motorbike.find.byId(plateNo);
            motorbike.delete();

            // Add a success message to the response.
            response.put("status", "MotorBike: " + motorbike.getPlateNumber() + " deleted successfully");

        } else {
            // If the vehicle is not found, add an error message to the response.
            response.put("status", "Item Not Found");
        }

        // Add the available space to the response.
        response.put("Available", availableSpace());
        // Return the response.
        return ok(response);
    }

    // This method is used to list all the vehicles in the system.
    @Override
    public Result listAll() {
        // Get all the cars and motorbikes from the database.
        List<Car> cars = Car.find.all();
        List<Motorbike> motorbikes = Motorbike.find.all();

        // Convert the lists to JSON arrays.
        ArrayNode node1 = ((ArrayNode) Json.toJson(cars));
        ArrayNode node2 = ((ArrayNode) Json.toJson(motorbikes));

        // Create a map to store the arrays.
        Map<String, ArrayNode> map = new HashMap<>();
        map.put("Cars", node1);
        map.put("Motorbikes", node2);

        // Return the map as a JSON object.
        return ok(Json.toJson(map));
    }

    // This method is used to reserve a vehicle.
    @Override
    public Result reserveVehicle() {
        // Create a new JSON object to store the response.
        ObjectNode response = Json.newObject();

        // Get the data from the POST request.
        Map<String, String[]> body = request().body().asFormUrlEncoded();

        try {
            // Get the plate number from the body.
            String plateNo = body.get("plateNo")[0];

            // Get the pick-up and drop-off dates from the body.
            int pickUpDay = Integer.parseInt(body.get("pickUpDate")[0]);
            int pickUpMonth = Integer.parseInt(body.get("pickUpMonth")[0]);
            int pickUpYear = Integer.parseInt(body.get("pickUpYear")[0]);
            int dropOffDay = Integer.parseInt(body.get("dropOffDate")[0]);
            int dropOffMonth = Integer.parseInt(body.get("dropOffMonth")[0]);
            int dropOffYear = Integer.parseInt(body.get("dropOffYear")[0]);

            // Convert the dates to LocalDate objects.
            LocalDate pickUpDate = LocalDate.of(pickUpYear, pickUpMonth, pickUpDay);
            LocalDate dropOffDate = LocalDate.of(dropOffYear, dropOffMonth, dropOffDay);

            // Create a new schedule object.
            Schedule schedule = new Schedule(pickUpDate, dropOffDate);

            // Check if the vehicle is a car.
            if (Car.find.query().where().eq("plate_number", plateNo).findCount() == 1) {

                // Get the car from the database.
                Car car = Car.find.byId(plateNo);

                // Check if the car is already reserved.
                if (isReserved(car, pickUpDate, dropOffDate)) {
                    // If the car is already reserved, add an error message to the response.
                    response.put("status", "Car has been already reserved by someone.");

                } else {
                    // If the car is not reserved, add the schedule to the car's reservations and update the car in the database.
                    car.getReservations().add(schedule);
                    car.update();

                    // Add a success message to the response.
                    response.put("status", "Car: " + car.getPlateNumber() + " has been successfully marked as reserved ");
                }

            // Check if the vehicle is a motorbike.
            } else if (Motorbike.find.query().where().eq("plate_number", plateNo).findCount() == 1) {

                // Get the motorbike from the database.
                Motorbike motorbike = Motorbike.find.byId(plateNo);

                // Check if the motorbike is already reserved.
                if (isReserved(motorbike, pickUpDate, dropOffDate)) {
                    // If the motorbike is already reserved, add an error message to the response.
                    response.put("status", "Motorbike has been already reserved by someone.");

                } else {
                    // If the motorbike is not reserved, add the schedule to the motorbike's reservations and update the motorbike in the database.
                    motorbike.getReservations().add(schedule);
                    motorbike.update();

                    // Add a success message to the response.
                    response.put("status", "Motorbike: " + motorbike.getPlateNumber() + " has been successfully marked as reserved ");
                }
            } else {
                // If the vehicle is not found, add an error message to the response.
                response.put("status", "Vehicle Not Found");
            }


        } catch (Exception e) {
            // If an exception occurs, add the error message to the response.
            response.put("status", e.getMessage());
            return ok(response);
        }

        // Return the response.
        return ok(response);
    }

    // This method is used to check if a vehicle is available for reservation.
    @Override
    public Result isAvailable(String plateNo, String pickUpDay, String pickUpMonth, String pickUpYear, String dropOffDay, String dropOffMonth, String dropOffYear) {
        // Create a new JSON object to store the response.
        ObjectNode response = Json.newObject();

        try {
            // Convert the pick-up and drop-off dates to LocalDate objects.
            LocalDate pickUpDate = LocalDate.of(Integer.parseInt(pickUpYear), Integer.parseInt(pickUpMonth), Integer.parseInt(pickUpDay));
            LocalDate dropOffDate = LocalDate.of(Integer.parseInt(dropOffYear), Integer.parseInt(dropOffMonth), Integer.parseInt(dropOffDay));
            // Check if the vehicle is a car.
            if (Car.find.query().where().eq("plate_number", plateNo).findCount() == 1) {
                // Get the car from the database.
                Car car = Car.find.byId(plateNo);
                // Check if the car is reserved.
                if (isReserved(car, pickUpDate, dropOffDate)) {
                    // If the car is reserved, add an error message to the response.
                    response.put("status", "The vehicle is not available to reserve");
                    return ok(response);
                }
            // Check if the vehicle is a motorbike.
            } else if (Motorbike.find.query().where().eq("plate_number", plateNo).findCount() == 1) {
                // Get the motorbike from the database.
                Motorbike motorbike = Motorbike.find.byId(plateNo);
                // Check if the motorbike is reserved.
                if (isReserved(motorbike, pickUpDate, dropOffDate)) {
                    // If the motorbike is reserved, add an error message to the response.
                    response.put("status", "The vehicle is not available to reserve");
                    return ok(response);
                }
            }
        } catch (Exception e) {
            // If an exception occurs, add the error message to the response.
            response.put("error", e.getLocalizedMessage());
            return ok(response);
        }
        // If the vehicle is available, add a success message to the response.
        response.put("status", "The vehicle is available to reserve");
        return ok(response);
    }

    // This method is used to check if a vehicle is available for reservation and returns a boolean value.
    @Override
    public Result isAvailableBool(String plateNo, String pickUpDay, String pickUpMonth, String pickUpYear, String dropOffDay, String dropOffMonth, String dropOffYear) {
        // Create a new JSON object to store the response.
        ObjectNode response = Json.newObject();

        try {
            // Convert the pick-up and drop-off dates to LocalDate objects.
            LocalDate pickUpDate = LocalDate.of(Integer.parseInt(pickUpYear), Integer.parseInt(pickUpMonth), Integer.parseInt(pickUpDay));
            LocalDate dropOffDate = LocalDate.of(Integer.parseInt(dropOffYear), Integer.parseInt(dropOffMonth), Integer.parseInt(dropOffDay));
            // Check if the vehicle is a car.
            if (Car.find.query().where().eq("plate_number", plateNo).findCount() == 1) {
                // Get the car from the database.
                Car car = Car.find.byId(plateNo);
                // Check if the car is reserved.
                if (isReserved(car, pickUpDate, dropOffDate)) {
                    // If the car is reserved, return false.
                    return ok("false");
                }
            // Check if the vehicle is a motorbike.
            } else if (Motorbike.find.query().where().eq("plate_number", plateNo).findCount() == 1) {
                // Get the motorbike from the database.
                Motorbike motorbike = Motorbike.find.byId(plateNo);
                // Check if the motorbike is reserved.
                if (isReserved(motorbike, pickUpDate, dropOffDate)) {
                    // If the motorbike is reserved, return false.
                    return ok("false");
                }
            }
        } catch (Exception e) {
            // If an exception occurs, add the error message to the response.
            response.put("error", e.getLocalizedMessage());
            return ok(response);
        }
        // If the vehicle is available, return true.
        return ok("true");
    }

    // This method is used to check if a vehicle is available in the database.
    private boolean isAvailable(String plateNo) {
        // Check if the vehicle is a car.
        if (Car.find.query().where().eq("plateNumber", plateNo).findCount() == 1) {
            // If the vehicle is a car, return true.
            return true;

        // If the vehicle is not a car, check if it is a motorbike.
        } else return Motorbike.find.query().where().eq("plateNumber", plateNo).findCount() == 1;
    }

    // This method is used to check if a vehicle is reserved.
    private boolean isReserved(Vehicle vehicle, LocalDate pickUpdate, LocalDate dropOffDate) {
        // Create a list to store the reservations.
        List<Schedule> list = null;

        // Check if the vehicle is a car.
        if (vehicle instanceof Car) {
            // If the vehicle is a car, get the reservations.
            list = ((Car) vehicle).getReservations();
        // Check if the vehicle is a motorbike.
        } else if (vehicle instanceof Motorbike) {
            // If the vehicle is a motorbike, get the reservations.
            list = ((Motorbike) vehicle).getReservations();
        }

        // Check if the list is not empty.
        if ((!list.isEmpty())) {

            // Iterate through the reservations.
            for (Schedule reservation : list) {
                // Check if the pick-up and drop-off dates are within the reservation dates.
                if (((pickUpdate.isAfter(reservation.getPickUpDate()) && pickUpdate.isBefore(reservation.getDropOffDate())) || pickUpdate.isEqual(reservation.getPickUpDate()))
                        || ((dropOffDate.isAfter(reservation.getPickUpDate()) && dropOffDate.isBefore(reservation.getDropOffDate())) || dropOffDate.isEqual(reservation.getPickUpDate()))
                        || (pickUpdate.isBefore(reservation.getPickUpDate())) && dropOffDate.isAfter(reservation.getDropOffDate())) {
                    // If the dates are within the reservation dates, return true.
                    return true;
                }
            }
        }
        // If the dates are not within the reservation dates, return false.
        return false;
    }

    // This method is used to check if the size limit is exceeded.
    private boolean isExceeding() {
        // Check if the total number of cars and motorbikes is greater than or equal to 50.
        return (Car.find.all().size() + Motorbike.find.all().size()) >= 50;
    }

    // This method is used to get the available space.
    private int availableSpace() {
        // Return the difference between 50 and the total number of cars and motorbikes.
        return 50 - (Car.find.all().size() + Motorbike.find.all().size());
    }
}

