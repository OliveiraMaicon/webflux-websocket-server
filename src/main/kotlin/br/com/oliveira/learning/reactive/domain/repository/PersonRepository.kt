package br.com.oliveira.learning.reactive.domain.repository

import br.com.oliveira.learning.reactive.domain.entity.Person
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface PersonRepository : ReactiveMongoRepository<Person, String>