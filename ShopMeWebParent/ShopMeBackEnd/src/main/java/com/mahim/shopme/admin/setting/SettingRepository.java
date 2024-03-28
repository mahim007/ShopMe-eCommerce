package com.mahim.shopme.admin.setting;

import com.mahim.shopme.common.entity.Setting;
import com.mahim.shopme.common.enums.SettingCategory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SettingRepository extends CrudRepository<Setting, String> {

    List<Setting> findByCategory(SettingCategory category);
}
