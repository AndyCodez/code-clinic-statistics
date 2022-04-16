package Exceptions;

public class WeatherEntryNotFoundException extends RuntimeException {

    public WeatherEntryNotFoundException(String message) {
        super(message);
    }
}
