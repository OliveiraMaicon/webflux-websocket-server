package br.com.oliveira.learning.reactive.infrastructure.configuration

import br.com.oliveira.learning.reactive.domain.entity.Profile
import org.springframework.context.ApplicationEvent

class ProfileCreatedEvent(profile: Profile) : ApplicationEvent(profile) {
}