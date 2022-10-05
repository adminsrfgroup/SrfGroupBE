package com.takirahal.srfgroup.modules.suggestion.repositories;

import com.takirahal.srfgroup.modules.suggestion.entities.SuggestSearch;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuggestSearchRepository  extends ElasticsearchRepository<SuggestSearch, String> {
}
