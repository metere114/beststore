package com.metere114.beststore.controllers;

import com.metere114.beststore.controllers.ProductsControllers;
import com.metere114.beststore.models.Product;
import com.metere114.beststore.models.ProductDto;
import com.metere114.beststore.services.ProductsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class ProductsControllersTest {

    @Mock
    private ProductsRepository repo;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private ProductsControllers productsControllers;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void showProductList() {
        List<Product> products = Arrays.asList(new Product(), new Product());
        when(repo.findAll(any(Sort.class))).thenReturn(products);

        String viewName = productsControllers.showProductList(model);

        assertEquals("products/index", viewName);
        verify(model, times(1)).addAttribute("products", products);
    }

    @Test
    void showCreatePage() {
        String viewName = productsControllers.showCreatePage(model);

        assertEquals("products/CreateProduct", viewName);
        verify(model, times(1)).addAttribute(eq("productDto"), any(ProductDto.class));
    }

    @Test
    void createProduct() throws Exception {
        ProductDto productDto = new ProductDto();
        productDto.setImageFile(multipartFile);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(bindingResult.hasErrors()).thenReturn(false);

        String viewName = productsControllers.createProduct(productDto, bindingResult);

        assertEquals("redirect:/products", viewName);
        verify(repo, times(1)).save(any(Product.class));
    }

    @Test
    void createProductWithErrors() {
        ProductDto productDto = new ProductDto();
        productDto.setImageFile(multipartFile);
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = productsControllers.createProduct(productDto, bindingResult);

        assertEquals("products/CreateProduct", viewName);
        verify(repo, times(0)).save(any(Product.class));
    }

    @Test
    void showEditPage() {
        Product product = new Product();
        product.setId(1);
        when(repo.findById(anyInt())).thenReturn(Optional.of(product));

        String viewName = productsControllers.showEditPage(model, 1);

        assertEquals("products/EditProduct", viewName);
        verify(model, times(1)).addAttribute("product", product);
        verify(model, times(1)).addAttribute(eq("productDto"), any(ProductDto.class));
    }

    @Test
    void updateProduct() throws Exception {
        Product product = new Product();
        when(repo.findById(anyInt())).thenReturn(Optional.of(product));
        ProductDto productDto = new ProductDto();
        productDto.setImageFile(multipartFile);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(bindingResult.hasErrors()).thenReturn(false);

        String viewName = productsControllers.updateProduct(model, 1, productDto, bindingResult);

        assertEquals("redirect:/products", viewName);
        verify(repo, times(1)).save(any(Product.class));
    }

    @Test
    void updateProductWithErrors() {
        Product product = new Product();
        when(repo.findById(anyInt())).thenReturn(Optional.of(product));
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = productsControllers.updateProduct(model, 1, new ProductDto(), bindingResult);

        assertEquals("products/EditProduct", viewName);
        verify(repo, times(0)).save(any(Product.class));
    }

    @Test
    void deleteProduct() throws Exception {
        Product product = new Product();
        product.setImageFileName("image.jpg");
        when(repo.findById(anyInt())).thenReturn(Optional.of(product));

        String viewName = productsControllers.deleteProduct(1);

        assertEquals("redirect:/products", viewName);
        verify(repo, times(1)).delete(product);
    }
}