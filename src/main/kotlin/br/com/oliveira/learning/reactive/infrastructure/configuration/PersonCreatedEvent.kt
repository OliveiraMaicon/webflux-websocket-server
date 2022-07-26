package br.com.oliveira.learning.reactive.infrastructure.configuration

import br.com.oliveira.learning.reactive.domain.entity.Person
import org.springframework.context.ApplicationEvent

class PersonCreatedEvent(person: Person) : ApplicationEvent(person)