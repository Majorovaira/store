package ua.spribe.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.spribe.store.model.Customer;
import ua.spribe.store.repository.CustomerRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Customer getById(long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException(String.format("customer with id %d not found", customerId)));
    }

    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
}
