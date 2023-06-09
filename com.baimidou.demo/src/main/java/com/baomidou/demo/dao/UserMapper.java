package com.baomidou.demo.dao;

import com.baomidou.demo.model.UserModel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @author sp0313
 * @date 2023年06月09日 11:13:00
 */
public interface UserMapper extends BaseMapper<UserModel> {

    UserModel getById(Integer id);

}
