
package am.trainings.palmetto.customer.repository;

import am.trainings.palmetto.common.JsonWriter;
import am.trainings.palmetto.customer.Customer;
import am.trainings.palmetto.customer.exceptions.DuplicateUsernameException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class CustomerFileRepository implements CustomerRepository {

    private final Map<String, Customer> customers = new HashMap<>();

    private static CustomerFileRepository INSTANCE;

    private long customerId = 0;

    private CustomerFileRepository() {
        JsonWriter writer = JsonWriter.getInstance();

        for (Customer customer : writer.readCustomers()) {
            customers.put(customer.getUsername(), customer);
            customerId = Math.max(
                    customerId,
                    customer.getCustomerId() + 1
            );
        }
    }

    public static CustomerFileRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CustomerFileRepository();
        }

        return INSTANCE;
    }

    @Override
    public String registerCustomer(Customer customer) {
        String username = customer.getUsername();

        if (customers.containsKey(username)) {
            throw new DuplicateUsernameException(
                    "Username: " + username + " already exists"
            );
        }

        customer.setCustomerId(customerId);

        customers.put(username, customer);

        try {
            JsonWriter.getInstance().writeCustomers(customers.values());
        } catch (RuntimeException e) {
            customers.remove(username);
            throw e;
        }

        customerId++;
        return username;
    }

    @Override
    public Optional<Customer> findCustomerByUsername(String username) {
        return Optional.ofNullable(customers.get(username));
    }
}
