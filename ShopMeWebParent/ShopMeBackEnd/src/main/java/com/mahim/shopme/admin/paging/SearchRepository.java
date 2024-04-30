package com.mahim.shopme.admin.paging;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

@NoRepositoryBean
public interface SearchRepository<T, ID> extends PagingAndSortingRepository<T, ID> {

    Page<T> findAllByKeyword(String keyword, Pageable pageable);
}
