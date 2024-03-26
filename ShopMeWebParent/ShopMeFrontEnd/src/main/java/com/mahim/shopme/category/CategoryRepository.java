package com.mahim.shopme.category;

import com.mahim.shopme.common.entity.Category;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Integer> {

    @Query("SELECT c from Category c WHERE c.enabled = true ORDER BY c.name ASC")
    List<Category> findAllEnabled();

    @Query("SELECT c FROM Category c WHERE c.enabled = true AND c.alias = ?1")
    Category findByAliasEnabled(String alias);
}
