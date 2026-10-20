package am.trainings.palmetto.customer;


import am.trainings.palmetto.pizza.Pizza;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * add ability to order several types of pizzas
 */


@Data
public class Order {

    private Long orderId;
    private final Long customerId;
    private final Map<Pizza, Integer> pizzas;

    public Order(Long customerId) {
        this.customerId = customerId;
        this.pizzas = new HashMap<>();
    }

    public Order(Long customerId, Map<Pizza, Integer> pizzas) {
        this.customerId = customerId;
        this.pizzas = new HashMap<>(pizzas);
    }

    public void addPizza(Pizza pizza, int count) {
        if (count <= 0) {
            throw new IllegalArgumentException(
                    "Pizza count must be greater than zero"
            );
        }

        pizzas.merge(pizza, count, Integer::sum);
    }
}
