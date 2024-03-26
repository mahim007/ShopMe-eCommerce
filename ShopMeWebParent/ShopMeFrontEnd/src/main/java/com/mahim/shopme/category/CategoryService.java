package com.mahim.shopme.category;

import com.mahim.shopme.common.entity.Category;
import com.mahim.shopme.common.exception.CategoryNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> listNoChildrenCategories() {
        ArrayList<Category> listNoChildrenCategories = new ArrayList<>();
        List<Category> listEnabledCategories = categoryRepository.findAllEnabled();

        listEnabledCategories.forEach(category -> {
            Set<Category> children = category.getChildren();
            if (children == null || children.isEmpty()) {
                listNoChildrenCategories.add(category);
            }
        });

        return listNoChildrenCategories;
    }

    public Category getCategory(String alias) throws CategoryNotFoundException {
        Category category = categoryRepository.findByAliasEnabled(alias);
        if (category == null) {
            throw new CategoryNotFoundException("Category with alias: " + alias + " not found.");
        }

        return category;
    }

    public List<Category> getCategoryParents(Category child) {
        List<Category> listParents = new ArrayList<>();
        Category parent = child.getParent();

        while (parent != null) {
            listParents.add(0, parent);
            parent = parent.getParent();
        }
        listParents.add(child);

        return listParents;
    }
}
