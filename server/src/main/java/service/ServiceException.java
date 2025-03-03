package service;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Indicates there was an error connecting to the database
 */
public class ServiceException extends Exception{
  final private int statusCode;

  public ServiceException(int statusCode, String message) {
    super(message);
    this.statusCode = statusCode;
  }

  public String toJson() {
    return new Gson().toJson(Map.of("message", getMessage(), "status", statusCode));
  }

  public static ServiceException fromJson(InputStream stream) {
    var map = new Gson().fromJson(new InputStreamReader(stream), HashMap.class);
    var status = ((Double)map.get("status")).intValue();
    String message = map.get("message").toString();
    return new ServiceException(status, message);
  }

  public int StatusCode() {
    return statusCode;
  }
}
