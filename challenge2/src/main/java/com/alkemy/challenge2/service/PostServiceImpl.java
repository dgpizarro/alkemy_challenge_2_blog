package com.alkemy.challenge2.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alkemy.challenge2.entity.Post;
import com.alkemy.challenge2.repository.PostRepository;

@Service
public class PostServiceImpl implements PostService {
    
    @Autowired
    private PostRepository repo;

    @Override
    public List<Post> getPosts() {
        return repo.findByOrderByCreationDateDesc();
    }

    @Override
    public Post addEditPost(Post p) {
       return repo.save(p);
    }

    @Override
    public Optional<Post> getPostById(int id) {
        return repo.findById(id);
    }

    @Override
    public void deletePostById(int id) {
       repo.deleteById(id);
    }

}
