
package am.trainings.palmetto.common;

import am.trainings.palmetto.customer.Customer;
import am.trainings.palmetto.customer.Order;
import am.trainings.palmetto.pizza.Pizza;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class JsonWriter {

    private static final JsonWriter INSTANCE = new JsonWriter();

    private static final Path CUSTOMERS_FILE =
            Path.of("data", "customers.json");

    private static final Path ORDERS_FILE =
            Path.of("data", "orders.json");

    private JsonWriter() {
    }

    public static JsonWriter getInstance() {
        return INSTANCE;
    }

    public List<Customer> readCustomers() {
        JSONArray array = readArray(CUSTOMERS_FILE);
        List<Customer> customers = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            JSONObject json = array.getJSONObject(i);

            Customer customer = new Customer(
                    json.getString("fullName"),
                    json.getString("username")
            );

            customer.setCustomerId(json.getLong("customerId"));
            customers.add(customer);
        }

        return customers;
    }

    public void writeCustomers(Collection<Customer> customers) {
        JSONArray array = new JSONArray();

        for (Customer customer : customers) {
            JSONObject json = new JSONObject();

            json.put("customerId", customer.getCustomerId());
            json.put("fullName", customer.getFullName());
            json.put("username", customer.getUsername());

            array.put(json);
        }

        writeArray(CUSTOMERS_FILE, array);
    }

    public List<Order> readOrders() {
        JSONArray array = readArray(ORDERS_FILE);
        List<Order> orders = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            JSONObject json = array.getJSONObject(i);

            Order order = new Order(json.getLong("customerId"));
            order.setOrderId(json.getLong("orderId"));

            JSONArray pizzas = json.getJSONArray("pizzas");

            for (int j = 0; j < pizzas.length(); j++) {
                JSONObject pizzaJson = pizzas.getJSONObject(j);
                JSONArray ingredientsJson =
                        pizzaJson.getJSONArray("ingredients");

                String[] ingredients =
                        new String[ingredientsJson.length()];

                for (int k = 0; k < ingredientsJson.length(); k++) {
                    if (!ingredientsJson.isNull(k)) {
                        ingredients[k] = ingredientsJson.getString(k);
                    }
                }

                Pizza pizza = new Pizza(
                        pizzaJson.getString("name"),
                        pizzaJson.getString("type"),
                        ingredients
                );

                int count = pizzaJson.getInt("count");
                order.addPizza(pizza, count);
            }

            orders.add(order);
        }

        return orders;
    }

    public void writeOrders(Collection<Order> orders) {
        JSONArray array = new JSONArray();

        for (Order order : orders) {
            JSONObject json = new JSONObject();

            json.put("orderId", order.getOrderId());
            json.put("customerId", order.getCustomerId());

            JSONArray pizzas = new JSONArray();

            for (Map.Entry<Pizza, Integer> entry
                    : order.getPizzas().entrySet()) {

                Pizza pizza = entry.getKey();
                JSONObject pizzaJson = new JSONObject();

                pizzaJson.put("name", pizza.getName());
                pizzaJson.put("type", pizza.getType());

                JSONArray ingredients = new JSONArray();

                for (String ingredient : pizza.getIngredients()) {
                    ingredients.put(
                            ingredient == null
                                    ? JSONObject.NULL
                                    : ingredient
                    );
                }

                pizzaJson.put("ingredients", ingredients);
                pizzaJson.put("count", entry.getValue());

                pizzas.put(pizzaJson);
            }

            json.put("pizzas", pizzas);
            array.put(json);
        }

        writeArray(ORDERS_FILE, array);
    }

    private JSONArray readArray(Path path) {
        try {
            if (Files.notExists(path)) {
                return new JSONArray();
            }

            String content = Files.readString(path);

            if (content.isBlank()) {
                return new JSONArray();
            }

            return new JSONArray(content);
        } catch (IOException e) {
            throw new UncheckedIOException(
                    "Could not read file: " + path, e
            );
        }
    }

    private void writeArray(Path path, JSONArray array) {
        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, array.toString(2));
        } catch (IOException e) {
            throw new UncheckedIOException(
                    "Could not write file: " + path, e
            );
        }
    }
}
