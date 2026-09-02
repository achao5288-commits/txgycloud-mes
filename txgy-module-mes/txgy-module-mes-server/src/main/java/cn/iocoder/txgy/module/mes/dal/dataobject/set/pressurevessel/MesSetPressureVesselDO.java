package cn.iocoder.txgy.module.mes.dal.dataobject.set.pressurevessel;

import cn.iocoder.txgy.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * MES 安全环保检测-压力容器检测记录 DO
 *
 * @author OPENLAB BS
 */
@TableName("mes_set_pressure_vessel")
@KeySequence("mes_set_pressure_vessel_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesSetPressureVesselDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 检验记录/报告编号
     */
    private String recordNo;
    /**
     * 关联检测计划编号
     */
    private Long planId;
    /**
     * 关联设备编号(特种设备台账)
     */
    private Long deviceId;
    /**
     * 压力容器使用登记证号(特种设备注册代码)
     */
    private String vesselRegNo;
    /**
     * 壁厚测定最小壁厚 mm
     */
    private BigDecimal wallThickness;
    /**
     * 无损检测方法：UT/RT/MT/PT(多选逗号分隔)
     */
    private String ndtMethods;
    /**
     * 无损检测结果JSON文本 如 {UT:PASS,MT:PASS}
     */
    private String ndtResults;
    /**
     * 安全阀校验合格：1是/0否
     */
    private Integer safetyValveOk;
    /**
     * 耐压试验压力 MPa
     */
    private BigDecimal pressureTestValue;
    /**
     * 耐压试验结果 PASS/FAIL
     */
    private String pressureTestResult;
    /**
     * 工艺允许压力 MPa
     */
    private BigDecimal processPressure;
    /**
     * 综合结论 PASS/FAIL
     */
    private String result;
    /**
     * 检验机构(需资质)
     */
    private String inspectOrg;
    /**
     * 下次检验日期
     */
    private LocalDate nextInspectDate;
    /**
     * 检验报告文件 URL
     */
    private String reportFileUrl;
    /**
     * 检验人
     */
    private String inspector;
    /**
     * 检验时间
     */
    private LocalDateTime inspectTime;
    /**
     * 备注
     */
    private String remark;

}
