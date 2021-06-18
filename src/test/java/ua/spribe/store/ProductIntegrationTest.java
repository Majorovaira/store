package ua.spribe.store;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import ua.spribe.store.model.Customer;
import ua.spribe.store.model.Product;
import ua.spribe.store.service.CustomerService;
import ua.spribe.store.service.ProductService;
import ua.spribe.store.utils.DataGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@AutoConfigureMockMvc
@Testable
@ContextConfiguration
@TestPropertySource(value = "classpath:application-integration-test.yaml")
@SpringBootTest
public class ProductIntegrationTest {

    public static final String PRODUCT_IDS = "productIds";
    public static final String CUSTOMER_ID = "customerId";
    /*
      Because in this case I don't need custom implementation and I worked without third weekend(I really exhausted and I try, trust me)
    */
    @Rule
    public PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer();

    @Autowired
    private ProductService productService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private DataGenerator dataGenerator;

    @Autowired
    private CustomerService customerService;

    @BeforeEach
    public void generateData() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
        postgreSQLContainer.withDatabaseName("store");
        /*
        Here we can customize number of generated products and customers.
         */
        dataGenerator.generateData(10, 5);
    }

    @Test
    public void testContainer() throws Exception {

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            executorService.submit(this::buyProduct);
        }
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        executorService.shutdown();

        customerService.getAllCustomers().forEach(customer -> {
                    log.info("customer with id {} buys products with ids {}", customer.getId(), productService.getByCustomerId(customer.getId()).stream().map(p -> p.getId()).collect(Collectors.toList()));
                }
        );

    }

    @SneakyThrows
    private void buyProduct() {

        Customer customer = dataGenerator.getRandomCustomer();
        List<Long> productIds = dataGenerator.getRandomListProduct();

        String[] ids = productIds.stream()
                .map(String::valueOf)
                .toArray(String[]::new);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/products/buy")
                .param(PRODUCT_IDS, ids)
                .param(CUSTOMER_ID, String.valueOf(customer.getId())))
                .andReturn();

    }
}
