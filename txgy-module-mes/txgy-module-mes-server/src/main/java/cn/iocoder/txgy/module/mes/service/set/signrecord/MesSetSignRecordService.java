package cn.iocoder.txgy.module.mes.service.set.signrecord;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.signrecord.vo.MesSetSignRecordPageReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.signrecord.MesSetSignRecordDO;

/**
 * MES 安全环保检测-签字记录 Service 接口
 *
 * 签字记录只增不改删：仅由复核/审批等流程内部追加(createSignRecord)，对前端只读 page。
 *
 * @author OPENLAB BS
 */
public interface MesSetSignRecordService {

    /**
     * 追加签字记录（内部调用：复核签字落档，须在调用方事务内）
     *
     * @param record 签字（biz_type/biz_no/sign_role 等须由调用方组装）
     * @return 编号
     */
    Long createSignRecord(MesSetSignRecordDO record);

    /**
     * 获得签字记录分页
     *
     * @param pageReqVO 分页查询
     * @return 签字记录分页
     */
    PageResult<MesSetSignRecordDO> getSignRecordPage(MesSetSignRecordPageReqVO pageReqVO);

}
