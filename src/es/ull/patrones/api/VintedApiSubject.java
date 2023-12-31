package es.ull.patrones.api;

import es.ull.patrones.observer.Observer;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class VintedApiSubject {
  String data;
  private List<Observer> observers = new ArrayList<>();

  public void addObserver(Observer observer) {
    observers.add(observer);
  }

  public void notifyObservers(List<Product> productList) {
    for (Observer observer : observers) {
      observer.update(productList);
    }
  }

  // Method that performs the HTTP request and processes the response.
  public void fetchData(int page, String object,int priceMin,int priceMax, int favourites) {
    try {
      String obj = convertir(object);
      HttpRequest request = HttpRequest.newBuilder()
              .uri(URI.create("https://vinted3.p.rapidapi.com/getSearch?country=es&page="+page+"&order=relevance&keyword="+obj+"&minPrice="+priceMin+"&maxPrice="+priceMax))
              .header("X-RapidAPI-Key", "27b0327141msh229d5637874896ep1e7633jsn1d252f6db918")
              .header("X-RapidAPI-Host", "vinted3.p.rapidapi.com")
              .method("GET", HttpRequest.BodyPublishers.noBody())
              .build();
      HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
      //Notifies observers with processed data
      data = response.body();
      ParseJSON parser = new ParseJSON(data, favourites);
      notifyObservers(parser.getProductList());

    } catch (InterruptedException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  public String convertir(String objeto) {
    return objeto.replace(" ", "%20");
  }
}
