package com.alkemy.challenge2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alkemy.challenge2.entity.Post;

public interface PostRepository extends JpaRepository<Post, Integer> {
    
    public List<Post> findByOrderByCreationDateDesc();

}
