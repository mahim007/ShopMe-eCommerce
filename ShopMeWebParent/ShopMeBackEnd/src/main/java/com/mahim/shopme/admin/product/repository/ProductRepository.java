package com.mahim.shopme.admin.product.repository;

import com.mahim.shopme.admin.paging.SearchRepository;
import com.mahim.shopme.common.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends SearchRepository<Product, Integer> {

    int countById(Integer id);
    Product findByName(String name);
    Product findByAlias(String alias);

    @Query("update Product p set p.enabled = ?2 where p.id = ?1")
    @Modifying
    void updateEnableStatus(Integer id, boolean enabled);

    @Query("SELECT p FROM Product p WHERE p.name LIKE %?1%"
            + " OR p.shortDescription LIKE %?1%"
            + " OR p.fullDescription LIKE %?1%"
            + " OR p.brand.name LIKE %?1%"
            + " OR p.category.name LIKE %?1%"
    )
    Page<Product> findAllByKeyword(String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.category.id = ?1 " +
            "OR p.category.allParentIDs LIKE %?2%")
    Page<Product> findAllInCategory(Integer categoryId, String categoryIdMatch, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE (p.category.id = ?1 "
            + "OR p.category.allParentIDs LIKE %?2%)"
            + "AND (p.name LIKE %?3%"
            + "OR p.alias LIKE %?3%"
            + " OR p.shortDescription LIKE %?3%"
            + " OR p.fullDescription LIKE %?3%"
            + " OR p.brand.name LIKE %?3%"
            + " OR p.category.name LIKE %?3%)"
    )
    Page<Product> searchInCategory(Integer categoryId, String categoryIdMatch, String keyword, Pageable pageable);

    @Query("" +
            "select p from Product p where " +
            "p.name like %:keyword% or " +
            "p.alias like %:keyword% or " +
            "p.shortDescription like %:keyword% or " +
            "p.fullDescription like %:keyword%")
    Page<Product> searchProductsByName(String keyword, Pageable pageable);
}
