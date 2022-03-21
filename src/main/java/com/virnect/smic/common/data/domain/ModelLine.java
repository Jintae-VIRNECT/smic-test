package com.virnect.smic.common.data.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter @Setter
@Table(name = "model_line")
public class ModelLine {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "model_line_id")
	private Long id;

	@Column(unique = true)
	private String name;

	private String description;

	@OneToMany(mappedBy = "modelLine")//, cascade = CascadeType.ALL)
	private List<Tag> tags = new ArrayList<>();

	// @OneToOne(mappedBy = "model_line")
	// private TaskExecution taskExecution;
}
