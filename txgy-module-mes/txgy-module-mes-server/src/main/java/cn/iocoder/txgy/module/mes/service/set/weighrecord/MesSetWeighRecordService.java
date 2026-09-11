package cn.iocoder.txgy.module.mes.service.set.weighrecord;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.weighrecord.vo.MesSetWeighRecordPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.weighrecord.vo.MesSetWeighRecordSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.weighrecord.MesSetWeighRecordDO;

/**
 * MES 安全环保检测-称重记录 Service 接口
 *
 * 电子秤直采（AUTO）未接入前支持现场手工登记（MANUAL）；净重由服务端计算。
 *
 * @author OPENLAB BS
 */
public interface MesSetWeighRecordService {

    /**
     * 获得称重记录分页
     *
     * @param pageReqVO 分页查询
     * @return 称重记录分页
     */
    PageResult<MesSetWeighRecordDO> getWeighRecordPage(MesSetWeighRecordPageReqVO pageReqVO);

    /**
     * 手工登记称重记录（dataSource 恒为 MANUAL）
     *
     * @param reqVO 登记请求
     * @return 称重记录编号
     */
    Long createWeighRecord(MesSetWeighRecordSaveReqVO reqVO);

}
