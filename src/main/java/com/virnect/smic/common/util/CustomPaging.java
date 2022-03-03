package com.virnect.smic.common.util;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CustomPaging {
    private int currentPage;
	private int totalPage;
	private long totalElements;
	private int size;
	private boolean last;
	private int startIndex;
	private int endIndex;
	private int numberOfElements;
}
