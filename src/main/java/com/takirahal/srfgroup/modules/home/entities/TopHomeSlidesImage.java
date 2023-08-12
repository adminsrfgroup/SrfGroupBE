package com.takirahal.srfgroup.modules.home.entities;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;

import jakarta.persistence.*;
import java.io.Serializable;
import java.sql.Types;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sg_top_home_slides_image")
public class TopHomeSlidesImage implements Serializable {

    @Id
    @SequenceGenerator(name = "sequenceGeneratorTopHomeSlidesImage", sequenceName = "sequence_name_top_home_slides_images", allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGeneratorTopHomeSlidesImage")
    private Long id;

    @NotBlank
    @Lob
    @JdbcTypeCode(Types.LONGVARCHAR)
    @Column(name = "descriptionAr")
    private String descriptionAr;

    @NotBlank
    @Lob
    @JdbcTypeCode(Types.LONGVARCHAR)
    @Column(name = "descriptionFr")
    private String descriptionFr;

    @NotBlank
    @Lob
    @JdbcTypeCode(Types.LONGVARCHAR)
    @Column(name = "descriptionEn")
    private String descriptionEn;

    @NotBlank
    @Lob
    @JdbcTypeCode(Types.LONGVARCHAR)
    @Column(name = "image_desktop")
    private String imageDesktop;

    @NotBlank
    @Lob
    @JdbcTypeCode(Types.LONGVARCHAR)
    @Column(name = "image_mobile")
    private String imageMobile;

}
