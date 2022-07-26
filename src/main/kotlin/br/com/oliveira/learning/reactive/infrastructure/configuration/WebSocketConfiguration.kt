package br.com.oliveira.learning.reactive.infrastructure.configuration

import br.com.oliveira.learning.reactive.domain.entity.Person
import br.com.oliveira.learning.reactive.infrastructure.publisher.ProfileCreatedEventPublisher
import br.com.oliveira.learning.reactive.domain.entity.Profile
import br.com.oliveira.learning.reactive.infrastructure.publisher.PersonCreatedEventPublisher
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter
import reactor.core.publisher.Flux
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors


@Configuration
class WebSocketConfiguration() {

    val log: Logger = LogManager.getLogger(WebSocketConfiguration::class.java)

    @Bean
    fun handlerMapping(@Qualifier("profileSocketHandler") profileWebSocketHandler: WebSocketHandler,
                       @Qualifier("personSocketHandler") personWebSocketHandler: WebSocketHandler) : HandlerMapping {
        return object : SimpleUrlHandlerMapping() {
            init {
                // <3>
                val map = mutableMapOf<String, WebSocketHandler>()
                map["/ws/persons"] = personWebSocketHandler
                map["/ws/profiles"] = profileWebSocketHandler

                urlMap = map
                order = 10
            }
        }
    }

    @Bean
    fun webSocketHandlerAdapter(): WebSocketHandlerAdapter = WebSocketHandlerAdapter()

    @Bean("profileSocketHandler")
    fun profileWebSocketHandler(
        objectMapper: ObjectMapper,  // <5>
        eventPublisher: ProfileCreatedEventPublisher // <6>
    ): WebSocketHandler {
        val publish = Flux.create(eventPublisher).share() // <7>
        return WebSocketHandler { session: WebSocketSession ->

            println("profileSocketHandler")
            val messageFlux =
                publish.map { evt: ProfileCreatedEvent ->
                    try {
                        val (id) = evt.source as Profile // <1>
                        val data = mutableMapOf<String,String>()
                        data["id"] = id!!
                        println(data)
                        return@map objectMapper.writeValueAsString(data) // <3>
                    } catch (e: JsonProcessingException) {
                        throw RuntimeException(e)
                    }
                }
                    .map { str: String ->
                        log.info("sending $str")
                        session.textMessage(str)
                    }
            session.send(messageFlux)
        }
    }


    @Bean("personSocketHandler")
    fun personWebSocketHandler(
        objectMapper: ObjectMapper,  // <5>
        eventPublisher: PersonCreatedEventPublisher // <6>
    ): WebSocketHandler {
        val publish = Flux.create(eventPublisher).share() // <7>
        return WebSocketHandler { session: WebSocketSession ->

            println("personSocketHandler")
            val messageFlux =
                publish.map { evt: PersonCreatedEvent ->
                    try {
                        val (id) = evt.source as Person // <1>
                        val data = mutableMapOf<String,String>()
                        data["id"] = id!!
                        println(data)
                        return@map objectMapper.writeValueAsString(data) // <3>
                    } catch (e: JsonProcessingException) {
                        throw RuntimeException(e)
                    }
                }
                    .map { str: String ->
                        log.info("sending $str")
                        session.textMessage(str)
                    }
            session.send(messageFlux)
        }
    }
}