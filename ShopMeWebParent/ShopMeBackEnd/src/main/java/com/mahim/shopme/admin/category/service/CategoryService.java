package com.mahim.shopme.admin.category.service;

import com.mahim.shopme.admin.FileUploadUtil;
import com.mahim.shopme.admin.paging.PagingAndSortingHelper;
import com.mahim.shopme.common.exception.CategoryNotFoundException;
import com.mahim.shopme.admin.category.CategoryRepository;
import com.mahim.shopme.admin.user.UserNotFoundException;
import com.mahim.shopme.common.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mahim.shopme.common.util.StaticPathUtils.CATEGORY_UPLOAD_DIR;

@Service
public class CategoryService {

    public static final int CATEGORIES_PER_PAGE = 10;

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> listAll() {
        List<Category> categories = (List<Category>) categoryRepository.findAll();
        return getHierarchicalCategories(categories);
    }

    public Page<Category> listByKeyword(int pageNum, PagingAndSortingHelper helper) {
        return (Page<Category>) helper.listEntities(pageNum, CATEGORIES_PER_PAGE, categoryRepository);
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
                existingCategory.setParent(category.getParent());
                existingCategory.setEnabled(category.isEnabled());
                categoryToSave = existingCategory;
            } else {
                categoryToSave = category;
            }
        } else {
            categoryToSave = category;
        }

        Category parent = category.getParent();
        if (parent != null) {
            String allParentIds = parent.getAllParentIDs() == null ? "-" : parent.getAllParentIDs();
            allParentIds += String.valueOf(parent.getId()) + "-";
            categoryToSave.setAllParentIDs(allParentIds);
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
            throw new UserNotFoundException("No Category found with id: " + id);
        }

        categoryRepository.deleteById(id);
        FileUploadUtil.removeDir(CATEGORY_UPLOAD_DIR + "/" + id);
    }

    public void updateEnabledStatus(Integer id, boolean enabled) throws UserNotFoundException {
        Long countById = categoryRepository.countById(id);

        if (countById == null || countById == 0) {
            throw new UserNotFoundException("User not found (ID: " + id + ")");
        }
        categoryRepository.updateEnabledStatus(id, !enabled);
    }

    public List<Category> getHierarchicalCategories(Iterable<Category> categoryIterable) {
        List<Category> hierarchicalCategories = new ArrayList<>();

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

        hierarchicalCategories.add(new Category(parent.getId(), name.append(parent.getName()).toString(), parent.getAlias(), parent.getPhotos(),
                parent.isEnabled(), null, null));

        int count =1;
        for (Category child : parent.getChildren()) {
            listCategoryChildren(hierarchicalCategories, child, new StringBuilder(level.toString() + count++ + "."));
        }

    }

    public String checkUnique(Integer id, String name, String alias) {
        boolean isCreatingNew = (id == null || id == 0);

        Category categoryByName = categoryRepository.findByName(name);
        Category categoryByAlias = categoryRepository.findByAlias(alias);

        if (isCreatingNew) {
            if (categoryByName != null) {
                return "DuplicatedName";
            } else {
                if (categoryByAlias != null) {
                    return "DuplicatedAlias";
                }
            }
        } else {
            if (categoryByName != null && categoryByName.getId() != id) {
                return "DuplicatedName";
            }

            if (categoryByAlias != null && categoryByAlias.getId() != id) {
                return "DuplicatedAlias";
            }
        }

        return "OK";
    }
}
