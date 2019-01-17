package org.eclipse.microprofile.reactive.messaging.tck.signatures.processors;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.eclipse.microprofile.reactive.streams.operators.ProcessorBuilder;
import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;

import javax.enterprise.context.ApplicationScoped;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@ApplicationScoped
public class ProcessorBean {

  private Map<String, List<String>> collector = new ConcurrentHashMap<>();

  private static final List<String> EXPECTED = Arrays.asList(
    "1", "1",
    "2", "2",
    "3", "3",
    "4", "4",
    "5", "5",
    "6", "6",
    "7", "7",
    "8", "8",
    "9", "9",
    "10", "10"
  );

  @Outgoing("publisher-for-processor-message")
  public PublisherBuilder<Integer> streamForProcessorOfMessages() {
    return ReactiveStreams.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
  }

  @Outgoing("publisher-for-processor-payload")
  public PublisherBuilder<Integer> streamForProcessorOfPayloads() {
    return ReactiveStreams.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
  }

  @Outgoing("publisher-for-processor-builder-message")
  public PublisherBuilder<Integer> streamForProcessorBuilderOfMessages() {
    return ReactiveStreams.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
  }

  @Outgoing("publisher-for-processor-builder-payload")
  public PublisherBuilder<Integer> streamForProcessorBuilderOfPayloads() {
    return ReactiveStreams.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
  }

  @Incoming("processor-message")
  public void getMessgesFromProcessorOfMessages(String value) {
    add("processor-message", value);
  }

  @Incoming("processor-payload")
  public void getMessgesFromProcessorOfPayloads(String value) {
    add("processor-payload", value);
  }

  @Incoming("processor-builder-message")
  public void getMessgesFromProcessorBuilderOfMessages(String value) {
    add("processor-builder-message", value);
  }

  @Incoming("processor-builder-payload")
  public void getMessgesFromProcessorBuilderOfPayloads(String value) {
    add("processor-builder-payload", value);
  }

  @Incoming("publisher-for-processor-message")
  @Outgoing("processor-message")
  public Processor<Message<Integer>, Message<String>> processorOfMessages() {
    return ReactiveStreams.<Message<Integer>>builder()
      .map(Message::getPayload)
      .map(i -> i + 1)
      .flatMap(i -> ReactiveStreams.of(i, i))
      .map(i -> Integer.toString(i))
      .map(Message::of)
      .buildRs();
  }

  @Incoming("publisher-for-processor-payload")
  @Outgoing("processor-payload")
  public Processor<Integer, String> processorOfPayloads() {
    return ReactiveStreams.<Integer>builder()
      .map(i -> i + 1)
      .flatMap(i -> ReactiveStreams.of(i, i))
      .map(i -> Integer.toString(i))
      .buildRs();
  }

  @Incoming("publisher-for-processor-builder-message")
  @Outgoing("processor-builder-message")
  public ProcessorBuilder<Message<Integer>, Message<String>> processorBuilderOfMessages() {
    return ReactiveStreams.<Message<Integer>>builder()
      .map(Message::getPayload)
      .map(i -> i + 1)
      .flatMap(i -> ReactiveStreams.of(i, i))
      .map(i -> Integer.toString(i))
      .map(Message::of);
  }

  @Incoming("publisher-for-processor-builder-payload")
  @Outgoing("processor-builder-payload")
  public ProcessorBuilder<Integer, String> processorBuilderOfPayloads() {
    return ReactiveStreams.<Integer>builder()
      .map(i -> i + 1)
      .flatMap(i -> ReactiveStreams.of(i, i))
      .map(i -> Integer.toString(i));
  }

  private void add(String key, String value) {
    collector.computeIfAbsent(key, x -> new CopyOnWriteArrayList<>()).add(value);
  }

  void verify() {
    assertThat(collector).hasSize(4).allSatisfy((k, v) -> assertThat(v).containsExactlyElementsOf(EXPECTED));
  }

}