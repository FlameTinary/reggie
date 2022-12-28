package top.sheldon.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    /**
     * 添加操作，自动填充
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        Long currentId = BaseContext.getCurrentId();
        log.info("currentId = " + currentId);
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser", currentId);
        metaObject.setValue("updateUser", currentId);
    }

    /**
     * 更新操作，自动填充
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {

        long id = Thread.currentThread().getId();
        log.info("当前线程为：{}", id);
        Long currentId = BaseContext.getCurrentId();
        log.info("currentId = " + currentId);
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", currentId);
    }
}
