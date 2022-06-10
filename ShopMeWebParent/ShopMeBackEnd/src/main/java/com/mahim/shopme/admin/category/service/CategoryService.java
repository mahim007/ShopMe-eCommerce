package com.mahim.shopme.admin.category.service;

import com.mahim.shopme.admin.category.CategoryNotFoundException;
import com.mahim.shopme.admin.category.CategoryRepository;
import com.mahim.shopme.admin.user.UserNotFoundException;
import com.mahim.shopme.common.entity.Category;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    public static final int CATEGORIES_PER_PAGE = 5;

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> listAll() {
        return (List<Category>) categoryRepository.findAll();
    }

    public Page<Category> listByPage(int pageNum, String sortField, String sortDir) {
        Sort sort = Sort.by(sortField);
        sort = StringUtils.equals(sortDir, "asc") ? sort.ascending() : sort.descending();
        PageRequest pageRequest = PageRequest.of(pageNum - 1, CATEGORIES_PER_PAGE, sort);
        return categoryRepository.findAll(pageRequest);
    }

    public Page<Category> listByKeyword(int pageNum, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);
        sort = StringUtils.equals(sortDir, "asc") ? sort.ascending() : sort.descending();
        PageRequest pageRequest = PageRequest.of(pageNum - 1, CATEGORIES_PER_PAGE, sort);
        return categoryRepository.findAllByKeyword(keyword, pageRequest);
    }

    public Category save(Category category) {
        boolean isUpdating = category.getId() != null;
        Category categoryToSave;

        if (isUpdating) {
            Optional<Category> existingCategoryOptional = categoryRepository.findById(category.getId());
            if (existingCategoryOptional.isPresent()) {
                Category existingCategory = existingCategoryOptional.get();
                existingCategory.setName(category.getName());
                existingCategory.setAlias(category.getAlias());
                existingCategory.setPhotos(category.getPhotos());
                categoryToSave = existingCategory;
            } else {
                categoryToSave = category;
            }
        } else {
            categoryToSave = category;
        }

        return categoryRepository.save(categoryToSave);
    }

    public Category findCategoryById(Integer id) throws CategoryNotFoundException {
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        if (categoryOptional.isPresent()) {
            return categoryOptional.get();
        } else {
            throw new CategoryNotFoundException("Category with id : " + id + " not found!");
        }
    }

    public void delete(Integer id) throws UserNotFoundException {
        Long countById = categoryRepository.countById(id);
        if (countById == null || countById == 0) {
            throw new UserNotFoundException("No user found with id: " + id);
        }

        categoryRepository.deleteById(id);
    }

    public void updateEnabledStatus(Integer id, boolean enabled) throws UserNotFoundException {
        Long countById = categoryRepository.countById(id);

        if (countById == null || countById == 0) {
            throw new UserNotFoundException("User not found (ID: " + id + ")");
        }
        categoryRepository.updateEnabledStatus(id, !enabled);
    }

    public List<Category> getHierarchicalCategories() {
        List<Category> hierarchicalCategories = new ArrayList<>();
        Iterable<Category> categoryIterable = categoryRepository.findAll();

        int count = 1;
        for (Category category : categoryIterable) {
            if (category.getParent() == null) {
                listCategoryChildren(hierarchicalCategories, category, new StringBuilder("" + count++ + "."));
            }
        }

        return hierarchicalCategories;
    }

    private void listCategoryChildren(List<Category> hierarchicalCategories, Category parent, StringBuilder level) {
        StringBuilder name = new StringBuilder(level.toString());
        name.append(" ");
        name.append(parent.getName());
        hierarchicalCategories.add(new Category(parent.getId(), name.toString()));

        int count =1;
        for (Category child : parent.getChildren()) {
            listCategoryChildren(hierarchicalCategories, child, new StringBuilder(level.toString() + count++ + "."));
        }

    }
}
