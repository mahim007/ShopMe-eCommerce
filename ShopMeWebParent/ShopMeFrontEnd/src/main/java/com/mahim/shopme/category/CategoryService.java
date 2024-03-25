package com.mahim.shopme.category;

import com.mahim.shopme.common.entity.Category;
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
}
