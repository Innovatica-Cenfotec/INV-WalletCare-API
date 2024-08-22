package com.inv.walletCare.rest.category;

import com.inv.walletCare.logic.entity.expenseCategory.ExpenseCategory;
import com.inv.walletCare.logic.entity.expenseCategory.ExpenseCategoryRepository;
import com.inv.walletCare.logic.entity.user.User;
import com.inv.walletCare.logic.exceptions.FieldValidationException;
import com.inv.walletCare.logic.validation.OnCreate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/categories")
public class CategoryRestController {

    @Autowired
    private ExpenseCategoryRepository categoryRepository;


    /***
     * Get all the categories for the current user
     * @return List of categories
     */
    @GetMapping
    public List<ExpenseCategory> getCategories() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return categoryRepository.findAllByOwnerId(user.getId());
    }

    /***
     * Get a category by id
     * @param id Category id
     * @return Category
     */
    @GetMapping("/{id}")
    public ExpenseCategory getCategory(@PathVariable long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Optional<ExpenseCategory> category = categoryRepository.findByIdAndOwnerId(id, user.getId());

        if (category.isEmpty()) {
            throw new FieldValidationException("id", "La categoría no existe o no pertenece al usuario");
        }

        return category.get();
    }

    /***
     * Create a new category
     * @param category Category object
     * @return Created category
     */
    @PostMapping
    public ExpenseCategory createCategory(@Validated(OnCreate.class) @RequestBody ExpenseCategory category) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        category.setOwner(user);

        Optional<ExpenseCategory> existingCategory = categoryRepository.findByNameAndOwnerId(category.getName(), user.getId());
        if (existingCategory.isPresent()) {
            throw new FieldValidationException("name", "El nombre de la categoría ya existe, por favor elija otro");
        }

        ExpenseCategory newCategory = new ExpenseCategory();
        newCategory.setName(category.getName());
        newCategory.setOwner(user);
        newCategory.setCreatedAt(new Date());
        newCategory.setDeleted(false);
        newCategory.setUpdatedAt(new Date());
        return categoryRepository.save(newCategory);
    }

    /***
     * Update a category
     * @param id Category id
     * @param category Category object
     * @return Updated category
     */
    @PutMapping("/{id}")
    public ExpenseCategory updateCategory(@PathVariable long id, @Validated(OnCreate.class) @RequestBody ExpenseCategory category) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Optional<ExpenseCategory> existingCategory = categoryRepository.findByNameAndOwnerId(category.getName(), user.getId());
        if (existingCategory.isEmpty()) {
            throw new IllegalArgumentException("La categoría no existe o no pertenece al usuario");
        }

        if (existingCategory.get().getId() != id) {
            throw new FieldValidationException("name", "El nombre de la categoría ya existe, por favor elija otro");
        }

        existingCategory.get().setName(category.getName());
        existingCategory.get().setUpdatedAt(new Date());
        return categoryRepository.save(existingCategory.get());
    }

    /**
     * Delete a category
     * @param id Category id
     */
    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Optional<ExpenseCategory> existingCategory = categoryRepository.findByIdAndOwnerId(id, user.getId());
        if (existingCategory.isEmpty()) {
            throw new IllegalArgumentException("La categoría no existe o no pertenece al usuario");
        }

        existingCategory.get().setDeleted(true);
        existingCategory.get().setDeletedAt(new Date());
        existingCategory.get().setUpdatedAt(new Date());
        categoryRepository.save(existingCategory.get());
    }
}