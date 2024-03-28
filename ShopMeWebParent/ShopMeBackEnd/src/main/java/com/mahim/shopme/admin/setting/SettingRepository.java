package com.mahim.shopme.admin.setting;

import com.mahim.shopme.common.entity.Setting;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingRepository extends CrudRepository<Setting, String> {

}
