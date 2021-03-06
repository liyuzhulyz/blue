package com.online.college.core.auth.service.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.online.college.common.page.TailPage;
import com.online.college.common.storage.QiniuStorage;
import com.online.college.core.auth.dao.AuthUserDao;
import com.online.college.core.auth.domain.AuthUser;
import com.online.college.core.auth.service.IAuthUserService;


@Service
public class AuthUserServiceImpl implements IAuthUserService {

    @Autowired
    private AuthUserDao entityDao;

    public void createSelectivity(AuthUser entity) {
        entityDao.createSelectivity(entity);
    }

    /**
     * 1 根据username获取
     **/
    public AuthUser getByUsername(String username) {
        return entityDao.getByUsername(username);
    }

    /**
     * 2 根据id获取
     */
    public AuthUser getById(Long id) {
        return entityDao.getById(id);
    }

    /**
     * 3 根据username和password获取
     **/
    public AuthUser getByUsernameAndPassword(AuthUser authUser) {
        return entityDao.getByUsernameAndPassword(authUser);
    }

    /**
     * 4 获取首页推荐5个讲师
     **/
    public List<AuthUser> queryRecomd() {
        List<AuthUser> recomdList = entityDao.queryRecomd();
        if (CollectionUtils.isNotEmpty(recomdList)) {
            /**教师的头像不为空就设置头像*/
            for (AuthUser item : recomdList) {
                if (StringUtils.isNotEmpty(item.getHeader())) {
                    item.setHeader(QiniuStorage.getUrl(item.getHeader()));
                }
            }
        }
        return recomdList;
    }

    /**
     * 5 分页的信息
     */
    public TailPage<AuthUser> queryPage(AuthUser queryEntity, TailPage<AuthUser> page) {
        Integer itemsTotalCount = entityDao.getTotalItemsCount(queryEntity);
        /**当页的数据*/
        List<AuthUser> items = entityDao.queryPage(queryEntity, page);
        /**总数*/
        page.setItemsTotalCount(itemsTotalCount);
        page.setItems(items);
        return page;
    }

    /**
     * 6 更新
     */
    public void update(AuthUser entity) {
        entityDao.update(entity);
    }

    /**
     * 7 有选择的更新
     */
    public void updateSelectivity(AuthUser entity) {
        entityDao.updateSelectivity(entity);
    }

    /**
     * 8 删除
     */
    public void delete(AuthUser entity) {
        entityDao.delete(entity);
    }

    /**
     * 9 逻辑删除
     */
    public void deleteLogic(AuthUser entity) {
        entityDao.deleteLogic(entity);
    }


}

