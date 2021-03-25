package com.alkemy.challenge2.service;

import java.util.List;
import java.util.Optional;

import com.alkemy.challenge2.entity.Post;

public interface PostService {
    
    public abstract List<Post> getPosts();
    public abstract Post addEditPost(Post p);
    public abstract Optional<Post> getPostById(int id);
    public abstract void deletePostById(int id);

}
