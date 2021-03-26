package com.alkemy.challenge2.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alkemy.challenge2.entity.Post;
import com.alkemy.challenge2.service.PostService;


@Controller()
public class PostController {

    @Autowired
    private PostService ps;

    @GetMapping({ "/", "/posts" })
    public String goHome(Model m) {
        m.addAttribute("posts", ps.getPosts());
        return "home";
    }

    @GetMapping("/new_post")
    public String goFormNew(Model m) {
        m.addAttribute("post", new Post());
        return "form";
    }

    @GetMapping("/edit_post/{id}")
    public String goForEdit(@PathVariable int id, Model m, RedirectAttributes redirectAtr) {
        Optional<Post> post_find = ps.getPostById(id);
        if (post_find.isPresent()) {
            m.addAttribute("post", post_find.get());
            return "form";
        }
        redirectAtr.addFlashAttribute("type_alert", "danger");
        redirectAtr.addFlashAttribute("text_alert", "¡Post no encontrado!");
        return "redirect:/posts";
    }

    @GetMapping("/posts/{id}")
    public String goDetails(@PathVariable int id, Model m, RedirectAttributes redirectAtr) {
        Optional<Post> post_find = ps.getPostById(id);
        if (post_find.isPresent()) {
            m.addAttribute("post", post_find.get());
            return "details";
        }
        redirectAtr.addFlashAttribute("type_alert", "danger");
        redirectAtr.addFlashAttribute("text_alert", "¡Post no encontrado!");
        return "redirect:/posts";
    }

    @PostMapping("/posts")
    public String savePost(Post p, final @RequestParam("imageUpload") MultipartFile file, RedirectAttributes redirectAtr) {
        
        if (file.isEmpty()) {
            try {
                ps.addEditPost(p);
                redirectAtr.addFlashAttribute("type_alert", "success");
                redirectAtr.addFlashAttribute("text_alert", "¡Post guardado correctamente!");
                return "redirect:/posts";
            } catch (Exception e) {
                redirectAtr.addFlashAttribute("type_alert", "danger");
                redirectAtr.addFlashAttribute("text_alert", "¡Ocurrió un error al guardar el Post!");
                return "redirect:/posts";
            }  
        } else {
            try {
                String image_name = file.getOriginalFilename();
                p.setImage(file.getBytes());
                p.setImage_name(image_name);
                ps.addEditPost(p);
                redirectAtr.addFlashAttribute("type_alert", "success");
                redirectAtr.addFlashAttribute("text_alert", "¡Post guardado correctamente!");
                return "redirect:/posts";
            } catch (Exception e) {
                redirectAtr.addFlashAttribute("type_alert", "danger");
                redirectAtr.addFlashAttribute("text_alert", "¡Ocurrió un error al guardar el Post!");
                return "redirect:/posts";
            }
        }
    }

    @GetMapping("/delete/{id}")
    public String borrarCurso(@PathVariable int id, RedirectAttributes redirectAtr) {
        try {
            ps.deletePostById(id);
            redirectAtr.addFlashAttribute("type_alert", "success");
            redirectAtr.addFlashAttribute("text_alert", "¡Post eliminado correctamente!");
            return "redirect:/posts";
        } catch (Exception e) {
            redirectAtr.addFlashAttribute("type_alert", "danger");
            redirectAtr.addFlashAttribute("text_alert", "¡Ocurrió un error al eliminar el Post!");
            return "redirect:/posts";
        }
    }
    
    @GetMapping("/post/image/{id}")
    public void showProductImage(@PathVariable int id, HttpServletResponse response) throws IOException {
        response.setContentType("image/jpeg"); 
    
        Optional<Post> post_find = ps.getPostById(id);
        if (post_find.isPresent()) {
            InputStream is = new ByteArrayInputStream(post_find.get().getImage());
            IOUtils.copy(is, response.getOutputStream());
        }
           
    }

}
