package br.com.oliveira.learning.reactive.domain.entity

import lombok.Data
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
@Data
data class Profile(
    @Id
    val id: String?,
    val email: String,) {
}