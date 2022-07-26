package br.com.oliveira.learning.reactive.domain.service

import br.com.oliveira.learning.reactive.infrastructure.configuration.ProfileCreatedEvent
import br.com.oliveira.learning.reactive.domain.entity.Profile
import br.com.oliveira.learning.reactive.domain.repository.ProfileRepository
import org.apache.logging.log4j.LogManager
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.apache.logging.log4j.Logger
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class ProfileService(val publisher: ApplicationEventPublisher,
                     val repository: ProfileRepository,
                     val log: Logger = LogManager.getLogger(ProfileService::class.java)) {

    fun all() : Flux<Profile>{
        return repository.findAll()
    }

    fun get(id: String) : Mono<Profile>{
        return repository.findById(id)
    }

    fun update(id: String, email: String) : Mono<Profile>{
        return repository.findById(id)
            .map { p -> Profile(p.id, email) }
            .flatMap(repository::save)
    }

    fun delete(id: String) : Mono<Profile> {
        return repository.findById(id)
            .flatMap { p -> repository.deleteById(p.id!!).thenReturn(p)}
    }

    fun create(email: String) : Mono<Profile> {
        return repository.save(Profile(UUID.randomUUID().toString(),email))
            .doOnSuccess{ entity -> publisher.publishEvent(ProfileCreatedEvent(entity))}
    }
}