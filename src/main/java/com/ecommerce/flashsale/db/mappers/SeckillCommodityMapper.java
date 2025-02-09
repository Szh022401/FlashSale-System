package com.ecommerce.flashsale.db.mappers;

import com.ecommerce.flashsale.db.po.SeckillCommodity;
import org.apache.ibatis.annotations.Mapper;


public interface SeckillCommodityMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SeckillCommodity record);

    int insertSelective(SeckillCommodity record);

    SeckillCommodity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SeckillCommodity record);

    int updateByPrimaryKey(SeckillCommodity record);
}