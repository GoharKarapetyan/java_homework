package am.trainings.palmetto.customer.repository;

import am.trainings.palmetto.common.JsonWriter;
import am.trainings.palmetto.customer.Order;

import java.util.HashMap;
import java.util.Map;

public final class OrderFileRepository implements OrderRepository {

    private final Map<Long, Order> orders = new HashMap<>();

    private static OrderFileRepository INSTANCE;

    private long nextOrderId = 0;

    private OrderFileRepository() {
        JsonWriter writer = JsonWriter.getInstance();

        for (Order order : writer.readOrders()) {
            orders.put(order.getOrderId(), order);

            nextOrderId = Math.max(
                    nextOrderId,
                    order.getOrderId() + 1
            );
        }
    }

    public static OrderFileRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new OrderFileRepository();
        }

        return INSTANCE;
    }

    @Override
    public long save(Order order) {
        long orderId = nextOrderId;

        order.setOrderId(orderId);
        orders.put(orderId, order);

        try {
            JsonWriter.getInstance().writeOrders(orders.values());
        } catch (RuntimeException e) {
            orders.remove(orderId);
            order.setOrderId(null);
            throw e;
        }

        nextOrderId++;

        return orderId;
    }
}
