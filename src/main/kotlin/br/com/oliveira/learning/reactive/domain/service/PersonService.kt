package br.com.oliveira.learning.reactive.domain.service

import br.com.oliveira.learning.reactive.domain.entity.Person
import br.com.oliveira.learning.reactive.domain.repository.PersonRepository
import br.com.oliveira.learning.reactive.infrastructure.configuration.PersonCreatedEvent
import org.apache.logging.log4j.LogManager
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.apache.logging.log4j.Logger
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class PersonService(val publisher: ApplicationEventPublisher,
                    val repository: PersonRepository) {

    val log: Logger = LogManager.getLogger(PersonService::class.java)


    fun all() : Flux<Person>{
        return repository.findAll()
    }

    fun get(id: String) : Mono<Person>{
        return repository.findById(id)
    }

    fun update(id: String, email: String) : Mono<Person>{
        return repository.findById(id)
            .map { p -> Person(p.id, email) }
            .flatMap(repository::save)
    }

    fun delete(id: String) : Mono<Person> {
        return repository.findById(id)
            .flatMap { p -> repository.deleteById(p.id!!).thenReturn(p)}
    }

    fun create(email: String) : Mono<Person> {
        return repository.save(Person(UUID.randomUUID().toString(),email))
            .doOnSuccess{ entity -> publisher.publishEvent(PersonCreatedEvent(entity))}
    }
}