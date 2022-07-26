package br.com.oliveira.learning.reactive.domain.repository

import br.com.oliveira.learning.reactive.domain.entity.Profile
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface ProfileRepository : ReactiveMongoRepository<Profile, String>{
}