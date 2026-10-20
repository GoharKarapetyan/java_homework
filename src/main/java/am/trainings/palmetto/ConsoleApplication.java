
package am.trainings.palmetto;

import am.trainings.palmetto.common.ApplicationContextHolder;
import am.trainings.palmetto.customer.Customer;
import am.trainings.palmetto.customer.service.CustomerService;
import am.trainings.palmetto.customer.service.CustomerServiceImpl;
import am.trainings.palmetto.customer.service.OrderService;
import am.trainings.palmetto.customer.service.OrderServiceImpl;
import am.trainings.palmetto.pizza.Pizza;
import am.trainings.palmetto.pizza.PizzaType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleApplication {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        ApplicationContextHolder contextHolder =
                new ApplicationContextHolder();

        CustomerService customerService =
                new CustomerServiceImpl(contextHolder);

        OrderService orderService =
                new OrderServiceImpl(contextHolder);

        while (true) {
            System.out.println("\n=== Pizzeria Palmetto ===");
            System.out.println("1. Register customer");
            System.out.println("2. Find customer");
            System.out.println("3. Create order");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    System.out.print("Full name: ");
                    String fullName = scanner.nextLine().trim();

                    System.out.print("Username: ");
                    String username = scanner.nextLine().trim();

                    if (fullName.isEmpty() || username.isEmpty()) {
                        System.out.println(
                                "Full name and username cannot be empty.");
                        continue;
                    }

                    try {
                        String registeredUsername =
                                customerService.registerCustomer(
                                        fullName, username);

                        Customer customer = customerService
                                .findCustomerByUsername(registeredUsername)
                                .orElseThrow(() ->
                                        new IllegalStateException(
                                                "Registered customer not found"));

                        System.out.println(
                                "\nCustomer registered successfully!");
                        System.out.println(
                                "Customer ID: " + customer.getCustomerId());
                        System.out.println(
                                "Full name: " + customer.getFullName());
                        System.out.println(
                                "Username: " + customer.getUsername());

                    } catch (RuntimeException e) {
                        System.out.println(
                                "Registration failed: " + e.getMessage());
                    }
                }

                case "2" -> {
                    System.out.print("Username: ");
                    String username = scanner.nextLine().trim();

                    Optional<Customer> customer =
                            customerService.findCustomerByUsername(username);

                    customer.ifPresentOrElse(
                            c -> {
                                System.out.println("\nCustomer found!");
                                System.out.println(
                                        "Customer ID: " + c.getCustomerId());
                                System.out.println(
                                        "Full name: " + c.getFullName());
                                System.out.println(
                                        "Username: " + c.getUsername());
                            },
                            () -> System.out.println("Customer not found.")
                    );
                }

                case "3" -> {
                    System.out.print("Customer username: ");
                    String username = scanner.nextLine().trim();

                    Optional<Customer> customerOptional =
                            customerService.findCustomerByUsername(username);

                    if (customerOptional.isEmpty()) {
                        System.out.println(
                                "Order failed: customer not found.");
                        continue;
                    }

                    Customer customer = customerOptional.get();
                    Map<Pizza, Integer> pizzas = new HashMap<>();

                    while (true) {
                        System.out.println("\nAvailable pizzas:");
                        System.out.println(
                                "1. Regular - "
                                        + PizzaType.REGULAR.getPrice());
                        System.out.println(
                                "2. Calzone - "
                                        + PizzaType.CALZONE.getPrice());
                        System.out.println("0. Finish order");
                        System.out.print("Choose pizza: ");

                        String pizzaChoice = scanner.nextLine().trim();

                        if (pizzaChoice.equals("0")) {
                            break;
                        }

                        Pizza pizza;

                        switch (pizzaChoice) {
                            case "1" -> pizza = Pizza.REGULAR;
                            case "2" -> pizza = new Pizza(
                                    "Calzone",
                                    PizzaType.CALZONE.name(),
                                    new String[7]);
                            default -> {
                                System.out.println(
                                        "Invalid pizza choice.");
                                continue;
                            }
                        }

                        System.out.print("Quantity: ");
                        int quantity;

                        try {
                            quantity = Integer.parseInt(
                                    scanner.nextLine().trim());
                        } catch (NumberFormatException e) {
                            System.out.println(
                                    "Enter a valid whole number.");
                            continue;
                        }

                        if (quantity <= 0) {
                            System.out.println(
                                    "Quantity must be greater than zero.");
                            continue;
                        }

                        pizzas.merge(pizza, quantity, Integer::sum);

                        System.out.println(
                                "Added: " + pizza.getName()
                                        + " x " + quantity);
                    }

                    if (pizzas.isEmpty()) {
                        System.out.println(
                                "Order was not created: no pizzas.");
                        continue;
                    }

                    try {
                        long orderId =
                                orderService.orderPizza(username, pizzas);

                        System.out.println(
                                "\nOrder created and saved successfully!");
                        System.out.println("Order ID: " + orderId);
                        System.out.println(
                                "Customer ID: " + customer.getCustomerId());
                        System.out.println(
                                "Customer name: " + customer.getFullName());
                        System.out.println(
                                "Customer username: " + customer.getUsername());
                        System.out.println("Pizzas:");

                        pizzas.forEach((pizza, quantity) ->
                                System.out.println(
                                        " - " + pizza.getName()
                                                + ", type: " + pizza.getType()
                                                + ", quantity: " + quantity
                                )
                        );

                    } catch (RuntimeException e) {
                        System.out.println(
                                "Order failed: " + e.getMessage());
                    }
                }

                case "0" -> {
                    System.out.println("Goodbye!");
                    return;
                }

                default -> System.out.println("Invalid option.");
            }
        }
    }
}
