package cn.iocoder.txgy.module.mes.dal.mysql.set.inventoryenv;

import cn.iocoder.txgy.module.mes.controller.admin.set.inventoryenv.vo.MesInventoryEnvDashboardItemRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.inventoryenv.vo.MesInventoryEnvInspectSummaryRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.inventoryenv.vo.MesInventoryEnvLedgerDurationRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.inventoryenv.vo.MesInventoryEnvPageReqVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.inventoryenv.vo.MesInventoryEnvPendingReviewRespVO;
import cn.iocoder.txgy.module.mes.controller.admin.set.inventoryenv.vo.MesInventoryEnvRespVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * MES 在库环保视图 Mapper
 *
 * 只用 XML：一条 SQL 里做多表 LEFT JOIN + 派生表取「每批次最近一次判定」+ 四个判据，
 * 让筛选和分页口径一致（在 Java 里补名字、过滤判据会把分页算错）。
 *
 * 分页/体检/看板三处共用 XML 里同一批片段（envFrom / criteriaCols），
 * 所以三处的数字天然对得上——改判据只改一处。
 */
@Mapper
public interface MesInventoryEnvMapper {

    /**
     * @param itemTypeIds 物料分类（含子分类），为空表示不限；由 Service 解析后传入
     */
    IPage<MesInventoryEnvRespVO> selectInventoryEnvPage(IPage<MesInventoryEnvRespVO> page,
                                                        @Param("reqVO") MesInventoryEnvPageReqVO reqVO,
                                                        @Param("itemTypeIds") Collection<Long> itemTypeIds);

    /**
     * 全库体检汇总（四项判据各自的命中数，不带页面筛选）。
     *
     * @param reqVO 只用于取 cycleDays / stockpileDays 两个阈值，筛选字段一律忽略
     */
    MesInventoryEnvInspectSummaryRespVO selectInspectSummary(@Param("reqVO") MesInventoryEnvPageReqVO reqVO);

    /** 看板 - 按仓库分档 */
    List<MesInventoryEnvDashboardItemRespVO> selectDashboardByWarehouse(@Param("reqVO") MesInventoryEnvPageReqVO reqVO);

    /** 看板 - 按物料分类分档 */
    List<MesInventoryEnvDashboardItemRespVO> selectDashboardByItemType(@Param("reqVO") MesInventoryEnvPageReqVO reqVO);

    /** 看板 - 待复核积压（全表口径，不限在库） */
    MesInventoryEnvPendingReviewRespVO selectPendingReview();

    /** 看板 - 污染台账处置时效（只算已闭环的行） */
    MesInventoryEnvLedgerDurationRespVO selectLedgerDuration();

}
