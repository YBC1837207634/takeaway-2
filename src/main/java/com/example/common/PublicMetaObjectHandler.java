package com.example.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

/**
 * MP 自动填充
 */
@Component
public class PublicMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        Long id = BaseContext.getThreadLocal().get();
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now()); // 起始版本 3.3.0(推荐使用)
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now()); // 起始版本 3.3.0(推荐使用)
        this.strictInsertFill(metaObject, "createUser", Long.class, id); // 起始版本 3.3.0(推荐使用)
        this.strictInsertFill(metaObject, "updateUser", Long.class, id);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Long id = BaseContext.getThreadLocal().get();
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        this.strictUpdateFill(metaObject, "updateUser", Long.class, id);
    }
}
