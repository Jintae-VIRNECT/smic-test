package com.virnect.smic.common.data.dao;

import java.util.List;

import com.virnect.smic.common.data.domain.Tag;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {

	List<Tag> findByModelLineId(Long modelLineId);

}
