package com.takirahal.srfgroup.modules.suggestion.controllers;

import com.takirahal.srfgroup.modules.offer.dto.filter.OfferFilter;
import com.takirahal.srfgroup.modules.suggestion.entities.Post;
import com.takirahal.srfgroup.modules.suggestion.services.PostService;
import com.takirahal.srfgroup.utils.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post/")
public class PostController {

    private final Logger log = LoggerFactory.getLogger(PostController.class);

    @Autowired
    PostService postService;

    @PostMapping("public/create")
    public ResponseEntity<Post> createPost(@RequestBody Post post){
        log.debug("REST request to save Post : {}", post);
        Post result = postService.save(post);
        return new ResponseEntity<>(result, HeaderUtil.createAlert("add_offer.message_create_offer_succefull", ""), HttpStatus.CREATED);
    }

    @GetMapping("public/suggestions")
    public ResponseEntity<List<Post>> suggestionList(Post post, Pageable pageable) {
        log.info("REST request to get Posts by criteria: {}", pageable);
        List<Post> page = postService.fetchSuggestions(post, pageable);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @GetMapping("public/search")
    public ResponseEntity<List<Post>> searchPosts(Post post, Pageable pageable) {
        log.info("REST request to search Posts by criteria: {}", pageable);
        List<Post> page = postService.searchPosts(post, pageable);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @GetMapping("public/fetchAll")
    public ResponseEntity<Page<Post>> getAllPosts(Pageable pageable) {
        log.info("REST request to get Posts by criteria: {}", pageable);
        Page<Post> page = postService.getAllPosts(pageable);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }
}
