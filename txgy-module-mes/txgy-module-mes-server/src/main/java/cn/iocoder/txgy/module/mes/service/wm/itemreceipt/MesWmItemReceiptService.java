package cn.iocoder.txgy.module.mes.service.wm.itemreceipt;

import cn.iocoder.txgy.framework.common.pojo.PageResult;
import cn.iocoder.txgy.module.mes.controller.admin.wm.itemreceipt.vo.MesWmItemReceiptPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.wm.itemreceipt.vo.MesWmItemReceiptRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.wm.itemreceipt.vo.MesWmItemReceiptSaveReqVO;
import cn.iocoder.txgy.module.mes.dal.dataobject.wm.itemreceipt.MesWmItemReceiptDO;
import jakarta.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static cn.iocoder.txgy.framework.common.util.collection.CollectionUtils.convertMap;

/**
 * MES 采购入库单 Service 接口
 */
public interface MesWmItemReceiptService {

    /**
     * 创建采购入库单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createItemReceipt(@Valid MesWmItemReceiptSaveReqVO createReqVO);

    /**
     * 修改采购入库单
     *
     * @param updateReqVO 修改信息
     */
    void updateItemReceipt(@Valid MesWmItemReceiptSaveReqVO updateReqVO);

    /**
     * 删除采购入库单（级联删除行+明细）
     *
     * @param id 编号
     */
    void deleteItemReceipt(Long id);

    /**
     * 获得采购入库单
     *
     * @param id 编号
     * @return 采购入库单
     */
    MesWmItemReceiptDO getItemReceipt(Long id);

    /**
     * 获得采购入库单分页
     *
     * @param pageReqVO 分页参数
     * @return 采购入库单分页
     */
    PageResult<MesWmItemReceiptDO> getItemReceiptPage(MesWmItemReceiptPageReqVO pageReqVO);

    /**
     * 给采购入库单列表补「整单环保判定」投影（列表与导出共用）。
     *
     * 判定行与入库单的唯一句柄是 biz_no=入库单编码：两个批量查询后内存聚合，不做逐单查询。
     * 「判定完成」必须拿**单据行数**去比——只看判定行会把「3 行只判了 1 行」误报成完成。
     * 同一物料行「重新检测」会再落一行判定，按批次（无批次退化到物料编码）归并后取最新一条为准。
     *
     * @param receipts 已拼好的单头 VO，原地回填 pollution* 四项
     */
    void fillPollutionStatus(List<MesWmItemReceiptRespVO> receipts);

    /**
     * 提交采购入库单（草稿 → 待上架）
     *
     * @param id 编号
     */
    void submitItemReceipt(Long id);

    /**
     * 执行上架（待上架 → 待入库）
     *
     * @param id 编号
     */
    void stockItemReceipt(Long id);

    /**
     * 执行入库（待入库 → 已完成），更新库存台账
     *
     * @param id 编号
     */
    void finishItemReceipt(Long id);

    /**
     * 取消采购入库单（任意非已完成/已取消状态 → 已取消）
     *
     * @param id 编号
     */
    void cancelItemReceipt(Long id);

    /**
     * 校验采购入库单存在且处于可编辑状态（草稿或待上架）
     *
     * @param id 编号
     * @return 采购入库单
     */
    MesWmItemReceiptDO validateItemReceiptEditable(Long id);

    /**
     * 查询指定供应商的采购入库单数量
     *
     * @param vendorId 供应商编号
     * @return 数量
     */
    Long getItemReceiptCountByVendorId(Long vendorId);

    /**
     * 查询指定供应商的采购入库单列表
     *
     * @param vendorId 供应商编号
     * @return 入库单列表
     */
    List<MesWmItemReceiptDO> getItemReceiptListByVendorId(Long vendorId);

    /**
     * 批量获得采购入库单列表
     *
     * @param ids 编号数组
     * @return 入库单列表
     */
    List<MesWmItemReceiptDO> getItemReceiptList(Collection<Long> ids);

    /**
     * 批量获得采购入库单 Map
     *
     * @param ids 编号数组
     * @return 入库单 Map
     */
    default Map<Long, MesWmItemReceiptDO> getItemReceiptMap(Collection<Long> ids) {
        return convertMap(getItemReceiptList(ids), MesWmItemReceiptDO::getId);
    }

}
