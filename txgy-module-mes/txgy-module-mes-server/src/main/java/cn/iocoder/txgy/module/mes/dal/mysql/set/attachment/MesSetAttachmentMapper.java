package cn.iocoder.txgy.module.mes.dal.mysql.set.attachment;

import cn.iocoder.txgy.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.txgy.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.attachment.MesSetAttachmentDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MES 安全环保检测-通用业务附件 Mapper
 *
 * @author OPENLAB BS
 */
@Mapper
public interface MesSetAttachmentMapper extends BaseMapperX<MesSetAttachmentDO> {

    /**
     * 按业务键取全部附件（最新在后，前端按上传顺序展示）。
     */
    default List<MesSetAttachmentDO> selectListByBiz(String bizType, String bizNo) {
        return selectList(new LambdaQueryWrapperX<MesSetAttachmentDO>()
                .eq(MesSetAttachmentDO::getBizType, bizType)
                .eq(MesSetAttachmentDO::getBizNo, bizNo)
                .orderByAsc(MesSetAttachmentDO::getId));
    }

}
