package br.com.oliveira.learning.reactive.infrastructure.publisher

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
class ProfileCreatedEventPublisher() : ApplicationListener<ProfileCreatedEvent>, Consumer<FluxSink<ProfileCreatedEvent>> {

    private val executor: Executor = Executors.newSingleThreadExecutor()

    private val queue: BlockingQueue<ProfileCreatedEvent> = LinkedBlockingQueue()

    override fun onApplicationEvent(event: ProfileCreatedEvent) {
        queue.offer(event)
    }

    override fun accept(sink: FluxSink<ProfileCreatedEvent>) {
        Executors.newSingleThreadScheduledExecutor()
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