package com.mahim.shopme.admin.product.repository;

import com.mahim.shopme.common.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProductRepository extends PagingAndSortingRepository<Product, Integer> {

    @Query("select p from Product p where concat(p.id, ' ', p.name, ' ', p.alias) like %?1%")
    Page<Product> findAllByKeyword(String keyword, PageRequest pageRequest);

    int countById(Integer id);
    Product findByName(String name);
    Product findByAlias(String alias);

    @Query("update Product p set p.enabled = ?2 where p.id = ?1")
    @Modifying
    void updateEnableStatus(Integer id, boolean enabled);
}
