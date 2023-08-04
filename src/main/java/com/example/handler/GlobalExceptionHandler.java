package com.example.handler;


import com.example.common.Result;
import com.example.exception.CommonException;
import com.example.exception.DeleteException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.FileNotFoundException;
import java.sql.SQLIntegrityConstraintViolationException;


@RestControllerAdvice  // (annotations = {RestController.class, Controller.class})
public class GlobalExceptionHandler {

    /**
     * 处理字段约束冲突
     * @return
     */
    @ExceptionHandler({SQLIntegrityConstraintViolationException.class})
    public Result<String> sqlConstraintViolationException(SQLIntegrityConstraintViolationException ex) {
//        return Result.error("修改字段冲突！");
        return Result.error(ex.getMessage());
    }

    /**
     * 删除数据库数据出现错误
     * @param ex
     * @return
     */
    @ExceptionHandler({DeleteException.class, CommonException.class})
    public Result<String> customException(RuntimeException ex) {
        return Result.error(ex.getMessage());
    }

    @ExceptionHandler(FileNotFoundException.class)
    public Result<String> notFoundFile(FileNotFoundException ex) {
        String msg = ex.getMessage();
        int i = msg.lastIndexOf("\\");
        String substring = msg.substring(i);
        return Result.error(substring);
    }

}
