package top.sheldon.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.sheldon.reggie.domain.Category;

public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
