package cn.iocoder.txgy.module.mes.service.set.envboard;

import cn.iocoder.txgy.module.mes.controller.admin.set.envboard.vo.MesSetEnvBoardRespVO;

/**
 * MES 安全环保检测-环保看板 Service 接口
 *
 * 只读聚合，无写操作：看板一旦能改数据，它就变成了第二套入口，判定口径两边迟早会漂。
 *
 * @author OPENLAB BS
 */
public interface MesSetEnvBoardService {

    /**
     * 汇总环保看板（许可余量 / 设施运行 / 暂存倒计时 / 联单状态 / 应急物资）
     *
     * @return 看板数据
     */
    MesSetEnvBoardRespVO getBoard();

}
