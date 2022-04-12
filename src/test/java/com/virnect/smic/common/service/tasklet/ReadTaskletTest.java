package com.virnect.smic.common.service.tasklet;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import ch.qos.logback.classic.Logger;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

import com.virnect.smic.common.data.domain.Tag;
import com.virnect.smic.common.data.dto.TagDto;

@SpringBootTest
class ReadTaskletTest{

	@Autowired
	private ReadTasklet readTasklet;

	private List<TagDto> tags;
	private ConcurrentHashMap<String, String> resultWithErrors;

	private void setErrorResultMap(){
		resultWithErrors = new ConcurrentHashMap<>();
		resultWithErrors.put("080_PACKING.EQUIPMENT.A.R21020", "1");
		resultWithErrors.put("020_KIT_ZONE.QUALITY.INVENTORY_INFO.ROW_2_COLUMN_87","ERR_QUEUE");
		resultWithErrors.put("020_KIT_ZONE.QUALITY.INVENTORY_INFO.ROW_2_COLUMN_6", "8");
	}

	//@Test
	void publishAndLogAsync() {
		setErrorResultMap();

		Logger taskletLogger = (Logger) LoggerFactory.getLogger(ReadTasklet.class);
		ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
		listAppender.start();
		taskletLogger.addAppender(listAppender);

		readTasklet.publishAndLogAsync(resultWithErrors, "TEST001");

		List<ILoggingEvent> logsList = listAppender.list;
		ILoggingEvent iLoggingEvent1 = logsList.stream()
			.filter(log -> log.getFormattedMessage().toString().contains("080_PACKING.EQUIPMENT.A.R21020"))
			.findFirst()
			.get();
		ILoggingEvent iLoggingEvent2 = logsList.stream()
			.filter(log -> log.getFormattedMessage().toString().contains("020_KIT_ZONE.QUALITY.INVENTORY_INFO.ROW_2_COLUMN_87"))
			.findFirst()
			.get();
		ILoggingEvent iLoggingEvent3 = logsList.stream()
			.filter(log -> log.getFormattedMessage().toString().contains("020_KIT_ZONE.QUALITY.INVENTORY_INFO.ROW_2_COLUMN_6"))
			.findFirst()
			.get();

		assertThat(iLoggingEvent1.getFormattedMessage()).contains("COMPLETED");
		assertThat(iLoggingEvent2.getFormattedMessage()).contains("FAILED");
		assertThat(iLoggingEvent3.getFormattedMessage()).contains("COMPLETED");
	}

}