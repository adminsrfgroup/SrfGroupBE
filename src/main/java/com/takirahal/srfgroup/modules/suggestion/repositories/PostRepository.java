package com.takirahal.srfgroup.modules.suggestion.repositories;

import com.takirahal.srfgroup.modules.suggestion.entities.Post;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PostRepository  extends ElasticsearchRepository<Post, String>{
}
