package ua.spribe.store.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ua.spribe.store.model.Customer;
import ua.spribe.store.model.Product;
import ua.spribe.store.mutex.ProductMutex;
import ua.spribe.store.repository.ProductRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CustomerService customerService;
    private final ProductMutex productMutex;


    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product findById(long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException(String.format("product with id %d not found", productId)));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = IllegalStateException.class)
    public void buyProduct(long productId, long customerId) {
        Product product = findById(productId);
        Customer customer = customerService.getById(customerId);
        if (product.isOrdered()) {
            log.info("product was already buying");
            log.info("customer {} can't buy product {}", customerId, productId);
            throw new IllegalStateException("this product was already buying");
        } else {
            product.setOrdered(true);
            product.setCustomer(customer);
            log.info("customer {} buy product {}", customerId, productId);

            productRepository.save(product);
        }

    }

    public void buyProducts(List<Long> productIds, long customerId) {

        log.info("start buying products {} by customer {}", productIds, customerId);
        synchronized (productMutex.getLock(productIds)) {
            productIds
                    .forEach(id -> {
                        try {
                            buyProduct(id, customerId);
                        } catch (IllegalStateException e) {
                            log.error(e.getMessage());
                        }
                    });
        }
    }

    public List<Product> getByCustomerId(Long customerId) {
        return productRepository.findAllByCustomerId(customerId);
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }


}
