package com.virnect.smic.server.data.dto.response.smic;

import java.util.List;

import com.google.gson.JsonObject;

import lombok.Data;

@Data
public class PlanResponse {
	private boolean isCompressed;

	private JsonObject dataShape;
	private List<Row> rows;

	@Data
	public static class Row {
		/*
		"_0": 19884,
		"_1": "SMIC 현장주문",
		"_2": 1648738800000,
		"_3": 0,
		"_4": "1",
		"_5": "0",
		"_6": 1648799663891,
		"_7": 1648799663891,
		"_10": "1"
		* */
		private int _0;
		private String _1;
		private long _2;
		private int _3;
		private String _4;
		private long _6;
		private long _7;
		private Object _8;
		private Object _9;
		private String _10;
	}
}
