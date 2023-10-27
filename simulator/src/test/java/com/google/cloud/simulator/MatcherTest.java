package com.google.cloud.simulator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.cloud.orderbook.model.OrderBookEvent;

/** Tests of Simulator. */
@RunWith(JUnit4.class)
public class MatcherTest {

  // 2023-01-01 noon in milliseconds.
  final static long startTime = (new GregorianCalendar(2023, 0, 1, 12, 0, 0)).getTimeInMillis();

  private void expectMatches(List<OrderBookEvent> results, List<Long> expectedPrices, List<Long> expectedQty) {
    ArrayList<Long> prices = new ArrayList<Long>();
    ArrayList<Long> qty = new ArrayList<Long>();
    for (OrderBookEvent obe : results) {
      if (obe.getType().equals(OrderBookEvent.Type.EXECUTED)) {
        prices.add(obe.getPrice());
        qty.add(obe.getQuantityFilled());
      }
    }

    Assert.assertEquals("Expected prices to match", prices, expectedPrices);
    Assert.assertEquals("Expected qty to match", qty, expectedQty);
  }

  private void addOrder(Matcher m, Order order, OrderBookEvent... events) {
    Assert.assertEquals("Expected order events to match", 
      m.add(order),
      Arrays.asList(events)
    );
  }

  @Test
  public void matchTest() {
    Matcher m = new Matcher(new MatcherContext(10, startTime), 1);

    expectMatches(m.add(new Order(OrderBookEvent.Side.SELL, 100, 100)), Arrays.asList(), Arrays.asList());
    expectMatches(m.add(new Order(OrderBookEvent.Side.SELL, 100, 100)), Arrays.asList(), Arrays.asList());
    expectMatches(m.add(new Order(OrderBookEvent.Side.SELL, 100, 100)), Arrays.asList(), Arrays.asList());
    expectMatches(m.add(new Order(OrderBookEvent.Side.SELL, 101, 100)), Arrays.asList(), Arrays.asList());
    expectMatches(m.add(new Order(OrderBookEvent.Side.SELL, 101, 100)), Arrays.asList(), Arrays.asList());
    expectMatches(m.add(new Order(OrderBookEvent.Side.SELL, 102, 100)), Arrays.asList(), Arrays.asList());

    expectMatches(
      m.add(new Order(OrderBookEvent.Side.BUY, 100, 150)),
      Arrays.asList(100L, 100L),
      Arrays.asList(100L, 50L)
    );
  }

  @Test
  public void simpleTest() {
    Matcher m = new Matcher(new MatcherContext(1000, startTime), 1);

    // Add new sell order of q:100, p:100
    addOrder(m,
      new Order(OrderBookEvent.Side.SELL, 100, 100),
      OrderBookEvent.newBuilder()
        .setTimestampMS(startTime)
        .setSeqId(0)
        .setContractSeqId(0)
        .setContractId(1)
        .setType(OrderBookEvent.Type.NEW)
        .setOrderId(1)
        .setSide(OrderBookEvent.Side.SELL)
        .setPrice(100)
        .setQuantity(100)
        .setQuantityRemaining(100)
        .setQuantityFilled(0)
        .setMatchNumber(0)
        .build()
    );

    // Add new buy order of q:100, p:100
    // See execution of original sell order
    addOrder(m,
      new Order(OrderBookEvent.Side.BUY, 100, 100),
      OrderBookEvent.newBuilder()
        .setTimestampMS(startTime)
        .setSeqId(1)
        .setContractSeqId(1)
        .setContractId(1)
        .setType(OrderBookEvent.Type.EXECUTED)
        .setOrderId(1)
        .setSide(OrderBookEvent.Side.SELL)
        .setPrice(100)
        .setQuantity(100)
        .setQuantityRemaining(0)
        .setQuantityFilled(100)
        .setMatchNumber(0)
        .build()
    );
  }
}