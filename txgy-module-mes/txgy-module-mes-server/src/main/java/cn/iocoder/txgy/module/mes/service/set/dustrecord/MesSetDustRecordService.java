package cn.iocoder.txgy.module.mes.service.set.dustrecord;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.dustrecord.vo.MesSetDustRecordPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.dustrecord.vo.MesSetDustRecordSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.dustrecord.MesSetDustRecordDO;

import jakarta.validation.Valid;

/**
 * MES 安全环保检测-粉尘浓度检测记录 Service 接口
 *
 * @author OPENLAB BS
 */
public interface MesSetDustRecordService {

    /**
     * 创建粉尘浓度检测记录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createDustRecord(@Valid MesSetDustRecordSaveReqVO createReqVO);

    /**
     * 更新粉尘浓度检测记录
     *
     * @param updateReqVO 更新信息
     */
    void updateDustRecord(@Valid MesSetDustRecordSaveReqVO updateReqVO);

    /**
     * 删除粉尘浓度检测记录
     *
     * @param id 编号
     */
    void deleteDustRecord(Long id);

    /**
     * 校验粉尘浓度检测记录存在
     *
     * @param id 编号
     */
    void validateDustRecordExists(Long id);

    /**
     * 获得粉尘浓度检测记录
     *
     * @param id 编号
     * @return 粉尘浓度检测记录
     */
    MesSetDustRecordDO getDustRecord(Long id);

    /**
     * 获得粉尘浓度检测记录分页
     *
     * @param pageReqVO 分页查询
     * @return 粉尘浓度检测记录分页
     */
    PageResult<MesSetDustRecordDO> getDustRecordPage(MesSetDustRecordPageReqVO pageReqVO);

}
