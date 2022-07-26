package br.com.oliveira.learning.reactive.view

import br.com.oliveira.learning.reactive.domain.entity.Profile
import br.com.oliveira.learning.reactive.domain.service.ProfileService
import org.reactivestreams.Publisher
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.net.URI

@RestController
@RequestMapping("/profiles")
@org.springframework.context.annotation.Profile("classic")
class ProfileController(val profileService: ProfileService) {


    @GetMapping
    @CrossOrigin(origins = ["http://localhost:3000"])
    fun getAll() : Publisher<Profile>{
        return profileService.all()
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id : String) : Publisher<Profile> {
        return profileService.get(id)
    }

    @PostMapping
    fun create(@RequestBody profile: Profile) : Publisher<ResponseEntity<Profile>>{
        return profileService.create(profile.email)
            .map { p -> ResponseEntity.created(URI.create("/profiles/${p.id}"))
                .contentType(MediaType.APPLICATION_JSON)
                .build() }
    }

    @DeleteMapping("/{id}")
    fun deleteById(@PathVariable id: String) : Publisher<Profile> {
        return profileService.delete(id)
    }

    @PutMapping("/{id}")
    fun updateById(@PathVariable id: String, @RequestBody profile: Profile) : Publisher<ResponseEntity<Profile>>{
        return Mono
            .just(profile)
            .flatMap { p -> profileService.update(id, p.email) }
            .map { p -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                .build()
            }
    }



}