package ua.spribe.store.utils;

import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.stereotype.Service;
import ua.spribe.store.model.Customer;
import ua.spribe.store.model.Product;
import ua.spribe.store.service.CustomerService;
import ua.spribe.store.service.ProductService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
@Service
public class DataGenerator {

    private final ProductService productService;
    private final CustomerService customerService;


    public void generateData(int products, int customers) {
        if (products < 0 || customers < 0) {
            throw new IllegalArgumentException("number of generated data must be positive");
        }
        for (int i = 0; i < products; i++) {
            String productName = RandomString.make();
            int productWage = ThreadLocalRandom.current().nextInt(1, 100);
            BigDecimal productPrice = BigDecimal.valueOf(ThreadLocalRandom.current().nextLong(10, 100));

            Product product = Product.builder()
                    .withName(productName)
                    .withWage(productWage)
                    .withPrice(productPrice)
                    .withOrdered(false)
                    .build();
            productService.saveProduct(product);
        }
        for (int i = 0; i < customers; i++) {
            String customerName = RandomString.make();
            Customer customer = Customer.builder()
                    .withName(customerName)
                    .build();
            customerService.saveCustomer(customer);
        }
    }


    public Customer getRandomCustomer() {

        List<Customer> allCustomers = customerService.getAllCustomers();
        int listSize = allCustomers.size();
        return allCustomers.get(ThreadLocalRandom.current().nextInt(listSize));

    }

    public List<Long> getRandomListProduct() {

        List<Product> allProducts = productService.getAllProducts();
        int listSize = allProducts.size();
        int countBuyingProducts = ThreadLocalRandom.current().nextInt(1, listSize / 2);
        List<Long> resultIds = new ArrayList<>();
        for (int i = 0; i < countBuyingProducts; i++) {
            long currentId = allProducts.get(ThreadLocalRandom.current().nextInt(listSize)).getId();
            resultIds.add(currentId);
        }
        return resultIds;
    }
}
