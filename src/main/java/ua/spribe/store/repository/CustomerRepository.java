package ua.spribe.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.spribe.store.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
