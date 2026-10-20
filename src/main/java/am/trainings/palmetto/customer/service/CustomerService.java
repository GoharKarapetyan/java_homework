package am.trainings.palmetto.customer.service;

import am.trainings.palmetto.customer.Customer;

import java.util.Optional;

public interface CustomerService {
    String registerCustomer(String fullName, String username);

    Optional<Customer> findCustomerByUsername(String username);
}
