package am.trainings.palmetto.customer.service;

import am.trainings.palmetto.common.ApplicationContextHolder;
import am.trainings.palmetto.customer.Customer;
import am.trainings.palmetto.customer.repository.CustomerRepository;

import java.util.Optional;

public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(ApplicationContextHolder contextHolder) {
        this.customerRepository =
                contextHolder.getObject(CustomerRepository.class);
    }

    @Override
    public String registerCustomer(String fullName, String username) {
        Customer customer = new Customer(fullName, username);
        return customerRepository.registerCustomer(customer);
    }

    @Override
    public Optional<Customer> findCustomerByUsername(String username) {
        return customerRepository.findCustomerByUsername(username);
    }
}