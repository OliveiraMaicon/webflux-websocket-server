package br.com.oliveira.learning.reactive.infrastructure.configuration

import org.springframework.http.server.PathContainer
import org.springframework.web.reactive.function.server.RequestPredicate
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.support.ServerRequestWrapper
import java.net.URI
import java.util.*


class CaseInsensitiveRequestPredicate(private val target : RequestPredicate) : RequestPredicate {

    override fun test(request: ServerRequest): Boolean { // <1>
        return target.test(LowerCaseUriServerRequestWrapper(request))
    }

    override fun toString(): String {
        return target.toString()
    }
}

internal class LowerCaseUriServerRequestWrapper(delegate: ServerRequest) : ServerRequestWrapper(delegate) {
    override fun uri(): URI {
        return URI.create(super.uri().toString().lowercase(Locale.getDefault()))
    }

    override fun path(): String {
        return uri().rawPath
    }

    override fun pathContainer(): PathContainer {
        return PathContainer.parsePath(path())
    }
}