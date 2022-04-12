package com.virnect.smic.common.data.dao;

import java.util.List;

import com.virnect.smic.common.data.domain.Tag;
import com.virnect.smic.common.data.dto.TagDto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TagRepository extends JpaRepository<Tag, Long> {

	List<Tag> findByModelLineId(Long modelLineId);

	String sql ="select t.node_id as nodeId, t.model_line_id as modelLineId, "
		+ "concat(m.name, '.', t.main_category, '.', t.sub_category, "
		+ "COALESCE(concat('.', t.sub2_category), ''), "
		+ "COALESCE(concat('.', t.etc), '' ) "
		+ ") as queueName "
		+ "from Tag as t, Model_line as m "
		+ "where t.activated = 1 "
		+ "and t.model_line_id = m.model_line_id";
	@Query(value =sql, nativeQuery = true)
	List<TagDto> findNodeIdQueueNameModelLineIdByActivated();

}
