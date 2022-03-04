package com.virnect.smic.daemon.mq.kafka;


import java.io.IOException;
import java.util.Properties;

import com.virnect.smic.daemon.mq.ProducerManager;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.core.env.Environment;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter @Setter
public class KafkaProducerManager implements ProducerManager {

	//private static Environment env;
	private static Producer<Long, String> producer;

	public KafkaProducerManager() {
		//this.env = env;
		producer = createKafkaProducer();
		
	}

	private static Producer<Long, String> createKafkaProducer() {
		Properties props = new Properties();
		props.put(
			ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
			//env.getProperty("kafka.host")+ ":"+ env.getProperty("kafka.port"));
			"localhost:9092");
		props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaProducerTest1");
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
			LongSerializer.class.getName());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
			StringSerializer.class.getName());
		return new KafkaProducer<>(props);
	}

	public void runProducer(final int sendMessageCount, String topic, String value) throws IOException {
		
		long time = System.currentTimeMillis();

		try {
			for (long index = time; index < time + sendMessageCount; index++) {
				final ProducerRecord<Long, String> record =
					new ProducerRecord<>(topic, index,
						value );

				RecordMetadata metadata = producer.send(record).get();

				long elapsedTime = System.currentTimeMillis() - time;
				log.debug("sent record(key=%s value=%s) " +
						"meta(partition=%d, offset=%d) time=%d\n",
					record.key(), record.value(), metadata.partition(),
					metadata.offset(), elapsedTime);

			}
		}catch (Exception e){
			e.printStackTrace();
		}
		// finally {
		// 	producer.flush();
		// 	producer.close();
		// }
	}

}
