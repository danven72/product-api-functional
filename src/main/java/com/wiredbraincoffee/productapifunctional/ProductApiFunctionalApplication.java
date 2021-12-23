package com.wiredbraincoffee.productapifunctional;

import com.wiredbraincoffee.productapifunctional.model.Product;
import com.wiredbraincoffee.productapifunctional.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class ProductApiFunctionalApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductApiFunctionalApplication.class, args);
	}

	@Bean
	CommandLineRunner init(ProductRepository productRepository) {

		return args -> {
			Flux<Product> productFlux = Flux.just(
							new Product(null, "Big Latte", 2.99),
							new Product(null, "Big Decaf", 2.49),
							new Product(null, "Green Tea", 1.99))
					.flatMap(productRepository::save);

			//In this way I can verify what I have saved
			productFlux
					// can't use flatMap because the save operation should not be ended. The operators than
					// and thanMany wait for the first puplisher complete before executing the publisher that it receive as argument.
					//
					.thenMany(productRepository.findAll())
					.subscribe(System.out::println);
		};
	}

}
