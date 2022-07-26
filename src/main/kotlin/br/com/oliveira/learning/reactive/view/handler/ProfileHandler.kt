package br.com.oliveira.learning.reactive.view.handler

import br.com.oliveira.learning.reactive.domain.service.ProfileService
import br.com.oliveira.learning.reactive.domain.entity.Profile
import org.reactivestreams.Publisher
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.net.URI


@Component
class ProfileHandler(val profileService: ProfileService) {

    // <2>
    fun getById(r: ServerRequest): Mono<ServerResponse> {
        return defaultReadResponse(this.profileService.get(id(r)))
    }

    fun all(r: ServerRequest): Mono<ServerResponse> {
        println("entrou")
        return defaultReadResponse(this.profileService.all())
    }

    fun deleteById(r: ServerRequest): Mono<ServerResponse> {
        return defaultReadResponse(this.profileService.delete(id(r)))
    }

    fun updateById(r: ServerRequest): Mono<ServerResponse> {
        val id = r.bodyToFlux(
            Profile::class.java
        ).flatMap { (_, email): Profile ->
                this.profileService.update(
                    id(r),
                    email
                )
            }
        return defaultReadResponse(id)
    }

    fun create(request: ServerRequest): Mono<ServerResponse> {
        val flux = request
            .bodyToFlux(Profile::class.java)
            .flatMap { (_, email): Profile ->
                this.profileService.create(
                    email
                )
            }
        return defaultWriteResponse(flux)
    }

    // <3>
    private fun defaultWriteResponse(profiles: Publisher<Profile>): Mono<ServerResponse> {
        return Mono
            .from(profiles)
            .flatMap { p  ->
                ServerResponse
                    .created(URI.create("/profiles/" + p.id))
                    .contentType(MediaType.APPLICATION_JSON)
                    .build()
            }
    }

    // <4>
    private fun defaultReadResponse(profiles: Publisher<Profile>): Mono<ServerResponse> {
        return ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body<Profile, Publisher<Profile>>(
                profiles,
                Profile::class.java
            )
    }

    private fun id(r: ServerRequest): String {
        return r.pathVariable("id")
    }
}