package com.virnect.smic.common.data.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.ColumnDefault;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter @Setter
@Table(name = "tag")
public class Tag {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tag_id")
	private Long id;

	private String name;

	@ColumnDefault("2")
	private int namespace;

	@NotBlank
	@Column(unique = true)
	private String nodeId;

	@ColumnDefault("true")
	private boolean activated;

	private String tagCategory;
	private String description;
	private String content;

	@ColumnDefault("200")
	private int scanRateMs;

	private String etc;

	@Enumerated(EnumType.STRING)
	private MainTaskCategory mainTaskCategory;

	private String subCategory;

	private String dataType;

	private String deviceDataType;

	private String unit;

	private String dataRange;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name="model_line_id")
	private ModelLine modelLine;

	@Override
	public String toString() {
		return "Tag{" +
			"id=" + id +
			", name='" + name + '\'' +
			", namespace=" + namespace +
			", nodeId='" + nodeId + '\'' +
			", activated=" + activated +
			", tagCategory='" + tagCategory + '\'' +
			", description='" + description + '\'' +
			", content='" + content + '\'' +
			", scanRateMs=" + scanRateMs +
			", etc='" + etc + '\'' +
			", mainTaskCategory=" + mainTaskCategory +
			", subCategory='" + subCategory + '\'' +
			", dataType='" + dataType + '\'' +
			", deviceDataType='" + deviceDataType + '\'' +
			", unit='" + unit + '\'' +
			", dataRange='" + dataRange + '\'' +
			", modelLine=" + modelLine +
			'}';
	}
}
