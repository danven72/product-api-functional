package com.wiredbraincoffee.productapifunctional;

import com.wiredbraincoffee.productapifunctional.handler.ProductHandler;
import com.wiredbraincoffee.productapifunctional.model.Product;
import com.wiredbraincoffee.productapifunctional.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
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

	@Bean
	RouterFunction<ServerResponse> routes(ProductHandler handler) {
		//In this way there are some repetitions, but maybe it is clearer what the code does.

		return RouterFunctions.route()
				.GET("/products/events", RequestPredicates.accept(MediaType.TEXT_EVENT_STREAM), handler::getProductEvents)
				.GET("/products/{id}", RequestPredicates.accept(MediaType.APPLICATION_JSON), handler::getProduct)
				.GET("/products", RequestPredicates.accept(MediaType.APPLICATION_JSON), handler::getAllProducts)
				.PUT("/products/{id}", RequestPredicates.accept(MediaType.APPLICATION_JSON), handler::updateProduct)
				.POST("/products", RequestPredicates.accept(MediaType.APPLICATION_JSON), handler::saveProduct)
				.DELETE("/products/{id}", RequestPredicates.accept(MediaType.APPLICATION_JSON), handler::deleteProduct)
				.DELETE("/products", RequestPredicates.accept(MediaType.APPLICATION_JSON), handler::deleteAllProducts)
				.build();



		//As an alternative it is possible to write the same thing in this way for avoid repetitions.
		//Here it is used a nest path builder
		/*
		return RouterFunctions.route()
				.path("/products",
						builder -> builder
								.nest(RequestPredicates.accept(MediaType.APPLICATION_JSON).or(RequestPredicates.accept(MediaType.TEXT_EVENT_STREAM)),
										nesterBuilder -> nesterBuilder
												.GET("/events", handler::getProductEvents)
												.GET("/{id}", handler::getProduct)
												.GET(handler::getAllProducts)
												.PUT("/{id}", handler::updateProduct)
												.POST(handler::saveProduct)
								)
								.DELETE("/{id}", handler::deleteProduct)
								.DELETE(handler::deleteProduct)
				)
				.build();

		 */

	}
}
