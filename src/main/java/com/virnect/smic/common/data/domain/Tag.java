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
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter @Setter
@Builder
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

	@Column(name = "sub2_category")
	private String sub2Category;
	private String description;
	private String content;

	@ColumnDefault("200")
	private int scanRateMs;

	private String etc;

	@Enumerated(EnumType.STRING)
	private MainCategory mainCategory;

	private String subCategory;

	private String dataType;

	private String deviceDataType;

	private String unit;

	private String dataRange;

	private String queueName;

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
			", sub2Category='" + sub2Category + '\'' +
			", description='" + description + '\'' +
			", content='" + content + '\'' +
			", scanRateMs=" + scanRateMs +
			", etc='" + etc + '\'' +
			", mainCategory=" + mainCategory +
			", subCategory='" + subCategory + '\'' +
			", dataType='" + dataType + '\'' +
			", deviceDataType='" + deviceDataType + '\'' +
			", unit='" + unit + '\'' +
			", dataRange='" + dataRange + '\'' +
			", queueName='" + queueName + '\'' +
			", modelLine=" + modelLine +
			'}';
	}
}
