package com.alkemy.challenge2;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import com.alkemy.challenge2.entity.Post;
import com.alkemy.challenge2.service.PostService;
 
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class Challenge2ApplicationTests {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private PostService ps;
    
    private Post test_post;
    
    @BeforeEach
    public void setUp() throws Exception {
        test_post = new Post();
        test_post.setTitle("Test go home");
        test_post.setBody("Testing");
    }
                
    /**
     * Se prueba petición GET para ir al home y comprobar presencia de model atribute lista de entidad Post, haciendo
     * coincidir los 3 atributos obligatorios de un objeto Post de prueba
     */
    @Test
    public void testGoHome() throws Exception {
        
        Post post_saved = ps.addEditPost(test_post);
        
        this.mockMvc
        .perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(view().name("home"))
        .andExpect(model().attributeExists("posts"))
        .andExpect(model().attribute("posts", isA(Iterable.class)))
        .andExpect(model().attribute("posts",
                        hasItem(allOf(
                            hasProperty("id", is(post_saved.getId())),
                            hasProperty("title", is("Test go home")),                            
                            hasProperty("body", is("Testing"))))))
        .andDo(print());
    }
    
    
    /**
     * Se prueba petición GET para ir al formulario para crear un nuevo objeto Post, se valida envío de model atribute Post()
     */
    @Test
    public void testGoFormNew() throws Exception {       
        this.mockMvc
        .perform(get("/new_post"))
        .andExpect(status().isOk())
        .andExpect(view().name("form"))
        .andExpect(model().attributeExists("post"))
        .andExpect(model().attribute("post", isA(Post.class)))
        .andDo(print());
    }
    
    
    /**
     * Se prueba petición GET para ir al formulario para editar el objeto Post de prueba, 
     * se valida envío de model atribute Post() con los datos del Post de prueba
     */
    @Test
    public void testGoEditPost() throws Exception {
       
        Post post_saved = ps.addEditPost(test_post);
        
        this.mockMvc
        .perform(get("/edit_post/"+post_saved.getId()))
        .andExpect(status().isOk())
        .andExpect(view().name("form"))
        .andExpect(model().attributeExists("post"))
        .andExpect(model().attribute("post", isA(Post.class)))
        .andExpect(model().attribute("post", hasProperty("id", is(post_saved.getId()))))
        .andDo(print());
    }
    
    
    /**
     * Se prueba petición GET para ir a página con detalle de objeto Post de prueba creado 
     */
    @Test
    public void testGoDetails() throws Exception {
        
        Post post_saved = ps.addEditPost(test_post);
        
        this.mockMvc
        .perform(get("/posts/"+post_saved.getId()))
        .andExpect(status().isOk())
        .andExpect(view().name("details"))
        .andExpect(model().attributeExists("post"))
        .andExpect(model().attribute("post", isA(Post.class)))
        .andExpect(model().attribute("post", hasProperty("title", is("Test go home"))))
        .andDo(print());
    }
    
    
    
    /**
     * Se prueba error generado al realizar petición GET de un objeto Post no existente en base de datos
     */
    @Test
    public void testGoDetailsNotFound() throws Exception {       
        this.mockMvc
        .perform(get("/posts/0"))
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:/posts"))
        .andExpect(flash().attribute("text_alert", "¡Post no encontrado!"))
        .andDo(print());
    }
    
    
    /**
     * Se prueba el formulario para guardar un objeto Post con los campos obligatorios, para ello se crea una
     * simulación de un archivo de imagen  
     */
    @Test
    public void testSavePost() throws Exception {
        final MockMultipartFile multipartFile = new MockMultipartFile("imageUpload", "some content".getBytes());
        this.mockMvc
        .perform(multipart("/posts")
        .file(multipartFile)
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .param("title", "Post de prueba")
        .param("body", "Contenido post de prueba"))      
        .andExpect(status().isFound())
        .andExpect(redirectedUrl("/posts"))
        .andExpect(flash().attribute("text_alert", "¡Post guardado correctamente!"))
        .andDo(print());
    }
    
    
    /**
     * Se prueba petición GET para eliminar el objeto Post de prueba creado en la base de datos
     */
    @Test
    public void testDelete() throws Exception {   
        Post post_saved = ps.addEditPost(test_post);
        
        this.mockMvc
        .perform(get("/delete/" + post_saved.getId()))
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:/posts"))
        .andExpect(flash().attribute("text_alert", "¡Post eliminado correctamente!"))
        .andDo(print());
    }

}
