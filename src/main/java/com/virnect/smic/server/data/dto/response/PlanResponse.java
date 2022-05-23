package com.virnect.smic.server.data.dto.response;

import java.util.List;

import com.google.gson.JsonObject;

import lombok.Builder;
import lombok.Data;

@Data
public class PlanResponse {
	private boolean isCompressed;

	private JsonObject dataShape;
	private List<Row> rows;

	@Data
	public static class Row {
		/*
	  "plan_cd": 19914,
      "plan_name": "SMIC_현장주문",
      "plan_date": 1650466800000,
      "add_count": 0,
      "auto_operation": "1",
      "plan_cat": "0",
      "ins_date": 1650499718200,
      "upt_date": 1650499718200,
      "plan_progress": "1"
		* */
		private int plan_cd;
		private String plan_name;
		private long plan_date;
		private int add_count;
		private String auto_operation;
		private String plan_cat;
		private long ins_date;
		private long upt_date;
		private String plan_progress;
	}
}
