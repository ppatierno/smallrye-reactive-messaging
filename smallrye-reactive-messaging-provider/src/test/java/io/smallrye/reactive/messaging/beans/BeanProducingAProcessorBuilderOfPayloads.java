package io.smallrye.reactive.messaging.beans;

import io.reactivex.Flowable;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.eclipse.microprofile.reactive.streams.ProcessorBuilder;
import org.eclipse.microprofile.reactive.streams.ReactiveStreams;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BeanProducingAProcessorBuilderOfPayloads {

  @Incoming("count")
  @Outgoing("sink")
  public ProcessorBuilder<Integer, String> process() {
    return ReactiveStreams.<Integer>builder()
      .map(i -> i + 1)
      .flatMapPublisher(i -> Flowable.just(i, i))
      .map(i -> Integer.toString(i));
  }

}