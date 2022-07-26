package br.com.oliveira.learning.reactive.infrastructure.publisher

import br.com.oliveira.learning.reactive.infrastructure.configuration.PersonCreatedEvent
import br.com.oliveira.learning.reactive.infrastructure.configuration.ProfileCreatedEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Configuration
import org.springframework.util.ReflectionUtils
import reactor.core.publisher.FluxSink
import java.util.concurrent.BlockingQueue
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.function.Consumer

@Configuration
class PersonCreatedEventPublisher() : ApplicationListener<PersonCreatedEvent>, Consumer<FluxSink<PersonCreatedEvent>> {

    private val executor: Executor = Executors.newSingleThreadExecutor()
    private val queue: BlockingQueue<PersonCreatedEvent> = LinkedBlockingQueue()

    override fun onApplicationEvent(event: PersonCreatedEvent) {
        queue.offer(event)
    }

    override fun accept(sink: FluxSink<PersonCreatedEvent>) {
        executor.execute{
            while (true){
                try {
                    val event = queue.take()
                    sink.next(event)
                }catch (e : InterruptedException){
                    ReflectionUtils.rethrowRuntimeException(e)
                }
            }
        }
    }
}