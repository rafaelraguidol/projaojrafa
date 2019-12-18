package br.com.fiapaoj.pedidoservice;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
public class KafkaConfig {

	public static final String BOOTSTRAP_ADDRESS = "localhost:9092";

	public static final String GROUP_ID = "entregas";

	public static final String TOPICO = "atualizarPrazos";

	@Bean
	public KafkaAdmin kafkaAdmin() {
		Map<String, Object> configs = new HashMap<>();
		configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_ADDRESS);
		return new KafkaAdmin(configs);
	}

	@Bean
	public NewTopic topicoEntrega() {
		return new NewTopic(TOPICO, 1, (short) 1);
	}

	@Bean
	public ConsumerFactory<String, EntregaSlaDto> consumerFactory() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_ADDRESS);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		
		JsonDeserializer<EntregaSlaDto> deserializer = new JsonDeserializer<>(EntregaSlaDto.class);
//	    deserializer.setRemoveTypeHeaders(false);
	    deserializer.addTrustedPackages("br.com.fiapaoj");
	    deserializer.setUseTypeMapperForKey(true);
	    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

		return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, EntregaSlaDto> kafkaListenerContainerFactory() {

		ConcurrentKafkaListenerContainerFactory<String, EntregaSlaDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());
		return factory;
	}

}
