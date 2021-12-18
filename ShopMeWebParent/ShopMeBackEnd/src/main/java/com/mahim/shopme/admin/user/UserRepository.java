package com.mahim.shopme.admin.user;

import com.mahim.shopme.common.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    User getUserByEmail(@Param("email") String email);
}
