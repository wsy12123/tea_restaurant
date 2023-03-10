package com.xmut.tearestaurant.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xmut.tearestaurant.common.BaseContext;
import com.xmut.tearestaurant.common.R;
import com.xmut.tearestaurant.entity.AddressBook;
import com.xmut.tearestaurant.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Bless_Wu
 * @Description
 * @create 2022-11-01 21:35
 */
@RestController
@Slf4j
@RequestMapping("/addressBook")
@CrossOrigin
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;


    /**
     * 修改
     */
    @Transactional
    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook) {
//         System.out.println(addressBook);
        addressBook.setUpdateUser(addressBook.getUserId());
        addressBookService.updateById(addressBook);
        return R.success("成功");
    }

    /**
     * 新增地址的实现
     */
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook) {
       addressBook.setCreateUser(addressBook.getUserId());
        addressBook.setUpdateUser(addressBook.getUserId());
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }

    /**
     * 设置默认地址
     */
    @PutMapping("/default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook) {
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        wrapper.set(AddressBook::getIsDefault, 0);
        addressBookService.update(wrapper);
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }

    /**
     * 根据id查询地址
     */
    @Transactional
    @GetMapping
    public R<AddressBook> get(Long id) {
        AddressBook addressBook = addressBookService.getById(id);

        if (addressBook != null)
            return R.success(addressBook);
        else return R.error("没有找到地址");
    }

    /**
     * 查询默认对象
     */
    @GetMapping("/default")
    public R<AddressBook> getDefault() {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        queryWrapper.eq(AddressBook::getIsDefault, 1);
        AddressBook addressBook = addressBookService.getOne(queryWrapper);
        if (null == addressBook) {
            return R.error("没有找到对象");
        } else {
            return R.success(addressBook);
        }
    }

    /**
     * 查询指定用户的全部地址
     */
    @Transactional
    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook) {
        addressBook.setUserId(addressBook.getUserId());
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(null != addressBook.getUserId(), AddressBook::getUserId, addressBook.getUserId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);
        List<AddressBook> addressBooks = addressBookService.list(queryWrapper);
        return R.success(addressBooks);
    }
}
