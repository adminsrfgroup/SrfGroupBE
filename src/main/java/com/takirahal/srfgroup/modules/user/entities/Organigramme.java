package com.takirahal.srfgroup.modules.user.entities;

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
@Table(name = "sg_organigramme")
public class Organigramme  implements Serializable {

    @Id
    @SequenceGenerator(name = "sequenceGenerator", sequenceName = "sequence_name_user", allocationSize = 1, initialValue = 2)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    private Long id;

    @Lob
    @JdbcTypeCode(Types.LONGVARCHAR)
    @Column(name = "content", columnDefinition="TEXT")
    private String content;
}
