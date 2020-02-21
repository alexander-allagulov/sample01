package com.apress.ch04.sample01.service;

import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.apress.ch04.sample01.model.Item;
import com.apress.ch04.sample01.model.Order;
import com.apress.ch04.sample01.model.PaymentMethod;

@RestController
@RequestMapping(value = "/order")
public class OrderProcessing {

  /**
   * this method accepts an order id and returns back the status of the order.
   *
   * @param orderId
   * @return
   */
  @RequestMapping(value = "/{id}/status", method = RequestMethod.GET)
  public ResponseEntity<?> checkOrderStatus(@PathVariable("id") String orderId) {
    return ResponseEntity.ok("{'status' : 'shipped'}");
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public ResponseEntity<?> getOrder(@PathVariable("id") String orderId) {
    Item book1 = new Item("101", 1);
    Item book2 = new Item("103", 5);
    PaymentMethod myvisa = new PaymentMethod("VISA", "01/22", "John Doe", "201, 1st Street, San Jose, CA");
    Order order = new Order("101021", orderId, myvisa, new Item[]{book1, book2}, "201, 1st Street, San Jose, CA");
    return ResponseEntity.ok(order);
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<?> createOrder(@RequestBody Order order) {

    if (order != null) {
      RestTemplate restTemplate = new RestTemplate();
      URI uri = URI.create("http://localhost:9000/inventory");
      restTemplate.put(uri, order.getItems());

      order.setOrderId(UUID.randomUUID().toString());
      URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
              .buildAndExpand(order.getOrderId()).toUri();

      return ResponseEntity.created(location).build();
    }

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
  }

  @RequestMapping(value = "/ping/{msg}", method = RequestMethod.GET)
  public ResponseEntity<?> ping(@PathVariable("msg") String message) {
    String outMsg = null;
    StringBuilder sb = new StringBuilder();

    String url = "http://10.28.43.16:26011/integration/CRMB2BService?wsdl";
    try {
      URLConnection conn = new URL(url).openConnection();
      try (BufferedReader is = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
        String line;
        while ((line = is.readLine()) != null) {
          System.out.println(line);
          sb.append(line + "\n");
        }
      }
      outMsg = sb.toString();
    } catch (Exception e) {
      outMsg = e.getMessage();
    }
    //return ResponseEntity.ok("{'ping' : '" + outMsg + "'}");
    return ResponseEntity.ok(outMsg);
  }

}
