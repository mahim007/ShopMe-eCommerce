package com.mahim.shopme.admin.category;

import com.mahim.shopme.admin.paging.SearchRepository;
import com.mahim.shopme.common.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository  extends SearchRepository<Category, Integer> {

    Long countById(Integer id);

    @Query("update Category c set c.enabled = ?2 where c.id = ?1")
    @Modifying
    void updateEnabledStatus(Integer id, boolean enabled);

    @Query("select c from Category c where concat(c.id, ' ', c.name, ' ', c.alias) like %?1% ")
    Page<Category> findAllByKeyword(String keyword, Pageable pageRequest);

    Category findByName(String name);

    Category findByAlias(String alias);

    @Query("select c from Category c where c.parent.id is null")
    public List<Category> findRootCategories(Sort sort);

    @Query("select c from Category c where c.parent.id is null")
    public Page<Category> findRootCategories(Pageable pageable);

    @Query("select c from Category c where c.name like %?1%")
    public Page<Category> search(String keyword, Pageable pageable);
}
