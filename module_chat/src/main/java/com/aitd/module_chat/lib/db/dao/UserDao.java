package com.aitd.module_chat.lib.db.dao;

import com.aitd.module_chat.lib.db.entity.UserEntity;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import static androidx.room.OnConflictStrategy.REPLACE;

//用户表
@Dao
public interface UserDao {
    /**
     * 插入用户
     * @param userEntity
     * @return
     */
    @Insert(onConflict = REPLACE)
    long insertUser(UserEntity userEntity);

    /**
     * 获取用户id
     * @param userId
     * @return
     */
    @Query("SELECT * FROM user WHERE user_id == :userId LIMIT 1")
    UserEntity getUserById(String userId);
}
