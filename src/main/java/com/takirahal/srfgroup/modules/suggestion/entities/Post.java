package com.takirahal.srfgroup.modules.suggestion.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "postindex")
public class Post {

    @Id
    private String id;

    @Field(type = FieldType.Keyword, name = "name")
    private String name;

    @Field(type = FieldType.Keyword, name = "description")
    private String description;

    @Field(type = FieldType.Keyword, name = "module")
    private String module;
}
