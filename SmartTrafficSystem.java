import java.util.*;
import java.util.function.*;
import java.util.stream.*;



class SmartTrafficSystem {
    static class VehicleEvent{
        String vehicleId;
        double speed;
        String zone;
        boolean isEmergencyVehicle;
        long timestamp;

        public VehicleEvent(String vehicleId, double speed, String zone, boolean isEmergencyVehicle, long timestamp) {
            this.vehicleId = vehicleId;
            this.speed = speed;
            this.zone = zone;
            this.isEmergencyVehicle = isEmergencyVehicle;
            this.timestamp = timestamp;
        }
    }

    static class ViolationRecord{
        String vehicleId;
        double speed;
        String zone;
        int fine;

        public ViolationRecord(String vehicleId, double speed, String zone, int fine) {
            this.vehicleId = vehicleId;
            this.speed = speed;
            this.zone = zone;
            this.fine = fine;
        }
        @Override
        public String toString() {
            return "Vehicle: "+vehicleId+" | speed: "+speed+" | Zone"+zone+" | Fine: "+fine;
        }
    }

    static class TrafficRules{

        static Predicate<VehicleEvent>violationFilter=event ->
                event.speed >80&& !event.isEmergencyVehicle;

        static Function<Double, Integer>fineCalculator=speed ->{
            if(speed>120) return 5000;
            else if(speed>100) return 2000;
            else return 5000;
        };
    }

    public static void main(String[] args) {

        List<VehicleEvent> events=Arrays.asList(
                new VehicleEvent("MH12AB1234", 95,"Pune-West",false,System.currentTimeMillis()),
                new VehicleEvent("PC12An1234", 130,"Pndicherry-West",false,System.currentTimeMillis()),
                new VehicleEvent("Bh12BC1111", 110,"Bharat-East",false,System.currentTimeMillis()),
                new VehicleEvent("PJ12MC1234", 70,"Panjab-West",false,System.currentTimeMillis()),
                new VehicleEvent("RJ18At1734", 140,"Jaipur-West",true,System.currentTimeMillis())
        );

        List<ViolationRecord> violations = events
                .parallelStream()
                .filter(Objects::nonNull)
                .filter(TrafficRules.violationFilter)
                .map((VehicleEvent event) -> {


                    String vehicleId = Optional.ofNullable(event.vehicleId)
                            .orElse("UNKNOWN");

                    String zone = Optional.ofNullable(event.zone)
                            .orElse("UNKNOWN_ZONE");


                    int fine=TrafficRules.fineCalculator.apply(event.speed);

                    return new ViolationRecord(vehicleId,event.speed, zone, fine);
                })
                .collect(Collectors.toList());

        Consumer<ViolationRecord> logger =System.out::println;
        violations.forEach(logger);

        //Aggregation using reduce()
        int totalFine = violations.stream()
                .map((ViolationRecord v) -> v.fine)
                .reduce(0, Integer::sum);

        long totalViolations = violations.stream().count();

        System.out.println("\nTotal Violations: " + totalViolations);
        System.out.println("Total Fine Collected: ₹" + totalFine);

        Map<String, Long> violationsByZone = violations.stream()
                .collect(Collectors.groupingBy(
                        (ViolationRecord v) -> v.zone,
                        Collectors.counting()
                ));

        System.out.println("\nViolations by Zone:");

        violationsByZone.forEach((String zone, Long count) ->
                System.out.println(zone + " -> " + count)
        );

    }
}

