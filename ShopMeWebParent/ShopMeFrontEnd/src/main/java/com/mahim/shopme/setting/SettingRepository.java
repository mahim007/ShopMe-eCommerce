package com.mahim.shopme.setting;

import com.mahim.shopme.common.entity.Setting;
import com.mahim.shopme.common.enums.SettingCategory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SettingRepository extends CrudRepository<Setting, String> {

    List<Setting> findByCategory(SettingCategory category);

    @Query("SELECT s from Setting s where s.category = ?1 OR s.category = ?2")
    List<Setting> findByTwoCategories(SettingCategory cat1, SettingCategory cat2);

    Optional<Setting> findByKey(String key);
}
