package com.example.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.RPage;
import com.example.common.Result;
import com.example.entity.Employee;
import com.example.service.EmployeeService;
import com.example.utils.JWTUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
@RequestMapping("emp")
public class EmployeeController {

    private EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    /**
     * 默认密码经过md5加密后存入数据库中
     * @param employee
     * @return
     */
    @PostMapping
    public Result<String> save(@RequestBody Employee employee) {
        String md5Password = DigestUtils.md5DigestAsHex("123456".getBytes());
        employee.setPassword(md5Password);
        boolean save = service.save(employee);
        if (save) {
            return Result.success("添加成功");
        }
        return Result.error("添加失败");
    }

    /**
     * 分页查询
     * @param page
     * @param limit
     * @return
     */
    @GetMapping("/page")
    public Result<RPage> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit) {
        IPage<Employee> page2 = new Page<>(page, limit);
        service.page(page2);
        RPage rPage = new RPage();
        rPage.setTotal(page2.getTotal());
        rPage.setData(page2.getRecords());
        return Result.success(rPage);
    }

    /**
     * 修改用户信息
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public Result<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        String token = request.getHeader("token");
        Claims parser = JWTUtil.parser(token);
        String username = (String) parser.get("username");   // 获取当前操作的用户名
        if (!username.equals("admin") && employee.getStatus() != null) {
            return Result.error("非管理员无法禁用用户！");
        }
        boolean b = service.updateById(employee);
        if (!b) return Result.error("修改失败！");
        return Result.success("修改成功");
    }

    /**
     * 根据id查询员工，可用于前端页面的回显
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<Employee> get(@PathVariable Integer id) {
        Employee byId = service.getById(id);
        return Result.success(byId);
    }

}
