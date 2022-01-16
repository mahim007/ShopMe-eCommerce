package com.mahim.shopme.admin.user;

import com.mahim.shopme.common.entity.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Integer> {

    User getUserByEmail(@Param("email") String email);

    Long countById(Integer id);

    @Query("update User u set u.enabled = ?2 where  u.id = ?1")
    @Modifying
    void updateEnabledStatus(Integer id, boolean enabled);
}
