package com.biao.sql.build.mkcommon;

import com.biao.entity.UserRelation;
import com.biao.sql.BaseSqlBuild;

public class MkCommonUserRelationBuild extends BaseSqlBuild<UserRelation, Integer> {
    public static final String columns = " `id`, `user_id`, `username`, `parent_id`, `top_parent_id`, `tree_id`, `deth`, `level`, `create_date`, `update_date`, `create_by`, `update_by`" ;
}
