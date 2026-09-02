package cn.iocoder.txgy.module.mes.service.set.standard;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.set.standard.vo.MesSetStandardPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.standard.vo.MesSetStandardSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.set.standard.MesSetStandardDO;

import jakarta.validation.Valid;

import java.util.List;

/**
 * MES 安全环保检测-检测标准 Service 接口
 *
 * @author OPENLAB BS
 */
public interface MesSetStandardService {

    /**
     * 创建检测标准
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createStandard(@Valid MesSetStandardSaveReqVO createReqVO);

    /**
     * 更新检测标准
     *
     * @param updateReqVO 更新信息
     */
    void updateStandard(@Valid MesSetStandardSaveReqVO updateReqVO);

    /**
     * 删除检测标准
     *
     * @param id 编号
     */
    void deleteStandard(Long id);

    /**
     * 校验检测标准存在
     *
     * @param id 编号
     */
    void validateStandardExists(Long id);

    /**
     * 获得检测标准
     *
     * @param id 编号
     * @return 检测标准
     */
    MesSetStandardDO getStandard(Long id);

    /**
     * 获得检测标准分页
     *
     * @param pageReqVO 分页查询
     * @return 检测标准分页
     */
    PageResult<MesSetStandardDO> getStandardPage(MesSetStandardPageReqVO pageReqVO);

    /**
     * 获得启用状态的检测标准列表（标准下拉）
     *
     * @return 检测标准列表
     */
    List<MesSetStandardDO> getStandardList();

}
