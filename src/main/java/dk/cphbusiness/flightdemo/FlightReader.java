package dk.cphbusiness.flightdemo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dk.cphbusiness.utils.Utils;
import lombok.*;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;

/**
 * Purpose:
 *
 * @author: Thomas Hartmann
 */
public class FlightReader {

    static List<DTOs.FlightInfo> flightInfoList;

    public static void main(String[] args) {
        FlightReader flightReader = new FlightReader();
        try {
            List<DTOs.FlightDTO> flightList = flightReader.getFlightsFromFile("flights.json");
            flightInfoList = flightReader.getFlightInfoDetails(flightList);
            flightInfoList.forEach(f->{
                System.out.println("\n"+f);
            });

            flightReader.airLineAvg("Lufthansa");
            flightReader.airLineTotal("Lufthansa");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


//    public List<FlightDTO> jsonFromFile(String fileName) throws IOException {
//        List<FlightDTO> flights = getObjectMapper().readValue(Paths.get(fileName).toFile(), List.class);
//        return flights;
//    }


    public List<DTOs.FlightInfo> getFlightInfoDetails(List<DTOs.FlightDTO> flightList) {
        List<DTOs.FlightInfo> flightInfoList = flightList.stream().map(flight -> {
            Duration duration = Duration.between(flight.getDeparture().getScheduled(), flight.getArrival().getScheduled());
            DTOs.FlightInfo flightInfo = DTOs.FlightInfo.builder()
                    .name(flight.getFlight().getNumber())
                    .iata(flight.getFlight().getIata())
                    .airline(flight.getAirline().getName())
                    .duration(duration)
                    .departure(flight.getDeparture().getScheduled().toLocalDateTime())
                    .arrival(flight.getArrival().getScheduled().toLocalDateTime())
                    .origin(flight.getDeparture().getAirport())
                    .destination(flight.getArrival().getAirport())
                    .build();

            return flightInfo;
        }).toList();
        return flightInfoList;
    }

    public List<DTOs.FlightDTO> getFlightsFromFile(String filename) throws IOException {
        DTOs.FlightDTO[] flights = new Utils().getObjectMapper().readValue(Paths.get(filename).toFile(), DTOs.FlightDTO[].class);

        List<DTOs.FlightDTO> flightList = Arrays.stream(flights).toList();
        return flightList;
    }

    public double airLineAvg (String airLine){
        double averageForLufthansa = flightInfoList.stream()
                .filter(flightInfo -> airLine.equals(flightInfo.getAirline()))
                .mapToDouble(flightInfo -> flightInfo.getDuration().toHours())
                .average()
                .orElse(0.0);

        System.out.println("\n \nGennemsnitlig tid for " + airLine + " flytider er " + averageForLufthansa + " timer.");

        return averageForLufthansa;
    }

    public double airLineTotal (String airLine){
        double totalForLufthansa = flightInfoList.stream()
                .filter(flightInfo -> airLine.equals(flightInfo.getAirline()))
                .mapToDouble(flightInfo -> flightInfo.getDuration().toHours())
                .sum();

        System.out.println("\n \nTotal tid for " + airLine + " flytider er " + totalForLufthansa + " timer.");

        return totalForLufthansa;
    }


}
