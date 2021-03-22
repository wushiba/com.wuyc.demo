package com.yfshop.code.manager.impl;

import com.yfshop.code.model.Menu;
import com.yfshop.code.mapper.MenuMapper;
import com.yfshop.code.manager.MenuManager;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yoush
 * @since 2021-03-22
 */
@Service
public class MenuManagerImpl extends ServiceImpl<MenuMapper, Menu> implements MenuManager {

}
