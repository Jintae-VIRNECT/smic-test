package com.virnect.smic.common.util;

import com.virnect.smic.server.data.dto.response.PageMetadataResponse;

import org.springframework.data.domain.Pageable;

public class PagingUtils {
    public static PageMetadataResponse emptyPagingBuilder() {
		return PageMetadataResponse.builder()
			.currentPage(1)
			.currentSize(1)
			.numberOfElements(0)
			.totalPage(0)
			.totalElements(0)
			.last(true)
			.build();
	}

	public static PageMetadataResponse pagingBuilder(
		boolean paging,
		Pageable pageable,
		int numberOfElements,
		int totalPage,
		long totalElements,
		boolean last
	) {
		if (paging) {
			return usePagingBuilder(pageable, numberOfElements, totalPage, totalElements, last);
		} else {
			return notUsePagingBuilder(totalElements);
		}
	}

	private static PageMetadataResponse usePagingBuilder(
		Pageable pageable,
		int numberOfElements,
		int totalPage,
		long totalElements,
		boolean last
	) {
		return PageMetadataResponse.builder()
			.currentPage(pageable.getPageNumber())
			.currentSize(pageable.getPageSize())
			.numberOfElements(numberOfElements)
			.totalPage(totalPage)
			.totalElements(totalElements)
			.last(last)
			.build();
	}

	private static PageMetadataResponse notUsePagingBuilder(long totalElements) {
		return PageMetadataResponse.builder()
			.currentPage(1)
			.currentSize(1)
			.numberOfElements((int)totalElements)
			.totalPage(1)
			.totalElements(totalElements)
			.last(true)
			.build();
	}

	public static CustomPaging customPaging(
		int page,
		int totalElements,
		int pagingSize,
		boolean listEmpty
	) {

		int currentPage = page + 1; // current page number (start : 0)
		int totalPage = totalElements % pagingSize == 0 ? (totalElements / (pagingSize)) : (totalElements / (pagingSize)) + 1;
		boolean last = (currentPage) == totalPage;

		int startIndex = 0;
		int endIndex = 0;
		if (!listEmpty) {
			if (pagingSize > totalElements) {
				endIndex = totalElements;
			} else {
				startIndex = (currentPage - 1) * pagingSize;
				endIndex = last ? totalElements : ((currentPage - 1) * pagingSize) + (pagingSize);
			}
		}

		return CustomPaging.builder()
			.currentPage(page)
			.totalPage(totalPage)
			.totalElements(totalElements)
			.size(pagingSize)
			.last(listEmpty)
			.startIndex(startIndex)
			.endIndex(endIndex)
			.build();
	}

	public static PageMetadataResponse customPagingBuilder(
		CustomPaging customPaging
	) {
		return PageMetadataResponse.builder()
			.currentPage(customPaging.getCurrentPage())
			.currentSize(customPaging.getSize())
			.numberOfElements(customPaging.getNumberOfElements())
			.totalPage(customPaging.getTotalPage())
			.totalElements(customPaging.getTotalElements())
			.last(customPaging.isLast())
			.build();
	}
}
