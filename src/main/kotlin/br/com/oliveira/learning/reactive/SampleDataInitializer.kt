package br.com.oliveira.learning.reactive

import br.com.oliveira.learning.reactive.domain.entity.Person
import br.com.oliveira.learning.reactive.domain.entity.Profile
import br.com.oliveira.learning.reactive.domain.repository.PersonRepository
import br.com.oliveira.learning.reactive.domain.repository.ProfileRepository
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import java.util.*


@Component
class SampleDataInitializer(val profileRepository : ProfileRepository, val personRepository : PersonRepository, val  log : Logger = LogManager.getLogger(SampleDataInitializer::class.java)) : ApplicationListener<ApplicationReadyEvent>{
    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        profileRepository
            .deleteAll()
            .thenMany(
                Flux
                .just("A","B","C","D")
                    .map { name -> Profile(UUID.randomUUID().toString(), "$name@gmail.com") }
                    .flatMap(profileRepository::save)
            )
            .thenMany(profileRepository.findAll())
            .subscribe{profile -> log.info("Saving $profile")}


        personRepository
            .deleteAll()
            .thenMany(
                Flux
                    .just("Carla","Andre")
                    .map { name -> Person(UUID.randomUUID().toString(), name) }
                    .flatMap(personRepository::save)
            )
            .thenMany(personRepository.findAll())
            .subscribe{person -> log.info("Saving $person")}
    }
}