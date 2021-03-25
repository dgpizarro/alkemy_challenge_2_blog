package com.alkemy.challenge2.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alkemy.challenge2.entity.Post;
import com.alkemy.challenge2.service.PostService;
import com.alkemy.challenge2.util.FileUploadUtil;

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
    public String goDetails(@PathVariable int id, Model m) {
        Optional<Post> post_find = ps.getPostById(id);
        if (post_find.isPresent()) {
            m.addAttribute("post", post_find.get());
            return "details";
        }
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
                String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                p.setImage(fileName);
                Post savedPostWithPhoto = ps.addEditPost(p);

                String uploadDir = "post-photos/" + savedPostWithPhoto.getId();
                FileUploadUtil.saveFile(uploadDir, fileName, file);
                
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

}
