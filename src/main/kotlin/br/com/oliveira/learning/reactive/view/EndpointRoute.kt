package br.com.oliveira.learning.reactive.view

import br.com.oliveira.learning.reactive.view.handler.ProfileHandler
import br.com.oliveira.learning.reactive.infrastructure.configuration.CaseInsensitiveRequestPredicate
import br.com.oliveira.learning.reactive.view.handler.PersonHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RequestPredicate
import org.springframework.web.reactive.function.server.RequestPredicates.*
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions.route
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse


@Configuration
class EndpointRoute {

    @Bean
    fun routes(profileHandler: ProfileHandler, personHandler: PersonHandler): RouterFunction<ServerResponse> { // <1>
        return route(i(GET("/profiles"))) { r: ServerRequest -> profileHandler.all(r) } // <2>
            .andRoute(i(GET("/profiles/{id}"))) { r: ServerRequest -> profileHandler.getById(r) }
            .andRoute(i(DELETE("/profiles/{id}"))) { r: ServerRequest -> profileHandler.deleteById(r) } // <3>
            .andRoute(i(POST("/profiles"))) { request: ServerRequest -> profileHandler.create(request) }
            .andRoute(i(PUT("/profiles/{id}"))) { r: ServerRequest -> profileHandler.updateById(r) }
            .andRoute(i(GET("/persons"))) { r: ServerRequest -> personHandler.all(r) }
            .andRoute(i(GET("/persons/{id}"))) { r: ServerRequest -> personHandler.getById(r) }
            .andRoute(i(DELETE("/persons/{id}"))) { r: ServerRequest -> personHandler.deleteById(r) } // <3>
            .andRoute(i(POST("/persons"))) { request: ServerRequest -> personHandler.create(request) }
            .andRoute(i(PUT("/persons/{id}"))) { r: ServerRequest -> personHandler.updateById(r)}
    }

    // <4>
    private fun i(target: RequestPredicate): RequestPredicate {
        return CaseInsensitiveRequestPredicate(target)
    }
}