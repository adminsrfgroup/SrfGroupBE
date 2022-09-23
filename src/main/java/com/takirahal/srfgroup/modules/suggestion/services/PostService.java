package com.takirahal.srfgroup.modules.suggestion.services;

import com.takirahal.srfgroup.modules.suggestion.entities.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostService {

    List<Post> fetchSuggestions(Post post, Pageable pageable);

    List<Post> searchPosts(Post post, Pageable pageable);

    Post save(Post post);

    Page<Post> getAllPosts(Pageable pageable);
}
