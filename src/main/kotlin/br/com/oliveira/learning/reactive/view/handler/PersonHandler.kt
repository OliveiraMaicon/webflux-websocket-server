package br.com.oliveira.learning.reactive.view.handler

import br.com.oliveira.learning.reactive.domain.entity.Person
import br.com.oliveira.learning.reactive.domain.service.PersonService
import org.reactivestreams.Publisher
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.net.URI


@Component
class PersonHandler(val personService: PersonService) {

    // <2>
    fun getById(r: ServerRequest): Mono<ServerResponse> {
        return defaultReadResponse(this.personService.get(id(r)))
    }

    fun all(r: ServerRequest): Mono<ServerResponse> {
        println("entrou")
        return defaultReadResponse(this.personService.all())
    }

    fun deleteById(r: ServerRequest): Mono<ServerResponse> {
        return defaultReadResponse(this.personService.delete(id(r)))
    }

    fun updateById(r: ServerRequest): Mono<ServerResponse> {
        val id = r.bodyToFlux(
            Person::class.java
        ).flatMap { (_, email): Person ->
                this.personService.update(
                    id(r),
                    email
                )
            }
        return defaultReadResponse(id)
    }

    fun create(request: ServerRequest): Mono<ServerResponse> {
        val flux = request
            .bodyToFlux(Person::class.java)
            .flatMap { (_, email): Person ->
                this.personService.create(
                    email
                )
            }
        return defaultWriteResponse(flux)
    }

    // <3>
    private fun defaultWriteResponse(profiles: Publisher<Person>): Mono<ServerResponse> {
        return Mono
            .from(profiles)
            .flatMap { p  ->
                ServerResponse
                    .created(URI.create("/persons/" + p.id))
                    .contentType(MediaType.APPLICATION_JSON)
                    .build()
            }
    }

    // <4>
    private fun defaultReadResponse(profiles: Publisher<Person>): Mono<ServerResponse> {
        return ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body<Person, Publisher<Person>>(
                profiles,
                Person::class.java
            )
    }

    private fun id(r: ServerRequest): String {
        return r.pathVariable("id")
    }
}