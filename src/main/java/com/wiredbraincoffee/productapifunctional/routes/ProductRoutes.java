package com.wiredbraincoffee.productapifunctional.routes;

import com.wiredbraincoffee.productapifunctional.handler.ProductHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ProductRoutes {
    @Bean
    RouterFunction<ServerResponse> routes(ProductHandler handler) {
        //In this way there are some repetitions, but maybe it is clearer what the code does.

        return RouterFunctions.route()
                .GET("/products/events", accept(MediaType.TEXT_EVENT_STREAM), handler::getProductEvents)
                .GET("/products/{id}", accept(MediaType.APPLICATION_JSON), handler::getProduct)
                .GET("/products", accept(MediaType.APPLICATION_JSON), handler::getAllProducts)
                .PUT("/products/{id}", accept(MediaType.APPLICATION_JSON), handler::updateProduct)
                .POST("/products", accept(MediaType.APPLICATION_JSON), handler::saveProduct)
                .DELETE("/products/{id}", accept(MediaType.APPLICATION_JSON), handler::deleteProduct)
                .DELETE("/products", accept(MediaType.APPLICATION_JSON), handler::deleteAllProducts)
                .build();



        //As an alternative it is possible to write the same thing in this way for avoid repetitions.
        //Here it is used a nest path builder
		/*
		return RouterFunctions.route()
				.path("/products",
						builder -> builder
								.nest(accept(MediaType.APPLICATION_JSON).or(accept(MediaType.TEXT_EVENT_STREAM)),
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
