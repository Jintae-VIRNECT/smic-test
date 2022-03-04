package com.virnect.smic.daemon.mq.kafka;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.virnect.smic.common.data.dao.TagRepository;
import com.virnect.smic.daemon.mq.TopicManager;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Sort;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Setter @Getter
@RequiredArgsConstructor
public class KafkaTopicManager implements TopicManager {

	private final Environment env;
	private final TagRepository tagRepository;

	//private List<String> tags;

	// public KafkaTopicManager(TagRepository tagRepository, Environment env) {
	// 	super(tagRepository);
	// 	this.env = env;
	// }

	public void create() throws ExecutionException, InterruptedException {
		Properties config = new Properties();
		config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
			env.getProperty("kafka.host")+ ":"+ env.getProperty("kafka.port"));
		config.put(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_COMPACT);
		config.put(TopicConfig.COMPRESSION_TYPE_CONFIG, "gzip");

		AdminClient admin = AdminClient.create(config);
		ListTopicsResult listTopics = admin.listTopics();
		Set<String> names = listTopics.names().get();

		List<NewTopic> topicList = new ArrayList<NewTopic>();

		List<String> tags = getTagList();

		tags.forEach(item->{
			boolean contains = names.contains(item);
			if(!contains){

				Map<String, String> configs = new HashMap<String, String>();
				int partition = 1;
				short replication = 1;
				NewTopic newTopic = new NewTopic(item.replaceAll(" ", ""), partition, replication)
					.configs(configs);
				topicList.add(newTopic);

			}
		});

		CreateTopicsResult result = admin.createTopics(topicList);
		log.info("result");
	}

	private List<String> getTagList(){
		return tagRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
			.stream()
			.map(o-> o.getNodeId())
			.collect(Collectors.toList());

	}
}
