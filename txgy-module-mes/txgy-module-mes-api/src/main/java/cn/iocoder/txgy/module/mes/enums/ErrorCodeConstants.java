package cn.iocoder.txgy.module.mes.enums;

import cn.iocoder.txgy.framework.common.exception.ErrorCode;

/**
 * MES 错误码枚举类
 * <p>
 * mes 系统，使用 1-040-000-000 段
 */
public interface ErrorCodeConstants {

    // ========== MES 基础数据-物料分类（1-040-100-000） ==========
    ErrorCode MD_ITEM_TYPE_NOT_EXISTS = new ErrorCode(1_040_100_000, "物料分类不存在");
    ErrorCode MD_ITEM_TYPE_EXITS_CHILDREN = new ErrorCode(1_040_100_001, "存在子分类，无法删除");
    ErrorCode MD_ITEM_TYPE_PARENT_NOT_EXITS = new ErrorCode(1_040_100_002, "父级分类不存在");
    ErrorCode MD_ITEM_TYPE_PARENT_ERROR = new ErrorCode(1_040_100_003, "不能设置自己为父分类");
    ErrorCode MD_ITEM_TYPE_NAME_DUPLICATE = new ErrorCode(1_040_100_004, "同一父分类下已存在该名称的分类");
    ErrorCode MD_ITEM_TYPE_CODE_DUPLICATE = new ErrorCode(1_040_100_005, "同一父分类下已存在该编码的分类");
    ErrorCode MD_ITEM_TYPE_PARENT_IS_CHILD = new ErrorCode(1_040_100_006, "不能设置自己的子分类为父分类");
    ErrorCode MD_ITEM_TYPE_EXITS_ITEM = new ErrorCode(1_040_100_007, "该分类下存在物料，无法删除");
    ErrorCode MD_ITEM_TYPE_NOT_LEAF = new ErrorCode(1_040_100_008, "只能将物料挂载到叶子分类（该分类下存在子分类）");

    // ========== MES 基础数据-计量单位（1-040-101-000） ==========
    ErrorCode MD_UNIT_MEASURE_NOT_EXISTS = new ErrorCode(1_040_101_000, "计量单位不存在");
    ErrorCode MD_UNIT_MEASURE_CODE_DUPLICATE = new ErrorCode(1_040_101_001, "计量单位编码已存在");
    ErrorCode MD_UNIT_MEASURE_HAS_ITEM = new ErrorCode(1_040_101_002, "该计量单位下存在物料，无法删除");
    ErrorCode MD_UNIT_MEASURE_HAS_SECONDARY = new ErrorCode(1_040_101_003, "该主单位下存在辅单位，无法删除");
    ErrorCode MD_UNIT_MEASURE_HAS_TASK_ISSUE = new ErrorCode(1_040_101_004, "该计量单位已被生产投料引用，无法删除");
    ErrorCode MD_UNIT_MEASURE_HAS_QC_TEMPLATE_INDICATOR = new ErrorCode(1_040_101_005, "该计量单位已被质检方案指标项引用，无法删除");
    ErrorCode MD_UNIT_MEASURE_HAS_QC_LINE = new ErrorCode(1_040_101_006, "该计量单位已被质检单据行引用，无法删除");

    // ========== MES 基础数据-物料（1-040-102-000） ==========
    ErrorCode MD_ITEM_NOT_EXISTS = new ErrorCode(1_040_102_000, "物料不存在");
    ErrorCode MD_ITEM_CODE_DUPLICATE = new ErrorCode(1_040_102_001, "物料编码已存在");
    ErrorCode MD_ITEM_NAME_DUPLICATE = new ErrorCode(1_040_102_002, "物料名称已存在");
    ErrorCode MD_ITEM_IMPORT_LIST_IS_EMPTY = new ErrorCode(1_040_102_003, "导入物料数据不能为空");
    ErrorCode MD_ITEM_BATCH_REQUIRED = new ErrorCode(1_040_102_004, "当前物料启用了批次管理，请选择批次");
    ErrorCode MD_ITEM_IS_DISABLE = new ErrorCode(1_040_102_005, "物料已禁用");

    // ========== MES 基础数据-物料批次属性配置（1-040-102-100） ==========
    ErrorCode MD_ITEM_BATCH_CONFIG_NOT_EXISTS = new ErrorCode(1_040_102_100, "物料批次属性配置不存在");
    ErrorCode MD_ITEM_BATCH_CONFIG_AT_LEAST_ONE_FLAG = new ErrorCode(1_040_102_101, "批次管理已启用，至少需要配置一个批次属性");
    ErrorCode MD_ITEM_PRODUCT_BOM_REQUIRED = new ErrorCode(1_040_102_102, "产品类物料启用前，必须配置至少一个 BOM 组成");

    // ========== MES 仓库管理-批次管理（1-040-717-000） ==========
    ErrorCode WM_BATCH_PRODUCE_DATE_REQUIRED = new ErrorCode(1_040_717_000, "批次配置要求生产日期不能为空");
    ErrorCode WM_BATCH_RECEIPT_DATE_REQUIRED = new ErrorCode(1_040_717_001, "批次配置要求入库日期不能为空");
    ErrorCode WM_BATCH_EXPIRE_DATE_REQUIRED = new ErrorCode(1_040_717_002, "批次配置要求有效期不能为空");
    ErrorCode WM_BATCH_VENDOR_REQUIRED = new ErrorCode(1_040_717_003, "批次配置要求供应商不能为空");
    ErrorCode WM_BATCH_CLIENT_REQUIRED = new ErrorCode(1_040_717_004, "批次配置要求客户不能为空");
    ErrorCode WM_BATCH_PURCHASE_ORDER_CODE_REQUIRED = new ErrorCode(1_040_717_005, "批次配置要求采购订单编号不能为空");
    ErrorCode WM_BATCH_CUSTOMER_ORDER_CODE_REQUIRED = new ErrorCode(1_040_717_006, "批次配置要求销售订单编号不能为空");
    ErrorCode WM_BATCH_WORK_ORDER_REQUIRED = new ErrorCode(1_040_717_007, "批次配置要求生产工单不能为空");
    ErrorCode WM_BATCH_TASK_REQUIRED = new ErrorCode(1_040_717_008, "批次配置要求生产任务不能为空");
    ErrorCode WM_BATCH_WORKSTATION_REQUIRED = new ErrorCode(1_040_717_009, "批次配置要求工作站不能为空");
    ErrorCode WM_BATCH_TOOL_REQUIRED = new ErrorCode(1_040_717_010, "批次配置要求工具不能为空");
    ErrorCode WM_BATCH_MOLD_REQUIRED = new ErrorCode(1_040_717_011, "批次配置要求模具不能为空");
    ErrorCode WM_BATCH_LOT_NUMBER_REQUIRED = new ErrorCode(1_040_717_012, "批次配置要求生产批号不能为空");
    ErrorCode WM_BATCH_QUALITY_STATUS_REQUIRED = new ErrorCode(1_040_717_013, "批次配置要求质量状态不能为空");
    ErrorCode WM_BATCH_NOT_EXISTS = new ErrorCode(1_040_717_014, "批次不存在");
    ErrorCode WM_BATCH_ITEM_MISMATCH = new ErrorCode(1_040_717_015, "批次不属于当前物料");
    ErrorCode WM_BATCH_CLIENT_MISMATCH = new ErrorCode(1_040_717_016, "批次不属于当前客户");
    ErrorCode WM_BATCH_VENDOR_MISMATCH = new ErrorCode(1_040_717_017, "批次不属于当前供应商");

    // ========== MES 基础数据-客户（1-040-103-000） ==========
    ErrorCode MD_CLIENT_NOT_EXISTS = new ErrorCode(1_040_103_000, "客户不存在");
    ErrorCode MD_CLIENT_CODE_DUPLICATE = new ErrorCode(1_040_103_001, "客户编码已存在");
    ErrorCode MD_CLIENT_NAME_DUPLICATE = new ErrorCode(1_040_103_002, "客户名称已存在");
    ErrorCode MD_CLIENT_NICKNAME_DUPLICATE = new ErrorCode(1_040_103_003, "客户简称已存在");
    ErrorCode MD_CLIENT_IMPORT_LIST_IS_EMPTY = new ErrorCode(1_040_103_004, "导入客户数据不能为空");
    ErrorCode MD_CLIENT_IS_DISABLE = new ErrorCode(1_040_103_005, "客户已禁用");

    // ========== MES 基础数据-供应商（1-040-104-000） ==========
    ErrorCode MD_VENDOR_NOT_EXISTS = new ErrorCode(1_040_104_000, "供应商不存在");
    ErrorCode MD_VENDOR_CODE_DUPLICATE = new ErrorCode(1_040_104_001, "供应商编码已存在");
    ErrorCode MD_VENDOR_NAME_DUPLICATE = new ErrorCode(1_040_104_002, "供应商名称已存在");
    ErrorCode MD_VENDOR_NICKNAME_DUPLICATE = new ErrorCode(1_040_104_003, "供应商简称已存在");
    ErrorCode MD_VENDOR_IMPORT_LIST_IS_EMPTY = new ErrorCode(1_040_104_004, "导入供应商数据不能为空");
    ErrorCode MD_VENDOR_HAS_REFERENCE = new ErrorCode(1_040_104_005, "该供应商已被其他业务引用，无法删除");
    ErrorCode MD_VENDOR_IS_DISABLE = new ErrorCode(1_040_104_006, "供应商已禁用");

    // ========== MES 基础数据-车间（1-040-105-000） ==========
    ErrorCode MD_WORKSHOP_NOT_EXISTS = new ErrorCode(1_040_105_000, "车间不存在");
    ErrorCode MD_WORKSHOP_CODE_DUPLICATE = new ErrorCode(1_040_105_001, "车间编码已存在");
    ErrorCode MD_WORKSHOP_NAME_DUPLICATE = new ErrorCode(1_040_105_002, "车间名称已存在");
    ErrorCode MD_WORKSHOP_HAS_WORKSTATION = new ErrorCode(1_040_105_003, "车间下存在工作站，无法删除");

    // ========== MES 基础数据-工作站（1-040-106-000） ==========
    ErrorCode MD_WORKSTATION_NOT_EXISTS = new ErrorCode(1_040_106_000, "工作站不存在");
    ErrorCode MD_WORKSTATION_CODE_DUPLICATE = new ErrorCode(1_040_106_001, "工作站编码已存在");
    ErrorCode MD_WORKSTATION_NAME_DUPLICATE = new ErrorCode(1_040_106_002, "工作站名称已存在");
    ErrorCode MD_WORKSTATION_IS_DISABLE = new ErrorCode(1_040_106_003, "工作站已禁用");
    // ========== MES 基础数据-设备资源（1-040-106-100） ==========
    ErrorCode MD_WORKSTATION_MACHINE_NOT_EXISTS = new ErrorCode(1_040_106_100, "设备资源记录不存在");
    ErrorCode MD_WORKSTATION_MACHINE_EXISTS = new ErrorCode(1_040_106_101, "该设备已分配至工作站：{}");
    // ========== MES 基础数据-工装夹具资源（1-040-106-200） ==========
    ErrorCode MD_WORKSTATION_TOOL_NOT_EXISTS = new ErrorCode(1_040_106_200, "工装夹具资源记录不存在");
    ErrorCode MD_WORKSTATION_TOOL_TYPE_EXISTS = new ErrorCode(1_040_106_201, "该工具类型已在此工作站中存在");
    // ========== MES 基础数据-人力资源（1-040-106-300） ==========
    ErrorCode MD_WORKSTATION_WORKER_NOT_EXISTS = new ErrorCode(1_040_106_300, "人力资源记录不存在");
    ErrorCode MD_WORKSTATION_WORKER_POST_EXISTS = new ErrorCode(1_040_106_301, "该岗位已在此工作站中存在");

    // ========== MES 基础数据-产品BOM（1-040-107-000） ==========
    ErrorCode MD_PRODUCT_BOM_NOT_EXISTS = new ErrorCode(1_040_107_000, "产品BOM不存在");
    ErrorCode MD_PRODUCT_BOM_SELF_REFERENCE = new ErrorCode(1_040_107_001, "产品不能作为自身的BOM物料");
    ErrorCode MD_PRODUCT_BOM_CIRCULAR = new ErrorCode(1_040_107_002, "BOM物料存在闭环，无法新增");
    ErrorCode MD_PRODUCT_BOM_ITEM_INVALID = new ErrorCode(1_040_107_003, "选择的 BOM 物料不属于当前产品");

    // ========== MES 基础数据-产品SOP（1-040-108-000） ==========
    ErrorCode MD_PRODUCT_SOP_NOT_EXISTS = new ErrorCode(1_040_108_000, "产品SOP不存在");
    ErrorCode MD_PRODUCT_SOP_SORT_DUPLICATE = new ErrorCode(1_040_108_001, "该展示序号已存在");

    // ========== MES 基础数据-产品SIP（1-040-109-000） ==========
    ErrorCode MD_PRODUCT_SIP_NOT_EXISTS = new ErrorCode(1_040_109_000, "产品SIP不存在");
    ErrorCode MD_PRODUCT_SIP_SORT_DUPLICATE = new ErrorCode(1_040_109_001, "该展示序号已存在");

    // ========== MES 基础数据-编码规则（1-040-110-000） ==========
    ErrorCode AUTO_CODE_RULE_NOT_EXISTS = new ErrorCode(1_040_110_000, "编码规则不存在");
    ErrorCode AUTO_CODE_RULE_CODE_DUPLICATE = new ErrorCode(1_040_110_001, "规则编码已存在");
    ErrorCode AUTO_CODE_PART_NOT_EXISTS = new ErrorCode(1_040_110_002, "规则组成不存在");
    ErrorCode AUTO_CODE_REDIS_ERROR = new ErrorCode(1_040_110_003, "编码生成服务不可用，请稍后重试");
    ErrorCode AUTO_CODE_GENERATE_FAILED = new ErrorCode(1_040_110_004, "编码生成失败");
    ErrorCode AUTO_CODE_PART_SERIAL_NUMBER_DUPLICATE = new ErrorCode(1_040_110_005, "流水号分段只能存在一个");

    // ========== MES 日历排班-计划班次（1-040-200-000） ==========
    ErrorCode CAL_PLAN_SHIFT_NOT_EXISTS = new ErrorCode(1_040_200_000, "计划班次不存在");
    ErrorCode CAL_PLAN_SHIFT_COUNT_EXCEED = new ErrorCode(1_040_200_001, "班次数量已达到轮班方式的上限");

    // ========== MES 日历排班-班组（1-040-201-000） ==========
    ErrorCode CAL_TEAM_NOT_EXISTS = new ErrorCode(1_040_201_000, "班组不存在");
    ErrorCode CAL_TEAM_CODE_DUPLICATE = new ErrorCode(1_040_201_001, "班组编码已存在");
    // ========== MES 日历排班-班组成员（1-040-201-100） ==========
    ErrorCode CAL_TEAM_MEMBER_NOT_EXISTS = new ErrorCode(1_040_201_100, "班组成员不存在");
    ErrorCode CAL_TEAM_MEMBER_USER_DUPLICATE = new ErrorCode(1_040_201_101, "该用户已分配到其他班组");
    ErrorCode CAL_TEAM_MEMBER_USER_NOT_EXISTS = new ErrorCode(1_040_201_102, "用户不存在");
    // ========== MES 日历排班-班组排班（1-040-201-200） ==========
    ErrorCode CAL_TEAM_SHIFT_NOT_EXISTS = new ErrorCode(1_040_201_200, "班组排班记录不存在");
    ErrorCode CAL_TEAM_SHIFT_GENERATE_TEAM_NOT_ENOUGH = new ErrorCode(1_040_201_201, "班组数量不满足轮班方式要求");
    ErrorCode CAL_TEAM_SHIFT_GENERATE_SHIFT_NOT_ENOUGH = new ErrorCode(1_040_201_202, "班次数量不满足轮班方式要求");

    // ========== MES 日历排班-排班计划（1-040-202-000） ==========
    ErrorCode CAL_PLAN_NOT_EXISTS = new ErrorCode(1_040_202_000, "排班计划不存在");
    ErrorCode CAL_PLAN_CODE_DUPLICATE = new ErrorCode(1_040_202_001, "排班计划编码已存在");
    ErrorCode CAL_PLAN_NOT_PREPARE = new ErrorCode(1_040_202_002, "排班计划已确认，不允许修改或删除");
    ErrorCode CAL_PLAN_TEAM_COUNT_NOT_MATCH = new ErrorCode(1_040_202_003, "确认排班计划时，分配的班组数量与轮班方式不匹配");
    // ========== MES 日历排班-计划班组关联（1-040-202-100） ==========
    ErrorCode CAL_PLAN_TEAM_NOT_EXISTS = new ErrorCode(1_040_202_100, "计划班组关联不存在");
    ErrorCode CAL_PLAN_TEAM_DUPLICATE = new ErrorCode(1_040_202_101, "该班组已分配到此计划");

    // ========== MES 日历排班-假期设置（1-040-203-000） ==========
    ErrorCode CAL_HOLIDAY_NOT_EXISTS = new ErrorCode(1_040_203_000, "假期设置不存在");

    // ========== MES 设备管理-设备类型（1-040-300-000） ==========
    ErrorCode DV_MACHINERY_TYPE_NOT_EXISTS = new ErrorCode(1_040_300_000, "设备类型不存在");
    ErrorCode DV_MACHINERY_TYPE_EXITS_CHILDREN = new ErrorCode(1_040_300_001, "存在子类型，无法删除");
    ErrorCode DV_MACHINERY_TYPE_PARENT_NOT_EXITS = new ErrorCode(1_040_300_002, "父级类型不存在");
    ErrorCode DV_MACHINERY_TYPE_PARENT_ERROR = new ErrorCode(1_040_300_003, "不能设置自己为父类型");
    ErrorCode DV_MACHINERY_TYPE_NAME_DUPLICATE = new ErrorCode(1_040_300_004, "同一父类型下已存在该名称");
    ErrorCode DV_MACHINERY_TYPE_CODE_DUPLICATE = new ErrorCode(1_040_300_005, "设备类型编码已存在");
    ErrorCode DV_MACHINERY_TYPE_PARENT_IS_CHILD = new ErrorCode(1_040_300_006, "不能设置自己的子类型为父类型");
    ErrorCode DV_MACHINERY_TYPE_HAS_MACHINERY = new ErrorCode(1_040_300_007, "该类型下存在设备，无法删除");

    // ========== MES 设备管理-设备台账（1-040-301-000） ==========
    ErrorCode DV_MACHINERY_NOT_EXISTS = new ErrorCode(1_040_301_000, "设备不存在");
    ErrorCode DV_MACHINERY_CODE_DUPLICATE = new ErrorCode(1_040_301_001, "设备编码已存在");
    ErrorCode DV_MACHINERY_IS_DISABLE = new ErrorCode(1_040_301_007, "设备已禁用");
    ErrorCode DV_MACHINERY_IMPORT_LIST_IS_EMPTY = new ErrorCode(1_040_301_002, "导入设备数据不能为空");
    ErrorCode DV_MACHINERY_HAS_CHECK_PLAN = new ErrorCode(1_040_301_003, "设备已关联点检计划，无法删除");
    ErrorCode DV_MACHINERY_HAS_CHECK_RECORD = new ErrorCode(1_040_301_004, "设备已关联点检记录，无法删除");
    ErrorCode DV_MACHINERY_HAS_MAINTEN_RECORD = new ErrorCode(1_040_301_005, "设备已关联保养记录，无法删除");
    ErrorCode DV_MACHINERY_HAS_REPAIR = new ErrorCode(1_040_301_006, "设备已关联维修工单，无法删除");

    // ========== MES 设备管理-点检保养项目（1-040-304-000） ==========
    ErrorCode DV_SUBJECT_NOT_EXISTS = new ErrorCode(1_040_304_000, "点检保养项目不存在");
    ErrorCode DV_SUBJECT_CODE_DUPLICATE = new ErrorCode(1_040_304_001, "项目编码已存在");
    ErrorCode DV_SUBJECT_USED_BY_CHECK_PLAN = new ErrorCode(1_040_304_002, "点检保养项目已被点检保养方案使用，无法删除");
    ErrorCode DV_SUBJECT_IS_DISABLE = new ErrorCode(1_040_304_003, "点检保养项目已禁用");

    // ========== MES 设备管理-点检计划（1-040-302-000） ==========
    ErrorCode DV_CHECK_PLAN_NOT_EXISTS = new ErrorCode(1_040_302_000, "点检计划不存在");
    ErrorCode DV_CHECK_PLAN_CODE_DUPLICATE = new ErrorCode(1_040_302_001, "点检保养方案编码已存在");
    ErrorCode DV_CHECK_PLAN_NOT_PREPARE = new ErrorCode(1_040_302_002, "点检保养方案已启用，不允许修改或删除");
    ErrorCode DV_CHECK_PLAN_NO_MACHINERY = new ErrorCode(1_040_302_003, "启用方案时，至少需要关联一台设备");
    ErrorCode DV_CHECK_PLAN_NO_SUBJECT = new ErrorCode(1_040_302_004, "启用方案时，至少需要关联一个点检保养项目");
    ErrorCode DV_CHECK_PLAN_NOT_ENABLED = new ErrorCode(1_040_302_005, "点检保养方案未启用，不允许停用");
    ErrorCode DV_CHECK_PLAN_TYPE_MISMATCH = new ErrorCode(1_040_302_006, "点检保养方案类型与当前业务不匹配");
    ErrorCode DV_CHECK_PLAN_NOT_ENABLED_FOR_RECORD = new ErrorCode(1_040_302_007, "点检保养方案未启用，不允许创建记录");
    // ========== MES 设备管理-点检方案设备（1-040-302-100） ==========
    ErrorCode DV_CHECK_PLAN_MACHINERY_NOT_EXISTS = new ErrorCode(1_040_302_100, "点检保养方案设备不存在");
    ErrorCode DV_CHECK_PLAN_MACHINERY_DUPLICATE = new ErrorCode(1_040_302_101, "该设备已关联到当前方案，请勿重复添加");
    ErrorCode DV_CHECK_PLAN_MACHINERY_EXISTS_IN_SAME_TYPE = new ErrorCode(1_040_302_102, "该设备已存在于同类型的其他启用的或草稿的方案中，不允许同一设备添加多个同类型的方案");
    // ========== MES 设备管理-点检方案项目（1-040-302-200） ==========
    ErrorCode DV_CHECK_PLAN_SUBJECT_NOT_EXISTS = new ErrorCode(1_040_302_200, "点检保养方案项目不存在");
    ErrorCode DV_CHECK_PLAN_SUBJECT_DUPLICATE = new ErrorCode(1_040_302_201, "该项目已关联到当前方案，请勿重复添加");

    // ========== MES 设备管理-维修工单（1-040-303-000） ==========
    ErrorCode DV_REPAIR_NOT_EXISTS = new ErrorCode(1_040_303_000, "维修工单不存在");
    ErrorCode DV_REPAIR_NOT_PREPARE = new ErrorCode(1_040_303_001, "维修工单不是草稿状态，不允许修改或删除");
    ErrorCode DV_REPAIR_CODE_DUPLICATE = new ErrorCode(1_040_303_002, "维修工单编码已存在");
    ErrorCode DV_REPAIR_NOT_CONFIRMED = new ErrorCode(1_040_303_003, "只有维修中状态的维修工单才能完成维修");
    ErrorCode DV_REPAIR_NOT_APPROVING = new ErrorCode(1_040_303_004, "只有待验收状态的维修工单才能验收");
    // ========== MES 设备管理-维修工单行（1-040-303-100） ==========
    ErrorCode DV_REPAIR_LINE_NOT_EXISTS = new ErrorCode(1_040_303_100, "维修工单行不存在");

    // ========== MES 设备管理-保养记录（1-040-305-000） ==========
    ErrorCode MAINTEN_RECORD_NOT_EXISTS = new ErrorCode(1_040_305_000, "设备保养记录不存在");
    ErrorCode MAINTEN_RECORD_NOT_DRAFT = new ErrorCode(1_040_305_001, "设备保养记录已提交，不允许修改或删除");
    ErrorCode MAINTEN_RECORD_NO_LINE = new ErrorCode(1_040_305_002, "提交保养记录时，至少需要一条保养项目");
    // ========== MES 设备管理-保养记录明细（1-040-305-100） ==========
    ErrorCode MAINTEN_RECORD_LINE_NOT_EXISTS = new ErrorCode(1_040_305_100, "设备保养记录明细不存在");

    // ========== MES 设备管理-点检记录（1-040-306-000） ==========
    ErrorCode DV_CHECK_RECORD_NOT_EXISTS = new ErrorCode(1_040_306_000, "设备点检记录不存在");
    ErrorCode DV_CHECK_RECORD_NOT_DRAFT = new ErrorCode(1_040_306_001, "设备点检记录已完成，不允许修改或删除");
    ErrorCode DV_CHECK_RECORD_NO_LINE = new ErrorCode(1_040_306_002, "提交点检记录时，至少需要一条点检项目");
    // ========== MES 设备管理-点检记录明细（1-040-306-100） ==========
    ErrorCode DV_CHECK_RECORD_LINE_NOT_EXISTS = new ErrorCode(1_040_306_100, "设备点检记录明细不存在");

    // ========== MES 工具管理-工具类型（1-040-400-000） ==========
    ErrorCode TM_TOOL_TYPE_NOT_EXISTS = new ErrorCode(1_040_400_000, "工具类型不存在");
    ErrorCode TM_TOOL_TYPE_CODE_DUPLICATE = new ErrorCode(1_040_400_001, "工具类型编码已存在");
    ErrorCode TM_TOOL_TYPE_NAME_DUPLICATE = new ErrorCode(1_040_400_002, "工具类型名称已存在");
    ErrorCode TM_TOOL_TYPE_HAS_TOOL = new ErrorCode(1_040_400_003, "该工具类型下存在工具，无法删除");
    ErrorCode TM_TOOL_TYPE_HAS_WORKSTATION_TOOL = new ErrorCode(1_040_400_004, "该工具类型已被工作站工装资源引用，无法删除");

    // ========== MES 工具管理-工具台账（1-040-401-000） ==========
    ErrorCode TM_TOOL_NOT_EXISTS = new ErrorCode(1_040_401_000, "工具不存在");
    ErrorCode TM_TOOL_CODE_DUPLICATE = new ErrorCode(1_040_401_001, "工具编码已存在");
    ErrorCode TM_TOOL_HAS_BATCH = new ErrorCode(1_040_401_002, "该工具已被批次引用，无法删除");

    // ========== MES 生产管理-工序（1-040-500-000） ==========
    ErrorCode PRO_PROCESS_NOT_EXISTS = new ErrorCode(1_040_500_000, "工序不存在");
    ErrorCode PRO_PROCESS_CODE_EXISTS = new ErrorCode(1_040_500_001, "工序编码已存在");
    ErrorCode PRO_PROCESS_NAME_EXISTS = new ErrorCode(1_040_500_002, "工序名称已存在");
    ErrorCode PRO_PROCESS_USED_BY_ROUTE = new ErrorCode(1_040_500_003, "工序已被工艺路线引用，无法删除");
    ErrorCode PRO_PROCESS_IS_DISABLE = new ErrorCode(1_040_500_004, "工序已禁用");
    // ========== MES 生产管理-工序内容（1-040-500-100） ==========
    ErrorCode PRO_PROCESS_CONTENT_NOT_EXISTS = new ErrorCode(1_040_500_100, "工序内容不存在");

    // ========== MES 生产管理-工艺路线（1-040-501-000） ==========
    ErrorCode PRO_ROUTE_NOT_EXISTS = new ErrorCode(1_040_501_000, "工艺路线不存在");
    ErrorCode PRO_ROUTE_CODE_DUPLICATE = new ErrorCode(1_040_501_001, "工艺路线编码已存在");
    ErrorCode PRO_ROUTE_ENABLE_NO_PROCESS = new ErrorCode(1_040_501_002, "请先添加组成工序");
    ErrorCode PRO_ROUTE_ENABLE_NO_KEY_PROCESS = new ErrorCode(1_040_501_003, "工艺路线必须要有关键工序");
    ErrorCode PRO_ROUTE_ENABLE_PRODUCT_NO_BOM = new ErrorCode(1_040_501_004, "产品 {} 未配置工序的 BOM 消耗");
    ErrorCode PRO_ROUTE_IS_ENABLE = new ErrorCode(1_040_501_005, "工艺路线已启用，不允许操作");
    // ========== MES 生产管理-工艺路线工序（1-040-501-100） ==========
    ErrorCode PRO_ROUTE_PROCESS_NOT_EXISTS = new ErrorCode(1_040_501_100, "工艺路线工序不存在");
    ErrorCode PRO_ROUTE_PROCESS_SORT_DUPLICATE = new ErrorCode(1_040_501_101, "序号已存在");
    ErrorCode PRO_ROUTE_PROCESS_DUPLICATE = new ErrorCode(1_040_501_102, "不能重复添加工序");
    ErrorCode PRO_ROUTE_PROCESS_KEY_DUPLICATE = new ErrorCode(1_040_501_103, "当前工艺路线已经指定过关键工序");
    // ========== MES 生产管理-工艺路线产品（1-040-501-200） ==========
    ErrorCode PRO_ROUTE_PRODUCT_NOT_EXISTS = new ErrorCode(1_040_501_200, "工艺路线产品不存在");
    ErrorCode PRO_ROUTE_PRODUCT_ITEM_DUPLICATE = new ErrorCode(1_040_501_201, "此产品已配置了工艺路线");
    // ========== MES 生产管理-工艺路线产品BOM（1-040-501-300） ==========
    ErrorCode PRO_ROUTE_PRODUCT_BOM_NOT_EXISTS = new ErrorCode(1_040_501_300, "工艺路线产品 BOM 不存在");
    ErrorCode PRO_ROUTE_PRODUCT_BOM_DUPLICATE = new ErrorCode(1_040_501_301, "当前 BOM 物料在此工序已经配置过");

    // ========== MES 生产管理-生产工单（1-040-502-000） ==========
    ErrorCode PRO_WORK_ORDER_NOT_EXISTS = new ErrorCode(1_040_502_000, "生产工单不存在");
    ErrorCode PRO_WORK_ORDER_CODE_DUPLICATE = new ErrorCode(1_040_502_001, "生产工单编码已存在");
    ErrorCode PRO_WORK_ORDER_NOT_PREPARE = new ErrorCode(1_040_502_002, "只有草稿状态的工单才能执行此操作");
    ErrorCode PRO_WORK_ORDER_NOT_CONFIRMED = new ErrorCode(1_040_502_003, "只有已确认状态的工单才能执行此操作");
    ErrorCode PRO_WORK_ORDER_HAS_CHILDREN = new ErrorCode(1_040_502_004, "存在子工单，无法删除");
    ErrorCode PRO_WORK_ORDER_PRODUCT_MISMATCH = new ErrorCode(1_040_502_005, "当前产品物料与生产工单产品不一致");
    ErrorCode PRO_WORK_ORDER_BOM_NOT_EXISTS = new ErrorCode(1_040_502_100, "生产工单BOM不存在");

    // ========== MES 生产管理-生产任务（1-040-503-000） ==========
    ErrorCode PRO_TASK_NOT_EXISTS = new ErrorCode(1_040_503_000, "生产任务不存在");
    ErrorCode PRO_TASK_ALREADY_FINISHED = new ErrorCode(1_040_503_001, "生产任务已完成或已取消，不能继续操作");
    ErrorCode PRO_TASK_WORK_ORDER_MISMATCH = new ErrorCode(1_040_503_002, "生产任务不属于当前生产工单");
    ErrorCode PRO_TASK_WORKSTATION_MISMATCH = new ErrorCode(1_040_503_003, "生产任务不属于当前工作站");
    ErrorCode PRO_TASK_ROUTE_PROCESS_MISMATCH = new ErrorCode(1_040_503_004, "生产任务与当前工艺路线或工序不一致");
    ErrorCode PRO_TASK_ITEM_MISMATCH = new ErrorCode(1_040_503_005, "生产任务产品与当前产品物料不一致");
    ErrorCode PRO_WORKSTATION_PROCESS_MISMATCH = new ErrorCode(1_040_503_006, "工作站所属工序与当前工序不一致");
    // ========== MES 生产管理-生产任务投料（1-040-503-100） ==========
    ErrorCode PRO_TASK_ISSUE_NOT_EXISTS = new ErrorCode(1_040_503_100, "生产任务投料记录不存在");

    // ========== MES 生产管理-安灯呼叫配置（1-040-504-000） ==========
    ErrorCode PRO_ANDON_CONFIG_NOT_EXISTS = new ErrorCode(1_040_504_000, "安灯呼叫配置不存在");

    // ========== MES 生产管理-安灯呼叫记录（1-040-505-000） ==========
    ErrorCode PRO_ANDON_RECORD_NOT_EXISTS = new ErrorCode(1_040_505_000, "安灯呼叫记录不存在");
    ErrorCode PRO_ANDON_RECORD_ALREADY_HANDLED = new ErrorCode(1_040_505_001, "安灯记录已处置，不允许重复处置");
    ErrorCode PRO_ANDON_RECORD_HANDLE_TIME_REQUIRED = new ErrorCode(1_040_505_002, "标记已处置时，处置时间不能为空");
    ErrorCode PRO_ANDON_RECORD_HANDLER_USER_REQUIRED = new ErrorCode(1_040_505_003, "标记已处置时，处置人不能为空");

    // ========== MES 生产管理-生产报工（1-040-506-000） ==========
    ErrorCode PRO_FEEDBACK_NOT_EXISTS = new ErrorCode(1_040_506_000, "生产报工不存在");
    ErrorCode PRO_FEEDBACK_NOT_PREPARE = new ErrorCode(1_040_506_001, "只能修改或删除草稿状态的报工单");
    ErrorCode PRO_FEEDBACK_NOT_APPROVING = new ErrorCode(1_040_506_002, "只有审批中状态的报工单才能执行此操作");
    ErrorCode PRO_FEEDBACK_NOT_UNCHECK = new ErrorCode(1_040_506_003, "只有待检验状态的报工单才能完成检验");
    ErrorCode PRO_FEEDBACK_QUANTITY_EXCEED = new ErrorCode(1_040_506_004, "报工数量不能超过排产数量");
    ErrorCode PRO_FEEDBACK_STATUS_ERROR = new ErrorCode(1_040_506_005, "报工单状态不正确，无法执行此操作");
    ErrorCode PRO_FEEDBACK_WORK_ORDER_NOT_CONFIRMED = new ErrorCode(1_040_506_006, "关联的工单未确认，无法创建报工");
    ErrorCode PRO_FEEDBACK_QUALIFIED_UNQUALIFIED_MISMATCH = new ErrorCode(1_040_506_007, "合格品数量与不良品数量之和必须等于报工数量");
    ErrorCode PRO_FEEDBACK_ROUTE_PROCESS_INVALID = new ErrorCode(1_040_506_008, "未找到对应的工艺工序配置，请检查工艺路线与工序");
    ErrorCode PRO_FEEDBACK_TASK_OR_ORDER_FINISHED = new ErrorCode(1_040_506_009, "当前生产任务或工单已完成，不能继续报工");
    ErrorCode PRO_FEEDBACK_QUANTITY_MUST_POSITIVE = new ErrorCode(1_040_506_010, "报工数量必须大于 0");
    ErrorCode PRO_FEEDBACK_QUALIFIED_UNQUALIFIED_REQUIRED = new ErrorCode(1_040_506_011, "请输入合格品和不良品数量，且合计须大于 0");
    ErrorCode PRO_FEEDBACK_UNCHECK_QUANTITY_EXISTS = new ErrorCode(1_040_506_012, "当前报工单未完成检验（待检数量：{}），无法执行报工");

    // ========== MES 生产管理-生产流转卡（1-040-507-000） ==========
    ErrorCode PRO_CARD_NOT_EXISTS = new ErrorCode(1_040_507_000, "生产流转卡不存在");
    ErrorCode PRO_CARD_CODE_DUPLICATE = new ErrorCode(1_040_507_001, "流转卡编码已存在");
    ErrorCode PRO_CARD_STATUS_ERROR = new ErrorCode(1_040_507_002, "流转卡状态不正确");
    ErrorCode PRO_CARD_CANCEL_NOT_ALLOWED = new ErrorCode(1_040_507_003, "已完成或已取消的流转卡不允许取消");
    ErrorCode PRO_CARD_NOT_PREPARE = new ErrorCode(1_040_507_004, "流转卡不是草稿状态，不允许修改或删除");
    // ========== MES 生产管理-流转卡工序（1-040-507-100） ==========
    ErrorCode PRO_CARD_PROCESS_NOT_EXISTS = new ErrorCode(1_040_507_100, "流转卡工序记录不存在");

    // ========== MES 生产管理-工作记录（1-040-508-000） ==========
    ErrorCode WORK_RECORD_NOT_CLOCK_IN = new ErrorCode(1_040_508_001, "当前用户未上工，无法下工");
    ErrorCode WORK_RECORD_ALREADY_CLOCK_IN = new ErrorCode(1_040_508_002, "当前用户已上工，请先下工再操作");

    // ========== MES 生产管理-项目管理（1-040-509-000） ==========
    ErrorCode PRO_PROJECT_NOT_EXISTS = new ErrorCode(1_040_509_000, "项目不存在");
    ErrorCode PRO_PROJECT_CODE_DUPLICATE = new ErrorCode(1_040_509_001, "项目编码已存在");
    ErrorCode PRO_PROJECT_HAS_WORK_ORDERS = new ErrorCode(1_040_509_002, "项目下已关联生产工单，无法删除");
    ErrorCode PRO_PROJECT_WORK_ORDER_NOT_EXISTS = new ErrorCode(1_040_509_100, "关联的生产工单不存在");
    ErrorCode PRO_PROJECT_WORK_ORDER_CANCELED = new ErrorCode(1_040_509_101, "已取消的工单不允许挂接项目");
    ErrorCode PRO_PROJECT_STATUS_CLOSED = new ErrorCode(1_040_509_102, "项目已完成或已取消，不允许挂接生产工单");
    ErrorCode PRO_PROJECT_STATUS_FORBIDDEN = new ErrorCode(1_040_509_103, "项目状态不允许该操作，请刷新后重试");

    // ========== MES 质量管理-质检方案（1-040-600-000） ==========
    ErrorCode QC_TEMPLATE_NOT_EXISTS = new ErrorCode(1_040_600_000, "质检方案不存在");
    ErrorCode QC_TEMPLATE_CODE_DUPLICATE = new ErrorCode(1_040_600_001, "质检方案编号已存在");
    // ========== MES 质量管理-质检方案检测指标项（1-040-600-100） ==========
    ErrorCode QC_TEMPLATE_INDICATOR_NOT_EXISTS = new ErrorCode(1_040_600_100, "质检方案检测指标项不存在");
    // ========== MES 质量管理-质检方案产品关联（1-040-600-200） ==========
    ErrorCode QC_TEMPLATE_ITEM_NOT_EXISTS = new ErrorCode(1_040_600_200, "质检方案产品关联不存在");
    ErrorCode QC_TEMPLATE_ITEM_DUPLICATE = new ErrorCode(1_040_600_201, "该产品已关联此质检方案");

    // ========== MES 质量管理-质检指标（1-040-601-000） ==========
    ErrorCode QC_INDICATOR_NOT_EXISTS = new ErrorCode(1_040_601_000, "质检指标不存在");
    ErrorCode QC_INDICATOR_CODE_DUPLICATE = new ErrorCode(1_040_601_001, "质检指标编码已存在");
    ErrorCode QC_INDICATOR_NAME_DUPLICATE = new ErrorCode(1_040_601_002, "质检指标名称已存在");
    ErrorCode QC_INDICATOR_RESULT_SPECIFICATION_REQUIRED = new ErrorCode(1_040_601_003, "结果值属性不能为空");

    // ========== MES 质量管理-缺陷类型（1-040-602-000） ==========
    ErrorCode QC_DEFECT_NOT_EXISTS = new ErrorCode(1_040_602_000, "缺陷类型不存在");
    ErrorCode QC_DEFECT_CODE_DUPLICATE = new ErrorCode(1_040_602_001, "缺陷类型编码已存在");
    ErrorCode QC_DEFECT_NAME_DUPLICATE = new ErrorCode(1_040_602_002, "缺陷类型名称已存在");

    // ========== MES 质量管理-来料检验 IQC（1-040-603-000） ==========
    ErrorCode QC_IQC_NOT_EXISTS = new ErrorCode(1_040_603_000, "来料检验单不存在");
    ErrorCode QC_IQC_CODE_DUPLICATE = new ErrorCode(1_040_603_001, "来料检验单编号已存在");
    ErrorCode QC_IQC_NOT_PREPARE = new ErrorCode(1_040_603_002, "只有草稿状态的检验单才可操作");
    ErrorCode QC_IQC_QUANTITY_MISMATCH = new ErrorCode(1_040_603_004, "合格品与不合格品数量之和须等于检测数量");
    ErrorCode QC_NO_TEMPLATE = new ErrorCode(1_040_603_005, "当前产品未配置检测模板");
    ErrorCode QC_IQC_SOURCE_DOC_PARAMS_MISSING = new ErrorCode(1_040_603_006, "来源单据类型非空时，来源单据 ID 和来源单据行 ID 不能为空");
    ErrorCode QC_IQC_CHECK_RESULT_EMPTY = new ErrorCode(1_040_603_007, "完成检验单前，检测结果必须填写");
    // ========== MES 质量管理-来料检验行（1-040-603-100） ==========
    ErrorCode QC_IQC_LINE_NOT_EXISTS = new ErrorCode(1_040_603_100, "来料检验行不存在");

    // ========== MES 质量管理-过程检验 IPQC（1-040-604-000） ==========
    ErrorCode QC_IPQC_NOT_EXISTS = new ErrorCode(1_040_604_000, "过程检验单不存在");
    ErrorCode QC_IPQC_CODE_DUPLICATE = new ErrorCode(1_040_604_001, "过程检验单编号已存在");
    ErrorCode QC_IPQC_NOT_PREPARE = new ErrorCode(1_040_604_002, "只有草稿状态的检验单才可操作");
    ErrorCode QC_IPQC_QUANTITY_MISMATCH = new ErrorCode(1_040_604_004, "合格品与不合格品数量之和须等于检测数量");
    ErrorCode QC_IPQC_NO_TEMPLATE = new ErrorCode(1_040_604_005, "当前产品未配置 IPQC 检测模板");
    ErrorCode QC_IPQC_CHECK_RESULT_EMPTY = new ErrorCode(1_040_604_006, "完成检验单前，检测结果必须填写");
    ErrorCode QC_IPQC_SOURCE_DOC_TYPE_UNKNOWN = new ErrorCode(1_040_604_007, "未知的 IPQC 来源单据类型");
    ErrorCode QC_IPQC_SOURCE_DOC_NO_PENDING_LINE = new ErrorCode(1_040_604_008, "来源报工单不存在待检产出行，无法创建 IPQC");
    ErrorCode QC_IPQC_SOURCE_LINE_NOT_BELONG = new ErrorCode(1_040_604_009, "来源单据行不属于该报工单");
    ErrorCode QC_IPQC_SOURCE_LINE_REQUIRED = new ErrorCode(1_040_604_010, "来源单据类型为报工时，来源产出行 ID 不能为空");
    ErrorCode QC_IPQC_SOURCE_LINE_NOT_PENDING = new ErrorCode(1_040_604_011, "来源产出行不是待检验状态");
    // ========== MES 质量管理-过程检验行（1-040-604-100） ==========
    ErrorCode QC_IPQC_LINE_NOT_EXISTS = new ErrorCode(1_040_604_100, "过程检验行不存在");

    // ========== MES 质量管理-质检缺陷记录（通用）（1-040-605-000） ==========
    ErrorCode QC_DEFECT_RECORD_NOT_EXISTS = new ErrorCode(1_040_605_000, "缺陷记录不存在");
    ErrorCode QC_DEFECT_RECORD_LEVEL_UNKNOWN = new ErrorCode(1_040_605_001, "未知的缺陷等级");
    ErrorCode QC_DEFECT_RECORD_QC_TYPE_UNSUPPORTED = new ErrorCode(1_040_605_002, "不支持的检验类型");

    // ========== MES 质量管理-检验结果（1-040-606-000） ==========
    ErrorCode QC_RESULT_NOT_EXISTS = new ErrorCode(1_040_606_000, "检验结果不存在");
    ErrorCode QC_RESULT_VALUE_FORMAT_INVALID = new ErrorCode(1_040_606_001, "检测值格式不正确：{}");
    ErrorCode QC_FINISH_INDICATOR_RESULT_REQUIRED = new ErrorCode(1_040_606_002, "完成检验单前，至少需要录入一条检测结果");

    // ========== MES 质量管理-出货检验（1-040-607-000） ==========
    ErrorCode QC_OQC_NOT_EXISTS = new ErrorCode(1_040_607_000, "出货检验单不存在");
    ErrorCode QC_OQC_CODE_DUPLICATE = new ErrorCode(1_040_607_001, "出货检验单编号已存在");
    ErrorCode QC_OQC_NOT_PREPARE = new ErrorCode(1_040_607_002, "只有草稿状态的检验单才可操作");
    ErrorCode QC_OQC_QUANTITY_MISMATCH = new ErrorCode(1_040_607_004, "合格品与不合格品数量之和须等于检测数量");
    ErrorCode QC_OQC_NO_TEMPLATE = new ErrorCode(1_040_607_005, "当前产品未配置 OQC 检测模板");
    ErrorCode QC_OQC_CHECK_RESULT_EMPTY = new ErrorCode(1_040_607_006, "完成检验单前，检测结果必须填写");
    ErrorCode QC_OQC_SOURCE_DOC_TYPE_UNKNOWN = new ErrorCode(1_040_607_007, "未知的 OQC 来源单据类型");

    // ========== MES 质量管理-出货检验行（1-040-607-100） ==========
    ErrorCode QC_OQC_LINE_NOT_EXISTS = new ErrorCode(1_040_607_100, "出货检验行不存在");

    // ========== MES 质量管理-退货检验 RQC（1-040-608-000） ==========
    ErrorCode QC_RQC_NOT_EXISTS = new ErrorCode(1_040_608_000, "退货检验单不存在");
    ErrorCode QC_RQC_CODE_DUPLICATE = new ErrorCode(1_040_608_001, "退货检验单编号已存在");
    ErrorCode QC_RQC_NOT_PREPARE = new ErrorCode(1_040_608_002, "只有草稿状态的检验单才可操作");
    ErrorCode QC_RQC_QUANTITY_MISMATCH = new ErrorCode(1_040_608_004, "合格品与不合格品数量之和须等于检测数量");
    ErrorCode QC_RQC_NO_TEMPLATE = new ErrorCode(1_040_608_005, "当前产品未配置 RQC 检测模板");
    ErrorCode QC_RQC_CHECK_RESULT_EMPTY = new ErrorCode(1_040_608_006, "完成检验单前，检测结果必须填写");
    // ========== MES 质量管理-退货检验行（1-040-608-100） ==========
    ErrorCode QC_RQC_LINE_NOT_EXISTS = new ErrorCode(1_040_608_100, "退货检验行不存在");

    // ========== MES 仓库管理-仓库（1-040-700-000） ==========
    ErrorCode WM_WAREHOUSE_NOT_EXISTS = new ErrorCode(1_040_700_000, "仓库不存在");
    ErrorCode WM_WAREHOUSE_CODE_DUPLICATE = new ErrorCode(1_040_700_001, "仓库编码已存在");
    ErrorCode WM_WAREHOUSE_NAME_DUPLICATE = new ErrorCode(1_040_700_002, "仓库名称已存在");
    ErrorCode WM_WAREHOUSE_HAS_LOCATION = new ErrorCode(1_040_700_003, "仓库下存在库区，无法删除");
    ErrorCode WM_WAREHOUSE_HAS_WORKSTATION = new ErrorCode(1_040_700_004, "仓库已被工作站引用，无法删除");
    ErrorCode WM_WAREHOUSE_HAS_MATERIAL_STOCK = new ErrorCode(1_040_700_005, "仓库下有库存记录，无法删除");
    ErrorCode WM_WAREHOUSE_IS_VIRTUAL = new ErrorCode(1_040_700_006, "虚拟仓库不允许操作");

    // ========== MES 仓库管理-库区（1-040-701-000） ==========
    ErrorCode WM_WAREHOUSE_LOCATION_NOT_EXISTS = new ErrorCode(1_040_701_000, "库区不存在");
    ErrorCode WM_WAREHOUSE_LOCATION_CODE_DUPLICATE = new ErrorCode(1_040_701_001, "同一仓库下库区编码已存在");
    ErrorCode WM_WAREHOUSE_LOCATION_NAME_DUPLICATE = new ErrorCode(1_040_701_002, "同一仓库下库区名称已存在");
    ErrorCode WM_WAREHOUSE_LOCATION_HAS_AREA = new ErrorCode(1_040_701_003, "库区下存在库位，无法删除");
    ErrorCode WM_WAREHOUSE_LOCATION_HAS_WORKSTATION = new ErrorCode(1_040_701_004, "库区已被工作站引用，无法删除");
    ErrorCode WM_WAREHOUSE_REQUIRED = new ErrorCode(1_040_701_005, "选择库区时，仓库不能为空");
    ErrorCode WM_WAREHOUSE_LOCATION_RELATION_INVALID = new ErrorCode(1_040_701_006, "库区不属于所选仓库");
    ErrorCode WM_WAREHOUSE_LOCATION_HAS_MATERIAL_STOCK = new ErrorCode(1_040_701_007, "库区下有库存记录，无法删除");
    ErrorCode WM_WAREHOUSE_LOCATION_IS_VIRTUAL = new ErrorCode(1_040_701_008, "虚拟库区不允许操作");

    // ========== MES 仓库管理-库位（1-040-702-000） ==========
    ErrorCode WM_WAREHOUSE_AREA_NOT_EXISTS = new ErrorCode(1_040_702_000, "库位不存在");
    ErrorCode WM_WAREHOUSE_AREA_CODE_DUPLICATE = new ErrorCode(1_040_702_001, "同一库区下库位编码已存在");
    ErrorCode WM_WAREHOUSE_AREA_NAME_DUPLICATE = new ErrorCode(1_040_702_002, "同一库区下库位名称已存在");
    ErrorCode WM_WAREHOUSE_AREA_HAS_WORKSTATION = new ErrorCode(1_040_702_003, "库位已被工作站引用，无法删除");
    ErrorCode WM_WAREHOUSE_LOCATION_REQUIRED = new ErrorCode(1_040_702_004, "选择库位时，库区不能为空");
    ErrorCode WM_WAREHOUSE_AREA_RELATION_INVALID = new ErrorCode(1_040_702_005, "库位不属于所选库区");
    ErrorCode WM_WAREHOUSE_AREA_HAS_MATERIAL_STOCK = new ErrorCode(1_040_702_006, "库位下有库存记录，无法删除");
    ErrorCode WM_WAREHOUSE_AREA_WAREHOUSE_MISMATCH = new ErrorCode(1_040_702_007, "库位不属于所选仓库");
    ErrorCode WM_WAREHOUSE_AREA_IS_VIRTUAL = new ErrorCode(1_040_702_008, "虚拟库位不允许操作");

    // ========== MES 仓库管理-库存（1-040-703-000） ==========
    ErrorCode WM_MATERIAL_STOCK_NOT_EXISTS = new ErrorCode(1_040_703_000, "库存记录不存在");
    ErrorCode WM_MATERIAL_STOCK_INSUFFICIENT = new ErrorCode(1_040_703_001, "库存数量不足");
    ErrorCode WM_TRANSACTION_TYPE_NOT_EXISTS = new ErrorCode(1_040_703_002, "库存事务类型不存在");
    ErrorCode WM_TRANSACTION_WAREHOUSE_FROZEN = new ErrorCode(1_040_703_003, "仓库({})已被冻结");
    ErrorCode WM_TRANSACTION_LOCATION_FROZEN = new ErrorCode(1_040_703_004, "库区({})已被冻结");
    ErrorCode WM_TRANSACTION_AREA_FROZEN = new ErrorCode(1_040_703_005, "库位({})已被冻结");
    ErrorCode WM_TRANSACTION_STOCK_FROZEN = new ErrorCode(1_040_703_006, "存放于({}/{}/{})下的物料已被冻结");
    ErrorCode WM_TRANSACTION_BATCH_REQUIRED = new ErrorCode(1_040_703_007, "当前物料启用了批次管理，批次号不能为空");
    ErrorCode WM_MATERIAL_STOCK_AREA_ITEM_MIXING_NOT_ALLOWED = new ErrorCode(1_040_703_008, "库位({})不允许物料混放，请选择其他库位");
    ErrorCode WM_MATERIAL_STOCK_AREA_BATCH_MIXING_NOT_ALLOWED = new ErrorCode(1_040_703_009, "库位({})不允许批次混放，请选择其他库位");
    ErrorCode WM_TRANSACTION_RELATED_NOT_EXISTS = new ErrorCode(1_040_703_010, "关联的库存事务不存在");
    ErrorCode WM_TRANSACTION_LIST_EMPTY = new ErrorCode(1_040_703_011, "库存事务列表不能为空");
    ErrorCode WM_TRANSACTION_BATCH_NOT_EXISTS = new ErrorCode(1_040_703_012, "批次记录不存在");
    ErrorCode WM_MATERIAL_STOCK_REQUIRED = new ErrorCode(1_040_703_013, "库存记录不能为空");
    ErrorCode WM_MATERIAL_STOCK_SELECTION_MISMATCH = new ErrorCode(1_040_703_014, "库存记录与提交的物料、批次或库位信息不一致");
    ErrorCode WM_MATERIAL_STOCK_SIGN_REQUIRED = new ErrorCode(1_040_703_015, "库存冻结/解冻会改动环保数据，必须手写签名确认（未收到签名图），已拒绝");

    // ========== MES 仓库管理-到货通知单（1-040-704-000） ==========
    ErrorCode WM_ARRIVAL_NOTICE_NOT_EXISTS = new ErrorCode(1_040_704_000, "到货通知单不存在");
    ErrorCode WM_ARRIVAL_NOTICE_CODE_DUPLICATE = new ErrorCode(1_040_704_001, "到货通知单编码已存在");
    ErrorCode WM_ARRIVAL_NOTICE_STATUS_NOT_PREPARE = new ErrorCode(1_040_704_002, "只有草稿状态才允许此操作");
    ErrorCode WM_ARRIVAL_NOTICE_STATUS_NOT_PENDING_QC = new ErrorCode(1_040_704_003, "只有待质检状态才允许审批");
    ErrorCode WM_ARRIVAL_NOTICE_STATUS_NOT_PENDING_RECEIPT = new ErrorCode(1_040_704_004, "只有待入库状态才允许完成");
    ErrorCode WM_ARRIVAL_NOTICE_IQC_PENDING = new ErrorCode(1_040_704_005, "存在待检验行，无法审批通过");
    ErrorCode WM_ARRIVAL_NOTICE_NO_LINE = new ErrorCode(1_040_704_006, "至少需要一条行项目");
    ErrorCode WM_ARRIVAL_NOTICE_VENDOR_MISMATCH = new ErrorCode(1_040_704_007, "到货通知单的供应商与当前单据不一致");
    ErrorCode WM_ARRIVAL_NOTICE_LINE_NOT_EXISTS = new ErrorCode(1_040_704_100, "到货通知单行不存在");
    ErrorCode WM_ARRIVAL_NOTICE_LINE_NOT_MATCH = new ErrorCode(1_040_704_101, "到货通知单行不属于指定的到货通知单");

    // ========== MES 仓库管理-采购入库单（1-040-705-000） ==========
    ErrorCode WM_ITEM_RECEIPT_NOT_EXISTS = new ErrorCode(1_040_705_000, "采购入库单不存在");
    ErrorCode WM_ITEM_RECEIPT_CODE_DUPLICATE = new ErrorCode(1_040_705_001, "采购入库单编码已存在");
    ErrorCode WM_ITEM_RECEIPT_STATUS_NOT_PREPARE = new ErrorCode(1_040_705_002, "只有草稿或待上架状态才允许此操作");
    ErrorCode WM_ITEM_RECEIPT_NO_LINE = new ErrorCode(1_040_705_003, "至少需要一条行项目");
    ErrorCode WM_ITEM_RECEIPT_DETAIL_QUANTITY_MISMATCH = new ErrorCode(1_040_705_004, "明细上架总数与行入库数量不匹配");
    ErrorCode WM_ITEM_RECEIPT_STATUS_ERROR = new ErrorCode(1_040_705_005, "入库单状态不正确");
    ErrorCode WM_ITEM_RECEIPT_CANCEL_NOT_ALLOWED = new ErrorCode(1_040_705_006, "已完成或已取消的入库单不允许取消");
    ErrorCode WM_ITEM_RECEIPT_LINE_NOT_EXISTS = new ErrorCode(1_040_705_100, "采购入库单行不存在");
    ErrorCode WM_ITEM_RECEIPT_LINE_ARRIVAL_NOTICE_LINE_REQUIRED = new ErrorCode(1_040_705_101,
            "入库单关联了到货通知单，必须选择到货通知单行");
    ErrorCode WM_ITEM_RECEIPT_LINE_ARRIVAL_NOTICE_LINE_NOT_ALLOWED = new ErrorCode(1_040_705_102,
            "入库单未关联到货通知单，不能选择到货通知单行");
    ErrorCode WM_ITEM_RECEIPT_DETAIL_NOT_EXISTS = new ErrorCode(1_040_705_200, "采购入库明细不存在");
    ErrorCode WM_ITEM_RECEIPT_DETAIL_QUANTITY_EXCEED = new ErrorCode(1_040_705_202, "上架明细总数量不能超过行入库数量");

    // ========== MES 仓库管理-领料申请单（1-040-706-000） ==========
    ErrorCode WM_MATERIAL_REQUEST_NOT_EXISTS = new ErrorCode(1_040_706_000, "领料申请单不存在");
    ErrorCode WM_MATERIAL_REQUEST_STATUS_INVALID = new ErrorCode(1_040_706_001, "领料申请单状态不正确，无法执行该操作");
    ErrorCode WM_MATERIAL_REQUEST_LINE_NOT_EXISTS = new ErrorCode(1_040_706_100, "领料申请单行不存在");

    // ========== MES 仓库管理-外协发料单（1-040-707-000） ==========
    ErrorCode WM_OUTSOURCE_ISSUE_NOT_EXISTS = new ErrorCode(1_040_707_000, "外协发料单不存在");
    ErrorCode WM_OUTSOURCE_ISSUE_CODE_DUPLICATE = new ErrorCode(1_040_707_001, "外协发料单编码已存在");
    ErrorCode WM_OUTSOURCE_ISSUE_STATUS_NOT_PREPARE = new ErrorCode(1_040_707_002, "只有草稿状态才允许此操作");
    ErrorCode WM_OUTSOURCE_ISSUE_NO_LINE = new ErrorCode(1_040_707_003, "至少需要一条发料行");
    ErrorCode WM_OUTSOURCE_ISSUE_QUANTITY_MISMATCH = new ErrorCode(1_040_707_004, "发料单行数量与明细数量不一致");
    ErrorCode WM_OUTSOURCE_ISSUE_STATUS_NOT_APPROVING = new ErrorCode(1_040_707_005, "只有待拣货状态才允许此操作");
    ErrorCode WM_OUTSOURCE_ISSUE_STATUS_NOT_APPROVED = new ErrorCode(1_040_707_006, "只有待执行出库状态才允许此操作");
    ErrorCode WM_OUTSOURCE_ISSUE_CANCEL_NOT_ALLOWED = new ErrorCode(1_040_707_007, "已完成或已取消的发料单不允许取消");
    ErrorCode WM_OUTSOURCE_ISSUE_LINE_NOT_EXISTS = new ErrorCode(1_040_707_100, "外协发料单行不存在");
    ErrorCode WM_OUTSOURCE_ISSUE_LINE_ITEM_NOT_IN_BOM = new ErrorCode(1_040_707_101, "发料单行对应的物料不在当前工单的 BOM 列表中");
    ErrorCode WM_OUTSOURCE_ISSUE_DETAIL_NOT_EXISTS = new ErrorCode(1_040_707_200, "外协发料单明细不存在");
    ErrorCode WM_OUTSOURCE_ISSUE_DETAIL_LINE_NOT_MATCH = new ErrorCode(1_040_707_201, "拣货明细不属于指定的外协发料单");
    ErrorCode WM_OUTSOURCE_ISSUE_DETAIL_ITEM_MISMATCH = new ErrorCode(1_040_707_202, "拣货明细的物料与外协发料单行的物料不一致");
    ErrorCode WM_OUTSOURCE_ISSUE_WORK_ORDER_TYPE_INVALID = new ErrorCode(1_040_707_008, "工单类型不是外协（代工）类型");

    // ========== MES 仓库管理-生产领料出库单（1-040-708-000） ==========
    ErrorCode WM_PRODUCT_ISSUE_NOT_EXISTS = new ErrorCode(1_040_708_000, "生产领料出库单不存在");
    ErrorCode WM_PRODUCT_ISSUE_STATUS_INVALID = new ErrorCode(1_040_708_001, "生产领料出库单状态不正确，无法执行该操作");
    ErrorCode WM_PRODUCT_ISSUE_NO_LINE = new ErrorCode(1_040_708_002, "生产领料出库单至少需要一条行数据");
    ErrorCode WM_PRODUCT_ISSUE_DETAIL_QUANTITY_MISMATCH = new ErrorCode(1_040_708_003, "领料出库单行数量与明细数量不一致");
    ErrorCode WM_PRODUCT_ISSUE_WORKORDER_NOT_EXISTS = new ErrorCode(1_040_708_004, "生产工单不存在");
    ErrorCode WM_PRODUCT_ISSUE_WORKSTATION_NOT_EXISTS = new ErrorCode(1_040_708_005, "工作站不存在");
    ErrorCode WM_PRODUCT_ISSUE_CANCEL_NOT_ALLOWED = new ErrorCode(1_040_708_006, "生产领料出库单已完成或已取消，无法取消");
    ErrorCode WM_PRODUCT_ISSUE_LINE_NOT_EXISTS = new ErrorCode(1_040_708_100, "生产领料出库单行不存在");
    ErrorCode WM_PRODUCT_ISSUE_LINE_ITEM_NOT_IN_BOM = new ErrorCode(1_040_708_101, "当前物料不在生产工单的 BOM 物料清单中");
    ErrorCode WM_PRODUCT_ISSUE_CODE_DUPLICATE = new ErrorCode(1_040_708_102, "领料出库单编码已存在");
    ErrorCode WM_PRODUCT_ISSUE_DETAIL_NOT_EXISTS = new ErrorCode(1_040_708_200, "生产领料出库单明细不存在");
    ErrorCode WM_PRODUCT_ISSUE_DETAIL_ITEM_MISMATCH = new ErrorCode(1_040_708_201, "拣货明细的物料与领料单行的物料不一致");
    ErrorCode WM_PRODUCT_ISSUE_DETAIL_LINE_NOT_MATCH = new ErrorCode(1_040_708_202, "拣货明细不属于指定的领料出库单");
    ErrorCode WM_PRODUCT_ISSUE_NO_DETAIL = new ErrorCode(1_040_708_203, "领料出库单没有拣货明细，无法执行领出");

    // ========== MES 仓库管理-生产入库单（1-040-709-000） ==========
    ErrorCode WM_PRODUCT_PRODUCE_NOT_EXISTS = new ErrorCode(1_040_709_000, "生产入库单不存在");
    ErrorCode WM_PRODUCT_PRODUCE_STATUS_INVALID = new ErrorCode(1_040_709_001, "生产入库单状态不正确，无法执行该操作");
    ErrorCode WM_PRODUCT_PRODUCE_NO_LINE = new ErrorCode(1_040_709_002, "生产入库单至少需要一条行数据");
    ErrorCode WM_PRODUCT_PRODUCE_DETAIL_QUANTITY_MISMATCH = new ErrorCode(1_040_709_003, "生产入库单行数量与明细数量不一致");
    ErrorCode WM_PRODUCT_PRODUCE_CANCEL_NOT_ALLOWED = new ErrorCode(1_040_709_004, "生产入库单已完成或已取消，无法取消");
    ErrorCode WM_PRODUCT_PRODUCE_LINE_NOT_EXISTS = new ErrorCode(1_040_709_100, "生产入库单行不存在");
    ErrorCode WM_PRODUCT_PRODUCE_DETAIL_NOT_EXISTS = new ErrorCode(1_040_709_200, "生产入库单明细不存在");

    // ========== MES 仓库管理-转移调拨（1-040-710-000） ==========
    ErrorCode WM_TRANSFER_NOT_EXISTS = new ErrorCode(1_040_710_000, "转移单不存在");
    ErrorCode WM_TRANSFER_NOT_EDITABLE = new ErrorCode(1_040_710_001, "当前转移单状态不允许编辑");
    ErrorCode WM_TRANSFER_CODE_DUPLICATE = new ErrorCode(1_040_710_002, "转移单编号已存在");
    ErrorCode WM_TRANSFER_NOT_DRAFT = new ErrorCode(1_040_710_003, "只有草稿状态的转移单才可操作");
    ErrorCode WM_TRANSFER_NOT_CONFIRMED = new ErrorCode(1_040_710_004, "只有待确认状态的转移单才可执行确认");
    ErrorCode WM_TRANSFER_NOT_APPROVING = new ErrorCode(1_040_710_005, "只有待上架状态的转移单才可执行上架");
    ErrorCode WM_TRANSFER_NOT_APPROVED = new ErrorCode(1_040_710_006, "只有待执行状态的转移单才可完成");
    ErrorCode WM_TRANSFER_ALREADY_FINISHED = new ErrorCode(1_040_710_007, "转移单已完成或已取消，无法继续操作");
    ErrorCode WM_TRANSFER_NO_LINE = new ErrorCode(1_040_710_008, "转移单至少需要一条行数据");
    ErrorCode WM_TRANSFER_DETAIL_QUANTITY_MISMATCH = new ErrorCode(1_040_710_009, "转移单行数量与明细数量不一致");
    ErrorCode WM_TRANSFER_LINE_NOT_EXISTS = new ErrorCode(1_040_710_100, "转移单行不存在");
    ErrorCode WM_TRANSFER_LINE_QUANTITY_EXCEED_STOCK = new ErrorCode(1_040_710_101, "转移数量不能超过库存数量");
    ErrorCode WM_TRANSFER_DETAIL_NOT_EXISTS = new ErrorCode(1_040_710_200, "调拨明细不存在");
    ErrorCode WM_TRANSFER_DETAIL_QUANTITY_EXCEED = new ErrorCode(1_040_710_201, "调拨明细总数量不能超过调拨单行数量");
    ErrorCode WM_TRANSFER_DETAIL_MIXED_GOODS = new ErrorCode(1_040_710_202, "同一目标仓位下已存在其他物料的明细，不允许混货");

    // ========== MES 仓库管理-生产退料单（1-040-711-000） ==========
    ErrorCode WM_RETURN_ISSUE_NOT_EXISTS = new ErrorCode(1_040_710_000, "生产退料单不存在");
    ErrorCode WM_RETURN_ISSUE_STATUS_INVALID = new ErrorCode(1_040_710_001, "生产退料单状态不正确，无法执行该操作");
    ErrorCode WM_RETURN_ISSUE_NOT_PREPARE = new ErrorCode(1_040_710_002, "只有草稿状态的退料单才可操作");
    ErrorCode WM_RETURN_ISSUE_NOT_CONFIRMED = new ErrorCode(1_040_710_003, "只有待检验状态的退料单才可提交");
    ErrorCode WM_RETURN_ISSUE_NOT_APPROVING = new ErrorCode(1_040_710_004, "只有待上架状态的退料单才可入库上架");
    ErrorCode WM_RETURN_ISSUE_NOT_APPROVED = new ErrorCode(1_040_710_005, "只有待执行退料状态的退料单才可完成");
    ErrorCode WM_RETURN_ISSUE_NO_LINE = new ErrorCode(1_040_710_006, "生产退料单至少需要一条行数据");
    ErrorCode WM_RETURN_ISSUE_DETAIL_QUANTITY_MISMATCH = new ErrorCode(1_040_710_007, "退料单行数量与明细数量不一致");
    ErrorCode WM_RETURN_ISSUE_CANCEL_NOT_ALLOWED = new ErrorCode(1_040_710_008, "生产退料单已完成或已取消，无法取消");
    ErrorCode WM_RETURN_ISSUE_LINE_NOT_EXISTS = new ErrorCode(1_040_710_100, "生产退料单行不存在");
    ErrorCode WM_RETURN_ISSUE_DETAIL_NOT_EXISTS = new ErrorCode(1_040_710_200, "生产退料单明细不存在");
    ErrorCode WM_RETURN_ISSUE_DETAIL_QUANTITY_INVALID = new ErrorCode(1_040_710_201, "退料明细数量必须大于0");
    ErrorCode WM_RETURN_ISSUE_DETAIL_QUANTITY_EXCEED = new ErrorCode(1_040_710_202, "退料明细总数量不能超过退料单行数量");
    ErrorCode WM_RETURN_ISSUE_CODE_DUPLICATE = new ErrorCode(1_040_710_203, "退料单编码已存在");
    ErrorCode WM_RETURN_ISSUE_DETAIL_LINE_NOT_MATCH = new ErrorCode(1_040_710_204, "退料明细不属于指定的退料单");
    ErrorCode WM_RETURN_ISSUE_DETAIL_ITEM_MISMATCH = new ErrorCode(1_040_710_205, "退料明细的物料与退料单行的物料不一致");

    // ========== MES 仓库管理-供应商退货单（1-040-711-000） ==========
    ErrorCode WM_RETURN_VENDOR_NOT_EXISTS = new ErrorCode(1_040_711_000, "供应商退货单不存在");
    ErrorCode WM_RETURN_VENDOR_STATUS_INVALID = new ErrorCode(1_040_711_001, "供应商退货单状态不正确，无法执行该操作");
    ErrorCode WM_RETURN_VENDOR_NO_LINE = new ErrorCode(1_040_711_002, "供应商退货单至少需要一条行数据");
    ErrorCode WM_RETURN_VENDOR_DETAIL_QUANTITY_MISMATCH = new ErrorCode(1_040_711_003, "供应商退货单行数量与明细数量不一致");
    ErrorCode WM_RETURN_VENDOR_CANCEL_NOT_ALLOWED = new ErrorCode(1_040_711_004, "供应商退货单已完成或已取消，无法取消");
    ErrorCode WM_RETURN_VENDOR_CODE_DUPLICATE = new ErrorCode(1_040_711_005, "供应商退货单编号已存在");
    ErrorCode WM_RETURN_VENDOR_NO_DETAIL = new ErrorCode(1_040_711_006, "供应商退货单没有拣货明细，无法执行退货");
    ErrorCode WM_RETURN_VENDOR_DETAIL_ITEM_MISMATCH = new ErrorCode(1_040_711_007, "拣货明细的物料与退货单行的物料不一致");
    ErrorCode WM_RETURN_VENDOR_LINE_NOT_EXISTS = new ErrorCode(1_040_711_100, "供应商退货单行不存在");
    ErrorCode WM_RETURN_VENDOR_DETAIL_NOT_EXISTS = new ErrorCode(1_040_711_200, "供应商退货单明细不存在");
    ErrorCode WM_RETURN_VENDOR_DETAIL_QUANTITY_INVALID = new ErrorCode(1_040_711_201, "退货明细数量必须大于 0");
    ErrorCode WM_RETURN_VENDOR_DETAIL_LINE_NOT_MATCH = new ErrorCode(1_040_711_202, "拣货明细不属于指定的供应商退货单");

    // ========== MES 仓库管理-产品收货单（1-040-712-000） ==========
    ErrorCode WM_PRODUCT_RECPT_NOT_EXISTS = new ErrorCode(1_040_712_000, "产品收货单不存在");
    ErrorCode WM_PRODUCT_RECPT_CODE_DUPLICATE = new ErrorCode(1_040_712_001, "产品收货单编码已存在");
    ErrorCode WM_PRODUCT_RECPT_STATUS_NOT_PREPARE = new ErrorCode(1_040_712_002, "只有草稿或待上架状态才允许此操作");
    ErrorCode WM_PRODUCT_RECPT_NO_LINE = new ErrorCode(1_040_712_003, "至少需要一条行项目");
    ErrorCode WM_PRODUCT_RECPT_DETAIL_QUANTITY_MISMATCH = new ErrorCode(1_040_712_004, "明细上架总数与行收货数量不匹配");
    ErrorCode WM_PRODUCT_RECPT_STATUS_ERROR = new ErrorCode(1_040_712_005, "收货单状态不正确");
    ErrorCode WM_PRODUCT_RECPT_CANCEL_NOT_ALLOWED = new ErrorCode(1_040_712_006, "已完成或已取消的收货单不允许取消");
    ErrorCode WM_PRODUCT_RECPT_NO_DETAIL = new ErrorCode(1_040_712_007, "收货单没有上架明细，无法执行入库");
    ErrorCode WM_PRODUCT_RECPT_LINE_NOT_EXISTS = new ErrorCode(1_040_712_100, "产品收货单行不存在");
    ErrorCode WM_PRODUCT_RECPT_DETAIL_NOT_EXISTS = new ErrorCode(1_040_712_200, "产品收货明细不存在");

    // ========== MES 仓库管理-外协入库单（1-040-713-000） ==========
    ErrorCode WM_OUTSOURCE_RECEIPT_NOT_EXISTS = new ErrorCode(1_040_713_000, "外协入库单不存在");
    ErrorCode WM_OUTSOURCE_RECEIPT_CODE_DUPLICATE = new ErrorCode(1_040_713_001, "外协入库单编码已存在");
    ErrorCode WM_OUTSOURCE_RECEIPT_STATUS_NOT_PREPARE = new ErrorCode(1_040_713_002, "只有草稿状态才允许此操作");
    ErrorCode WM_OUTSOURCE_RECEIPT_NO_LINE = new ErrorCode(1_040_713_003, "至少需要一条行项目");
    ErrorCode WM_OUTSOURCE_RECEIPT_DETAIL_QUANTITY_MISMATCH = new ErrorCode(1_040_713_004, "明细上架总数与行入库数量不匹配");
    ErrorCode WM_OUTSOURCE_RECEIPT_STATUS_ERROR = new ErrorCode(1_040_713_005, "入库单状态不正确");
    ErrorCode WM_OUTSOURCE_RECEIPT_CANCEL_NOT_ALLOWED = new ErrorCode(1_040_713_006, "已完成或已取消的入库单不允许取消");
    ErrorCode WM_OUTSOURCE_RECEIPT_LINE_NOT_EXISTS = new ErrorCode(1_040_713_100, "外协入库单行不存在");
    ErrorCode WM_OUTSOURCE_RECEIPT_DETAIL_NOT_EXISTS = new ErrorCode(1_040_713_200, "外协入库明细不存在");

    // ========== MES 仓库管理-销售退货单（1-040-713-000） ==========
    ErrorCode WM_RETURN_SALES_NOT_EXISTS = new ErrorCode(1_040_713_000, "销售退货单不存在");
    ErrorCode WM_RETURN_SALES_CODE_DUPLICATE = new ErrorCode(1_040_713_001, "销售退货单编码已存在");
    ErrorCode WM_RETURN_SALES_STATUS_NOT_PREPARE = new ErrorCode(1_040_713_002, "只有草稿状态才允许此操作");
    ErrorCode WM_RETURN_SALES_STATUS_NOT_APPROVING = new ErrorCode(1_040_713_003, "只有待执行状态才允许执行退货");
    ErrorCode WM_RETURN_SALES_STATUS_NOT_APPROVED = new ErrorCode(1_040_713_004, "只有待上架状态才允许执行上架");
    ErrorCode WM_RETURN_SALES_NO_LINE = new ErrorCode(1_040_713_005, "销售退货单至少需要一条行数据");
    ErrorCode WM_RETURN_SALES_DETAIL_QUANTITY_MISMATCH = new ErrorCode(1_040_713_006, "销售退货单行数量与明细数量不一致");
    ErrorCode WM_RETURN_SALES_CANCEL_NOT_ALLOWED = new ErrorCode(1_040_713_007, "销售退货单已完成或已取消，无法取消");
    ErrorCode WM_RETURN_SALES_LINE_NOT_EXISTS = new ErrorCode(1_040_713_100, "销售退货单行不存在");
    ErrorCode WM_RETURN_SALES_DETAIL_NOT_EXISTS = new ErrorCode(1_040_713_200, "销售退货单明细不存在");
    ErrorCode WM_RETURN_SALES_DETAIL_QUANTITY_EXCEED = new ErrorCode(1_040_713_201, "上架明细总数量不能超过退货行数量");

    // ========== MES 仓库管理-盘点方案/任务/结果（1-040-714-100） ==========
    ErrorCode WM_STOCK_TAKING_PLAN_NOT_EXISTS = new ErrorCode(1_040_714_100, "盘点方案不存在");
    ErrorCode WM_STOCK_TAKING_PLAN_CODE_DUPLICATE = new ErrorCode(1_040_714_101, "盘点方案编码已存在");
    ErrorCode WM_STOCK_TAKING_PLAN_NOT_DISABLED = new ErrorCode(1_040_714_102, "只有禁用状态的盘点方案才允许修改、删除或维护参数");
    ErrorCode WM_STOCK_TAKING_PLAN_NOT_ENABLED = new ErrorCode(1_040_714_103, "只有启用状态的盘点方案才允许生成任务");
    ErrorCode WM_STOCK_TAKING_PLAN_PARAM_NOT_EXISTS = new ErrorCode(1_040_714_104, "盘点方案参数不存在");
    ErrorCode WM_STOCK_TAKING_PLAN_PARAM_EMPTY = new ErrorCode(1_040_714_105, "盘点方案参数不能为空，请先配置盘点参数");
    ErrorCode WM_STOCK_TAKING_PLAN_DYNAMIC_TIME_INVALID = new ErrorCode(1_040_714_106, "动态盘点方案必须设置开始时间和结束时间，且结束时间必须晚于开始时间");
    ErrorCode WM_STOCK_TAKING_TASK_NOT_EXISTS = new ErrorCode(1_040_714_110, "盘点任务不存在");
    ErrorCode WM_STOCK_TAKING_TASK_CODE_DUPLICATE = new ErrorCode(1_040_714_111, "盘点任务编码已存在");
    ErrorCode WM_STOCK_TAKING_TASK_NOT_PREPARE = new ErrorCode(1_040_714_112, "只有草稿状态的盘点任务才允许此操作");
    ErrorCode WM_STOCK_TAKING_TASK_NOT_APPROVING = new ErrorCode(1_040_714_113, "只有盘点中状态的任务才允许此操作");
    ErrorCode WM_STOCK_TAKING_TASK_CANNOT_CANCEL = new ErrorCode(1_040_714_114, "已完成或已取消的盘点任务不允许取消");
    ErrorCode WM_STOCK_TAKING_TASK_NO_STOCK = new ErrorCode(1_040_714_115, "未找到符合条件的库存数据");
    ErrorCode WM_STOCK_TAKING_TASK_NO_LINE = new ErrorCode(1_040_714_116, "盘点任务至少需要一条任务行");
    ErrorCode WM_STOCK_TAKING_TASK_LINE_NOT_EXISTS = new ErrorCode(1_040_714_117, "盘点任务行不存在");
    ErrorCode WM_STOCK_TAKING_TASK_DYNAMIC_TIME_REQUIRED = new ErrorCode(1_040_714_118, "动态盘点必须选择开始时间和结束时间");
    ErrorCode WM_STOCK_TAKING_TASK_RESULT_NOT_EXISTS = new ErrorCode(1_040_714_119, "盘点结果不存在");
    ErrorCode WM_STOCK_TAKING_TASK_LINE_ALREADY_TAKEN = new ErrorCode(1_040_714_120, "该盘点清单行已有盘点记录，不能重复盘点");

    // ========== MES 仓库管理-销售出库单（1-040-714-000） ==========
    ErrorCode WM_PRODUCT_SALES_NOT_EXISTS = new ErrorCode(1_040_714_000, "销售出库单不存在");
    ErrorCode WM_PRODUCT_SALES_CODE_DUPLICATE = new ErrorCode(1_040_714_001, "销售出库单号已存在");
    ErrorCode WM_PRODUCT_SALES_NOT_PREPARE = new ErrorCode(1_040_714_002, "只有草稿状态才可操作");
    ErrorCode WM_PRODUCT_SALES_LINES_EMPTY = new ErrorCode(1_040_714_003, "销售出库单行不能为空");
    ErrorCode WM_PRODUCT_SALES_CANNOT_SUBMIT = new ErrorCode(1_040_714_004, "当前状态不允许提交");
    ErrorCode WM_PRODUCT_SALES_CANNOT_PICK = new ErrorCode(1_040_714_005, "当前状态不允许拣货");
    ErrorCode WM_PRODUCT_SALES_CANNOT_SHIPPING = new ErrorCode(1_040_714_006, "当前状态不允许填写运单");
    ErrorCode WM_PRODUCT_SALES_CANNOT_FINISH = new ErrorCode(1_040_714_007, "当前状态不允许执行出库");
    ErrorCode WM_PRODUCT_SALES_CANNOT_CANCEL = new ErrorCode(1_040_714_008, "当前状态不允许取消");
    ErrorCode WM_PRODUCT_SALES_CANNOT_CONFIRM = new ErrorCode(1_040_714_009, "当前状态不允许确认检验通过");
    ErrorCode WM_PRODUCT_SALES_DETAILS_EMPTY = new ErrorCode(1_040_714_020, "拣货明细不能为空");
    ErrorCode WM_PRODUCT_SALES_DETAIL_QUANTITY_MISMATCH = new ErrorCode(1_040_714_010, "拣货数量与出库数量不匹配");
    ErrorCode WM_PRODUCT_SALES_LINE_NOT_EXISTS = new ErrorCode(1_040_714_011, "销售出库单行不存在");
    ErrorCode WM_PRODUCT_SALES_DETAIL_NOT_EXISTS = new ErrorCode(1_040_714_012, "销售出库明细不存在");
    ErrorCode WM_PRODUCT_SALES_STOCK_INSUFFICIENT = new ErrorCode(1_040_714_013, "库存不足，无法拣货");
    ErrorCode WM_PRODUCT_SALES_LINE_QUANTITY_INVALID = new ErrorCode(1_040_714_014, "出库数量必须大于 0");
    ErrorCode WM_PRODUCT_SALES_DETAIL_LINE_NOT_MATCH = new ErrorCode(1_040_714_015, "拣货明细不属于指定的销售出库单");
    ErrorCode WM_PRODUCT_SALES_DETAIL_ITEM_MISMATCH = new ErrorCode(1_040_714_016, "拣货明细的物料与销售出库单行的物料不一致");
    ErrorCode WM_PRODUCT_SALES_LINE_SALES_NOTICE_LINE_REQUIRED = new ErrorCode(1_040_714_017,
            "出库单关联了发货通知单，必须选择发货通知单行");
    ErrorCode WM_PRODUCT_SALES_LINE_SALES_NOTICE_LINE_NOT_ALLOWED = new ErrorCode(1_040_714_018,
            "出库单未关联发货通知单，不能选择发货通知单行");
    ErrorCode WM_PRODUCT_SALES_LINE_NOTICE_LINE_ITEM_MISMATCH = new ErrorCode(1_040_714_030,
            "出库行物料与发货通知单行物料不一致");
    ErrorCode WM_PRODUCT_SALES_LINE_NOTICE_LINE_QUANTITY_MISMATCH = new ErrorCode(1_040_714_031,
            "出库行数量与发货通知单行数量不一致");
    ErrorCode WM_PRODUCT_SALES_LINE_NOTICE_LINE_BATCH_MISMATCH = new ErrorCode(1_040_714_032,
            "出库行批次号与发货通知单行批次号不一致");
    ErrorCode WM_PRODUCT_SALES_LINE_NOTICE_LINE_OQC_MISMATCH = new ErrorCode(1_040_714_033,
            "出库行 OQC 检验标识与发货通知单行不一致");

    // ========== MES 仓库管理-杂项出库单（1-040-715-000） ==========
    ErrorCode WM_MISC_ISSUE_NOT_EXISTS = new ErrorCode(1_040_715_000, "杂项出库单不存在");
    ErrorCode WM_MISC_ISSUE_CODE_DUPLICATE = new ErrorCode(1_040_715_001, "杂项出库单编码已存在");
    ErrorCode WM_MISC_ISSUE_STATUS_INVALID = new ErrorCode(1_040_715_002, "杂项出库单状态不正确，无法执行该操作");
    ErrorCode WM_MISC_ISSUE_NO_LINE = new ErrorCode(1_040_715_003, "杂项出库单至少需要一条行数据");
    ErrorCode WM_MISC_ISSUE_CANCEL_NOT_ALLOWED = new ErrorCode(1_040_715_004, "杂项出库单已完成或已取消，无法取消");
    ErrorCode WM_MISC_ISSUE_DETAIL_QUANTITY_MISMATCH = new ErrorCode(1_040_715_005, "杂项出库单行数量与明细数量不一致");
    ErrorCode WM_MISC_ISSUE_LINE_NOT_EXISTS = new ErrorCode(1_040_715_100, "杂项出库单行不存在");
    ErrorCode WM_MISC_ISSUE_DETAIL_NOT_EXISTS = new ErrorCode(1_040_715_200, "杂项出库单明细不存在");

    // ========== MES 仓库管理-杂项入库单（1-040-716-000） ==========
    ErrorCode WM_MISC_RECEIPT_NOT_EXISTS = new ErrorCode(1_040_716_000, "杂项入库单不存在");
    ErrorCode WM_MISC_RECEIPT_CODE_DUPLICATE = new ErrorCode(1_040_716_001, "杂项入库单编码已存在");
    ErrorCode WM_MISC_RECEIPT_STATUS_NOT_PREPARE = new ErrorCode(1_040_716_002, "只有草稿状态才允许此操作");
    ErrorCode WM_MISC_RECEIPT_STATUS_NOT_APPROVED = new ErrorCode(1_040_716_003, "只有已审批状态才允许执行入库");
    ErrorCode WM_MISC_RECEIPT_NO_LINE = new ErrorCode(1_040_716_004, "至少需要一条行项目");
    ErrorCode WM_MISC_RECEIPT_CANCEL_NOT_ALLOWED = new ErrorCode(1_040_716_005, "已完成或已取消的入库单不允许取消");
    ErrorCode WM_MISC_RECEIPT_LINE_NOT_EXISTS = new ErrorCode(1_040_716_100, "杂项入库单行不存在");
    ErrorCode WM_MISC_RECEIPT_WAREHOUSE_REQUIRED = new ErrorCode(1_040_716_101, "仓库不能为空");
    ErrorCode WM_MISC_RECEIPT_QUANTITY_INVALID = new ErrorCode(1_040_716_102, "入库数量必须大于 0");
    ErrorCode WM_MISC_RECEIPT_DETAIL_NOT_EXISTS = new ErrorCode(1_040_716_200, "杂项入库单明细不存在");

    // ========== MES 仓库管理-发货通知单（1-040-720-000） ==========
    ErrorCode WM_SALES_NOTICE_NOT_EXISTS = new ErrorCode(1_040_720_000, "发货通知单不存在");
    ErrorCode WM_SALES_NOTICE_CODE_DUPLICATE = new ErrorCode(1_040_720_001, "通知单编号重复");
    ErrorCode WM_SALES_NOTICE_STATUS_NOT_ALLOW_DELETE = new ErrorCode(1_040_720_002, "单据状态不允许删除");
    ErrorCode WM_SALES_NOTICE_STATUS_NOT_ALLOW_UPDATE = new ErrorCode(1_040_720_003, "单据状态不允许修改");
    ErrorCode WM_SALES_NOTICE_STATUS_NOT_APPROVED = new ErrorCode(1_040_720_004, "发货通知单不是待出库状态");
    ErrorCode WM_SALES_NOTICE_CLIENT_MISMATCH = new ErrorCode(1_040_720_005, "发货通知单的客户与当前单据不一致");
    ErrorCode WM_SALES_NOTICE_LINE_NOT_EXISTS = new ErrorCode(1_040_720_010, "发货通知单行不存在");
    ErrorCode WM_SALES_NOTICE_LINE_EMPTY = new ErrorCode(1_040_720_011, "发货通知单行为空，不能提交");
    ErrorCode WM_SALES_NOTICE_LINE_NOT_MATCH = new ErrorCode(1_040_720_012, "发货通知单行不属于指定的发货通知单");

    // ========== MES 仓库管理-条码配置（1-040-730-000） ==========
    ErrorCode WM_BARCODE_CONFIG_NOT_EXISTS = new ErrorCode(1_040_730_000, "条码配置不存在");
    ErrorCode WM_BARCODE_CONFIG_BIZ_TYPE_DUPLICATE = new ErrorCode(1_040_730_001, "该业务类型的条码配置已存在");
    ErrorCode WM_BARCODE_CONFIG_HAS_BARCODE = new ErrorCode(1_040_730_002, "该条码配置已被条码记录关联，无法删除");

    // ========== MES 仓库管理-条码清单（1-040-731-000） ==========
    ErrorCode WM_BARCODE_NOT_EXISTS = new ErrorCode(1_040_731_000, "条码不存在");
    ErrorCode WM_BARCODE_ALREADY_EXISTS = new ErrorCode(1_040_731_001, "该业务对象的条码已存在");
    ErrorCode WM_BARCODE_CONTENT_DUPLICATE = new ErrorCode(1_040_731_002, "条码内容已存在");
    ErrorCode BARCODE_BIZ_TYPE_NOT_EXISTS = new ErrorCode(1_040_731_003, "业务类型不能为空");
    ErrorCode BARCODE_BIZ_CODE_NOT_EXISTS = new ErrorCode(1_040_731_004, "业务编码不能为空");
    ErrorCode BARCODE_CONFIG_NOT_EXISTS = new ErrorCode(1_040_731_005, "条码配置不存在");

    // ========== MES 仓库管理-装箱单（1-040-740-000） ==========
    ErrorCode WM_PACKAGE_NOT_EXISTS = new ErrorCode(1_040_740_000, "装箱单不存在");
    ErrorCode WM_PACKAGE_CODE_DUPLICATE = new ErrorCode(1_040_740_001, "装箱单编码已存在");
    ErrorCode WM_PACKAGE_STATUS_NOT_PREPARE = new ErrorCode(1_040_740_002, "只有草稿状态才允许此操作");
    ErrorCode WM_PACKAGE_PARENT_NOT_EXISTS = new ErrorCode(1_040_740_003, "父箱不存在");
    ErrorCode WM_PACKAGE_PARENT_SELF = new ErrorCode(1_040_740_004, "不能选择自己作为父箱");
    ErrorCode WM_PACKAGE_CHILD_HAS_PARENT = new ErrorCode(1_040_740_005, "该装箱单已有父箱，不能重复添加");
    ErrorCode WM_PACKAGE_PARENT_IS_CHILD = new ErrorCode(1_040_740_006, "不能选择子箱的后代作为父箱，会形成环路");
    ErrorCode WM_PACKAGE_CHILD_NOT_FINISHED = new ErrorCode(1_040_740_007, "子箱必须是已完成状态才能添加");
    ErrorCode WM_PACKAGE_HAS_CHILDREN = new ErrorCode(1_040_740_008, "存在子箱，不允许删除");
    ErrorCode WM_PACKAGE_LINE_NOT_EXISTS = new ErrorCode(1_040_740_100, "装箱明细不存在");

    // ========== MES 安全环保检测-污染判定记录（1-040-818-000） ==========
    ErrorCode SET_POLLUTION_CHECK_NOT_EXISTS = new ErrorCode(1_040_818_000, "污染判定记录不存在");
    ErrorCode SET_POLLUTION_CHECK_ALREADY_REVIEWED = new ErrorCode(1_040_818_001, "该记录已复核，禁止修改或删除");
    ErrorCode SET_POLLUTION_CHECK_NOT_REVIEWED = new ErrorCode(1_040_818_002, "该记录尚未复核");
    ErrorCode SET_POLLUTION_CHECK_REVIEW_RESULT_INVALID = new ErrorCode(1_040_818_003, "人工复核结果不合法（仅支持无污染/有污染）");
    ErrorCode SET_POLLUTION_CHECK_STAGE_INVALID = new ErrorCode(1_040_818_004, "环节类型不合法");
    ErrorCode SET_POLLUTION_CHECK_BATCH_REQUIRED = new ErrorCode(1_040_818_005, "有污染判定必须关联真实批次号（中间废弃物环节除外）");
    ErrorCode SET_POLLUTION_CHECK_LOCATION_REQUIRED = new ErrorCode(1_040_818_006, "有污染判定必须填写去向/库位");
    ErrorCode SET_POLLUTION_CHECK_BATCH_NOT_FOUND = new ErrorCode(1_040_818_007, "批次 {} 不存在，无法落地有污染管控，请核对批次号后再复核");
    ErrorCode SET_POLLUTION_CHECK_LOCATION_ID_REQUIRED = new ErrorCode(1_040_818_008, "有污染判定必须选择污染管控（受控/危废）库位，请从库位下拉中选择");
    ErrorCode SET_POLLUTION_CHECK_LOCATION_NOT_FOUND = new ErrorCode(1_040_818_009, "库位 {} 不存在，请重新选择");
    ErrorCode SET_POLLUTION_CHECK_LOCATION_NOT_CONTROLLED = new ErrorCode(1_040_818_010, "库位「{}」不是污染管控库位，有污染品只能入受控/危废库位（防混放）");
    ErrorCode SET_POLLUTION_CHECK_LOCATION_CONTROLLED_FORBIDDEN = new ErrorCode(1_040_818_011, "无污染复核不得占用污染管控库位「{}」，请选择普通库位（防混放）");
    // ========== 成品达标分支（需求文档 §11 用例5 / 团队终版 环节5） ==========
    ErrorCode SET_POLLUTION_CHECK_FINISHED_RESULT_REQUIRED = new ErrorCode(1_040_818_012, "成品环节必须选择达标分支（达标/局部缺陷返工/整体报废）");
    ErrorCode SET_POLLUTION_CHECK_FINISHED_RESULT_INVALID = new ErrorCode(1_040_818_013, "达标分支不合法（仅支持 QUALIFIED/REWORK/SCRAPPED）");
    ErrorCode SET_POLLUTION_CHECK_FINISHED_RESULT_NOT_ALLOWED = new ErrorCode(1_040_818_014, "非成品环节不得填写达标分支");
    ErrorCode SET_POLLUTION_CHECK_SCRAP_LOCATION_REQUIRED = new ErrorCode(1_040_818_015, "整体报废必须指定存放去向（受控存储或直接存储），请选择库位");
    // ========== 在库/入库物品环保检测（判定行锚定批次） ==========
    ErrorCode SET_POLLUTION_CHECK_BATCH_ID_NOT_FOUND = new ErrorCode(1_040_818_016, "批次编号 {} 不存在，无法发起在库检测，请重新选择批次");
    ErrorCode SET_POLLUTION_CHECK_ITEM_REQUIRED = new ErrorCode(1_040_818_017, "必须提供批次编号或物料名称（二者至少其一），否则无法判定");
    ErrorCode SET_POLLUTION_CHECK_LEDGER_CLOSED = new ErrorCode(1_040_818_018, "该判定的污染台账已闭环（当前状态 {}），物品已不在库，库位属历史事实不可调整");
    // @note 人为改数据的操作一律要手写签名（在库环保视图/发起检测）。签名图缺失 = 拒绝执行，
    //       不做"签名可选"的兼容：留一个不签名的口子，事后追责时这一条就是无主的。
    ErrorCode SET_POLLUTION_CHECK_SIGN_REQUIRED = new ErrorCode(1_040_818_019, "该操作会改动环保数据，必须手写签名确认（未收到签名图），已拒绝");
    // ========== 签字即冻结 / 变更单（1-040-818-020 起） ==========
    // 冻结线 = **收口线**（已复核，或已被变更单替代），不是"有没有签字行"：判定在创建时就落了一条
    // 发起检测签字（那是「这批要查」的主张，不是结论），按签字行冻结会让改单/删单从创建那一刻起
    // 永久失效，待复核的草稿连纠错都做不到。
    // 020 留空：原打算给"已收口"单发一个码，实际复用 ALREADY_REVIEWED(001) 更贴切——
    // 用户在前端看到的就是"已复核"，不该因为多了一层变更单概念而换一个陌生的码。
    ErrorCode SET_POLLUTION_CHECK_SUPERSEDED = new ErrorCode(1_040_818_021, "该判定已被变更单 {} 替代，请在新单上操作");
    ErrorCode SET_POLLUTION_CHECK_AMEND_REASON_REQUIRED = new ErrorCode(1_040_818_022, "发起变更必须填写变更事由（说明为什么要改）");
    ErrorCode SET_POLLUTION_CHECK_AMEND_ORIGIN_INVALID = new ErrorCode(1_040_818_023, "被变更的原单不存在或尚未复核；未复核的判定直接改即可，不必开变更单");
    // ========== MES 安全环保检测-污染管控门禁与台账（1-040-819-000） ==========
    ErrorCode MES_POLLUTION_ISSUE_BLOCKED = new ErrorCode(1_040_819_000, "批次 {} 存在有污染环保判定（去向：{}），拒绝领用，请先在污染/危废暂存区完成处理");
    ErrorCode MES_POLLUTION_OUTBOUND_BLOCKED = new ErrorCode(1_040_819_001, "批次 {} 存在有污染/标记环保判定（去向：{}），禁止出库");
    ErrorCode SET_POLLUTION_LEDGER_NOT_EXISTS = new ErrorCode(1_040_819_002, "污染/危废暂存台账记录不存在");
    ErrorCode SET_POLLUTION_LEDGER_STATUS_INVALID = new ErrorCode(1_040_819_003, "台账状态不合法（支持：处置中/已回用/已排放/已处置）");
    ErrorCode SET_POLLUTION_LEDGER_SIGN_REQUIRED = new ErrorCode(1_040_819_004, "标记品终审会改动环保数据，必须手写签名确认（未收到签名图），已拒绝");
    ErrorCode SET_POLLUTION_LEDGER_FLOW_SIGN_REQUIRED = new ErrorCode(1_040_819_005, "危废处置流转会改动环保数据，必须手写签名确认（未收到签名图），已拒绝");
    // ========== MES 安全环保检测-排污许可证（1-040-820-000） ==========
    // ========== MES 安全环保检测-追溯链/称重（1-040-821-000） ==========
    ErrorCode MES_SET_WEIGH_NET_WEIGHT_INVALID = new ErrorCode(1_040_821_000, "净重必须大于 0，请核对毛重与皮重");

    ErrorCode SET_POLLUTION_PERMIT_NOT_EXISTS = new ErrorCode(1_040_820_000, "排污许可证不存在");
    ErrorCode SET_POLLUTION_PERMIT_NO_DUPLICATE = new ErrorCode(1_040_820_001, "排污许可证编号已存在");
    ErrorCode SET_POLLUTION_PERMIT_STATUS_INVALID = new ErrorCode(1_040_820_002, "许可证状态不合法（支持：ACTIVE/EXPIRED/REVOKED）");
    ErrorCode SET_POLLUTION_PERMIT_DATE_INVALID = new ErrorCode(1_040_820_003, "有效期止不能早于有效期始");
    ErrorCode SET_POLLUTION_PERMIT_OUTLET_MISSING = new ErrorCode(1_040_820_004, "必须至少绑定一个排放口编号（outletCode）");
    ErrorCode SET_POLLUTION_PERMIT_LIMIT_ILLEGAL = new ErrorCode(1_040_820_005, "许可排放总量 JSON 格式不合法（[{pollutantCode, pollutantName, annualLimitT, annualUsedT}]）");

    // ========== MES 安全环保检测-空壳表落成（1-040-822-000 起，一表一段） ==========
    // 由 codegen/gen_shell_modules.py 生成，勿手改段号（822/823/... 与表一一对应）
    // ========== 环保检测报告（1-040-822-000） ==========
    ErrorCode SET_ENV_REPORT_NOT_EXISTS = new ErrorCode(1_040_822_000, "环保检测报告不存在");
    ErrorCode SET_ENV_REPORT_NO_DUPLICATE = new ErrorCode(1_040_822_001, "环保检测报告编号已存在");
    ErrorCode SET_ENV_REPORT_REPORT_TYPE_INVALID = new ErrorCode(1_040_822_002, "报告来源不合法");
    ErrorCode SET_ENV_REPORT_STATUS_INVALID = new ErrorCode(1_040_822_003, "状态不合法");
    ErrorCode SET_ENV_REPORT_PERIOD_MISSING = new ErrorCode(1_040_822_004, "报告统计期起止未填写，无法自动取数汇总");
    ErrorCode SET_ENV_REPORT_ARCHIVE_FAILED = new ErrorCode(1_040_822_005, "报告归档失败：文件服务不可用或返回异常");
    // ========== 排放口（1-040-823-000） ==========
    ErrorCode SET_EMISSION_OUTLET_NOT_EXISTS = new ErrorCode(1_040_823_000, "排放口不存在");
    ErrorCode SET_EMISSION_OUTLET_NO_DUPLICATE = new ErrorCode(1_040_823_001, "排放口编号已存在");
    ErrorCode SET_EMISSION_OUTLET_OUTLET_TYPE_INVALID = new ErrorCode(1_040_823_002, "排放类型不合法");
    ErrorCode SET_EMISSION_OUTLET_MONITOR_METHOD_INVALID = new ErrorCode(1_040_823_003, "在线监测方式不合法");
    // ========== 废气监测记录（1-040-824-000） ==========
    ErrorCode SET_EXHAUST_GAS_NOT_EXISTS = new ErrorCode(1_040_824_000, "废气监测记录不存在");
    ErrorCode SET_EXHAUST_GAS_NO_DUPLICATE = new ErrorCode(1_040_824_001, "废气监测记录编号已存在");
    ErrorCode SET_EXHAUST_GAS_COLLECTION_MODE_INVALID = new ErrorCode(1_040_824_002, "采集方式不合法");
    // ========== 废水监测记录（1-040-825-000） ==========
    ErrorCode SET_WASTEWATER_NOT_EXISTS = new ErrorCode(1_040_825_000, "废水监测记录不存在");
    ErrorCode SET_WASTEWATER_NO_DUPLICATE = new ErrorCode(1_040_825_001, "废水监测记录编号已存在");
    ErrorCode SET_WASTEWATER_COLLECTION_MODE_INVALID = new ErrorCode(1_040_825_002, "采集方式不合法");
    // ========== 碳排放核算（1-040-826-000） ==========
    ErrorCode SET_CARBON_EMISSION_NOT_EXISTS = new ErrorCode(1_040_826_000, "碳排放核算不存在");
    ErrorCode SET_CARBON_EMISSION_NO_DUPLICATE = new ErrorCode(1_040_826_001, "碳排放核算编号已存在");
    ErrorCode SET_CARBON_EMISSION_ENERGY_TYPE_INVALID = new ErrorCode(1_040_826_002, "能源类型不合法");
    // ========== 危化品安全检查（1-040-827-000） ==========
    ErrorCode SET_CHEMICAL_SAFETY_NOT_EXISTS = new ErrorCode(1_040_827_000, "危化品安全检查不存在");
    ErrorCode SET_CHEMICAL_SAFETY_NO_DUPLICATE = new ErrorCode(1_040_827_001, "危化品安全检查编号已存在");
    ErrorCode SET_CHEMICAL_SAFETY_RESULT_INVALID = new ErrorCode(1_040_827_002, "结果不合法");

    // ========== 排污许可合规（限值比对 / 年总量红线）1_040_828_xxx ==========
    ErrorCode SET_PERMIT_OUTLET_LIMITS_INVALID = new ErrorCode(1_040_828_000, "排放口许可限值配置格式错误");
    ErrorCode SET_PERMIT_ANNUAL_LIMITS_INVALID = new ErrorCode(1_040_828_001, "许可年排放量配置格式错误");

    // ========== 危废链（台账 / 国家固废联单五方 / 门卫硬放行 / HJ1276 标签）1_040_829_xxx ==========
    ErrorCode SET_HAZWASTE_NOT_EXISTS = new ErrorCode(1_040_829_000, "危废台账记录不存在");
    ErrorCode SET_HAZWASTE_MANIFEST_NO_DUPLICATE = new ErrorCode(1_040_829_001, "危废联单号已存在");
    ErrorCode SET_HAZWASTE_MANIFEST_NOT_EXISTS = new ErrorCode(1_040_829_002, "危废转移联单不存在");
    ErrorCode SET_HAZWASTE_STAGE_INVALID = new ErrorCode(1_040_829_003, "危废台账环节不合法（支持：GENERATED/STORED/TRANSFERRED/DISPOSED）");
    ErrorCode SET_HAZWASTE_STAGE_TRANSITION_INVALID = new ErrorCode(1_040_829_004, "危废台账环节流转不合法：只能按 产生→贮存→转移→处置 逐级推进，不得回退");
    ErrorCode SET_HAZWASTE_MANIFEST_STATUS_INVALID = new ErrorCode(1_040_829_005, "危废联单状态不合法（支持：DRAFT/DECLARED/EFFECTIVE/TRANSFERRED/CLOSED）");
    ErrorCode SET_HAZWASTE_MANIFEST_STATUS_TRANSITION_INVALID = new ErrorCode(1_040_829_006, "危废联单状态流转不合法：只能按 草稿→已申报→已生效→已出厂→已归档 逐级推进，不得回退");
    ErrorCode SET_HAZWASTE_GATE_MANIFEST_MISSING = new ErrorCode(1_040_829_007, "门卫拦截：联单号不存在，无有效转移联单不得出厂");
    ErrorCode SET_HAZWASTE_GATE_NOT_EFFECTIVE = new ErrorCode(1_040_829_008, "门卫拦截：联单尚未生效（须已申报且运输方、接收方均已确认）");
    ErrorCode SET_HAZWASTE_GATE_VEHICLE_MISMATCH = new ErrorCode(1_040_829_009, "门卫拦截：车牌号与联单登记不一致");
    ErrorCode SET_HAZWASTE_GATE_SIGN_INCOMPLETE = new ErrorCode(1_040_829_010, "门卫拦截：四方签字不齐（须移交环保员、押运司机、接收经手人三方先签，门卫方可放行）");
    ErrorCode SET_HAZWASTE_LABEL_ARCHIVE_FAILED = new ErrorCode(1_040_829_011, "危废标签归档失败：文件服务不可用或返回异常");
    ErrorCode SET_HAZWASTE_SIGN_ROLE_INVALID = new ErrorCode(1_040_829_012, "会签角色不合法（支持：HANDOVER/DRIVER/RECEIVER/GUARD）");
    ErrorCode SET_HAZWASTE_PARTY_ROLE_INVALID = new ErrorCode(1_040_829_013, "联单五方角色不合法（支持：GENERATOR/CARRIER/RECEIVER_PARTY/STORAGE/DISPOSER；GENERATOR 的确认走申报接口）");

    // ========== 危化品链（档案 / 分级储存禁配 / 储量上限 / 五双双人签字）1_040_830_xxx ==========
    ErrorCode SET_CHEMICAL_PROFILE_NOT_EXISTS = new ErrorCode(1_040_830_000, "危化品档案不存在");
    ErrorCode SET_CHEMICAL_PROFILE_NO_DUPLICATE = new ErrorCode(1_040_830_001, "危化品档案编号已存在");
    ErrorCode SET_CHEMICAL_PROFILE_STATUS_INVALID = new ErrorCode(1_040_830_002, "危化品档案状态不合法（支持：ENABLED/DISABLED）");
    ErrorCode SET_CHEMICAL_MSDS_MISSING = new ErrorCode(1_040_830_003, "危化品拦截：未挂载 MSDS，无 MSDS 的危化品不得入库");
    ErrorCode SET_CHEMICAL_ZONE_MISMATCH = new ErrorCode(1_040_830_004, "危化品拦截：该物料须存放于防爆区，当前库位专区不符");
    ErrorCode SET_CHEMICAL_INCOMPATIBLE = new ErrorCode(1_040_830_005, "危化品拦截：与该库位已有物料禁配（禁同区），不得同区存放");
    ErrorCode SET_CHEMICAL_QUOTA_EXCEEDED = new ErrorCode(1_040_830_006, "危化品拦截：入库后将超过储量上限");
    ErrorCode SET_CHEMICAL_DOUBLE_SIGN_SAME_USER = new ErrorCode(1_040_830_007, "五双拦截：同一账号不能连签两次，双人制须双账号各签一次");
    ErrorCode SET_CHEMICAL_DOUBLE_SIGN_ACTION_INVALID = new ErrorCode(1_040_830_008, "五双作业类型不合法（支持：RECEIVE 双人收发 / KEEP 双人保管 / LOCK 双人双锁 / ISSUE 双人领料 / TRANSPORT 双人运输）");
    ErrorCode SET_CHEMICAL_QUANTITY_INVALID = new ErrorCode(1_040_830_009, "数量不合法：必须大于 0");
    ErrorCode SET_CHEMICAL_ITEM_BOUND_DUPLICATE = new ErrorCode(1_040_830_010, "该物料已被危化品档案「{}」绑定——一个物料只能按一条危化品档案管，请先解绑");
    ErrorCode SET_CHEMICAL_ITEM_ZONE_MISMATCH = new ErrorCode(1_040_830_011, "危化品拦截：物料「{}」（{}）须存放于{}，库位「{}」是{}，专区不符");
    ErrorCode SET_CHEMICAL_ITEM_INCOMPATIBLE = new ErrorCode(1_040_830_012, "危化品拦截：物料「{}」（{}）与库位「{}」在库的「{}」（{}）禁配，不得同区存放");

    // ========== 治污设施运行管控（台账 / 换炭→HW49 / 停运申报审批 / 同开同停）1_040_831_xxx ==========
    ErrorCode SET_FACILITY_NOT_EXISTS = new ErrorCode(1_040_831_000, "治污设施不存在");
    ErrorCode SET_FACILITY_NO_DUPLICATE = new ErrorCode(1_040_831_001, "治污设施编号已存在");
    ErrorCode SET_FACILITY_TYPE_INVALID = new ErrorCode(1_040_831_002, "治污设施类型不合法（支持：ACTIVATED_CARBON 活性炭吸附 / CATALYTIC_COMBUSTION 催化燃烧 / BAG_FILTER 布袋除尘）");
    ErrorCode SET_FACILITY_SHUTDOWN_ALREADY_PENDING = new ErrorCode(1_040_831_003, "停运申报已存在待审批记录，同一设施同时只能有一份待审申报");
    ErrorCode SET_FACILITY_SHUTDOWN_NOT_PENDING = new ErrorCode(1_040_831_004, "无待审批的停运申报，无法审批（请先提交停运申报）");
    ErrorCode SET_FACILITY_STOP_WITHOUT_APPROVAL = new ErrorCode(1_040_831_005, "未批先停：设施停运必须有已批准的停运申报，不得先停后报");
    ErrorCode SET_FACILITY_CONSUMABLE_QUANTITY_INVALID = new ErrorCode(1_040_831_006, "换下耗材数量不合法：必须大于 0（换炭必须同时登记产生的废活性炭 HW49）");
    ErrorCode SET_FACILITY_CORUN_VIOLATION = new ErrorCode(1_040_831_007, "同开同停违规：产线运行中，配套治污设施不得停运");


    // ========== 第二批空壳表（粉尘/噪声/气体/电气/消防/劳保/压力容器/职业危害/检测计划/检测标准）1_040_832~841_xxx ==========
    ErrorCode SET_DUST_RECORD_NOT_EXISTS = new ErrorCode(1_040_832_000, "粉尘检测记录不存在");
    ErrorCode SET_DUST_RECORD_NO_DUPLICATE = new ErrorCode(1_040_832_001, "粉尘检测记录编号已存在");
    ErrorCode SET_NOISE_RECORD_NOT_EXISTS = new ErrorCode(1_040_833_000, "噪声检测记录不存在");
    ErrorCode SET_NOISE_RECORD_NO_DUPLICATE = new ErrorCode(1_040_833_001, "噪声检测记录编号已存在");
    ErrorCode SET_NOISE_RECORD_COLLECTION_MODE_INVALID = new ErrorCode(1_040_833_002, "采集方式不合法");
    ErrorCode SET_NOISE_RECORD_RESULT_INVALID = new ErrorCode(1_040_833_003, "结果不合法");
    ErrorCode SET_GAS_RECORD_NOT_EXISTS = new ErrorCode(1_040_834_000, "气体检测记录不存在");
    ErrorCode SET_GAS_RECORD_NO_DUPLICATE = new ErrorCode(1_040_834_001, "气体检测记录编号已存在");
    ErrorCode SET_GAS_RECORD_GAS_TYPE_INVALID = new ErrorCode(1_040_834_002, "气体类型不合法");
    ErrorCode SET_ELECTRICAL_RECORD_NOT_EXISTS = new ErrorCode(1_040_835_000, "电气安全检查不存在");
    ErrorCode SET_ELECTRICAL_RECORD_NO_DUPLICATE = new ErrorCode(1_040_835_001, "电气安全检查编号已存在");
    ErrorCode SET_FIRE_CHECK_NOT_EXISTS = new ErrorCode(1_040_836_000, "消防检查记录不存在");
    ErrorCode SET_FIRE_CHECK_NO_DUPLICATE = new ErrorCode(1_040_836_001, "消防检查记录编号已存在");
    ErrorCode SET_FIRE_CHECK_RESULT_INVALID = new ErrorCode(1_040_836_002, "结果不合法");
    ErrorCode SET_PPE_CHECK_NOT_EXISTS = new ErrorCode(1_040_837_000, "劳保用品检查不存在");
    ErrorCode SET_PPE_CHECK_NO_DUPLICATE = new ErrorCode(1_040_837_001, "劳保用品检查编号已存在");
    ErrorCode SET_PPE_CHECK_CHECK_MODE_INVALID = new ErrorCode(1_040_837_002, "检查方式不合法");
    ErrorCode SET_PPE_CHECK_RESULT_INVALID = new ErrorCode(1_040_837_003, "结果不合法");
    ErrorCode SET_PRESSURE_VESSEL_NOT_EXISTS = new ErrorCode(1_040_838_000, "压力容器检查不存在");
    ErrorCode SET_PRESSURE_VESSEL_NO_DUPLICATE = new ErrorCode(1_040_838_001, "压力容器检查编号已存在");
    ErrorCode SET_OCCUPATIONAL_HAZARD_NOT_EXISTS = new ErrorCode(1_040_839_000, "职业危害检测不存在");
    ErrorCode SET_OCCUPATIONAL_HAZARD_NO_DUPLICATE = new ErrorCode(1_040_839_001, "职业危害检测编号已存在");
    ErrorCode SET_OCCUPATIONAL_HAZARD_FACTOR_CATEGORY_INVALID = new ErrorCode(1_040_839_002, "因素类别不合法");
    ErrorCode SET_OCCUPATIONAL_HAZARD_RESULT_INVALID = new ErrorCode(1_040_839_003, "结果不合法");
    ErrorCode SET_PLAN_NOT_EXISTS = new ErrorCode(1_040_840_000, "检测计划不存在");
    ErrorCode SET_PLAN_NO_DUPLICATE = new ErrorCode(1_040_840_001, "检测计划编号已存在");
    ErrorCode SET_PLAN_PLAN_TYPE_INVALID = new ErrorCode(1_040_840_002, "触发类型不合法");
    ErrorCode SET_PLAN_PERIOD_TYPE_INVALID = new ErrorCode(1_040_840_003, "周期类型(周期型)不合法");
    ErrorCode SET_PLAN_STATUS_INVALID = new ErrorCode(1_040_840_004, "状态不合法");
    ErrorCode SET_STANDARD_NOT_EXISTS = new ErrorCode(1_040_841_000, "检测标准不存在");
    ErrorCode SET_STANDARD_NO_DUPLICATE = new ErrorCode(1_040_841_001, "检测标准编号已存在");
    ErrorCode SET_STANDARD_DOMAIN_INVALID = new ErrorCode(1_040_841_002, "检测域不合法");
    ErrorCode SET_STANDARD_PERIOD_TYPE_INVALID = new ErrorCode(1_040_841_003, "周期类型不合法");


    // ========== 应急管理（预案/物资/演练/事件/处置卡）1_040_842~846_xxx ==========
    ErrorCode SET_EMERGENCY_PLAN_NOT_EXISTS = new ErrorCode(1_040_842_000, "应急预案不存在");
    ErrorCode SET_EMERGENCY_PLAN_NO_DUPLICATE = new ErrorCode(1_040_842_001, "应急预案编号已存在");
    ErrorCode SET_EMERGENCY_PLAN_TYPE_INVALID = new ErrorCode(1_040_842_002, "预案类型不合法（支持：COMPREHENSIVE 综合 / SPECIAL 专项 / ONSITE 现场处置）");
    ErrorCode SET_EMERGENCY_PLAN_PUBLISH_DATE_REQUIRED = new ErrorCode(1_040_842_003, "预案发布前必须先填发布日期：备案时限（20 个工作日）与评估周期（3 年）都从发布日起算");
    ErrorCode SET_EMERGENCY_PLAN_ALREADY_FILED = new ErrorCode(1_040_842_004, "预案已备案，不能重复备案（重新备案请先修订）");
    ErrorCode SET_EMERGENCY_PLAN_FILING_NO_REQUIRED = new ErrorCode(1_040_842_005, "备案号不能为空");
    ErrorCode SET_EMERGENCY_PLAN_FILING_BEFORE_PUBLISH = new ErrorCode(1_040_842_006, "备案日期不能早于发布日期");
    ErrorCode SET_EMERGENCY_PLAN_FILED_IMMUTABLE = new ErrorCode(1_040_842_007, "预案已备案：发布日期与版本不可直接改，改了备案号就对不上了；请走「评估修订」，修订后重新备案");
    ErrorCode SET_EMERGENCY_MATERIAL_NOT_EXISTS = new ErrorCode(1_040_843_000, "应急物资不存在");
    ErrorCode SET_EMERGENCY_MATERIAL_NO_DUPLICATE = new ErrorCode(1_040_843_001, "应急物资编号已存在");
    ErrorCode SET_EMERGENCY_MATERIAL_TYPE_INVALID = new ErrorCode(1_040_843_002, "物资类型不合法（支持：DRY_SAND 干沙 / OIL_ABSORBENT 吸油毡 / CHEM_SUIT 防化服 / GAS_MASK 防毒面具 / EXPLOSION_TOOL 防爆工具 / SANDBAG 围堰沙袋 / EYE_WASH 洗眼器 / DRY_POWDER 干粉灭火器）");
    ErrorCode SET_EMERGENCY_DRILL_NOT_EXISTS = new ErrorCode(1_040_844_000, "应急演练不存在");
    ErrorCode SET_EMERGENCY_DRILL_NO_DUPLICATE = new ErrorCode(1_040_844_001, "应急演练编号已存在");
    ErrorCode SET_EMERGENCY_DRILL_TYPE_INVALID = new ErrorCode(1_040_844_002, "演练类型不合法（支持：COMPREHENSIVE 综合 / SPECIAL 专项 / ONSITE 现场处置）");
    ErrorCode SET_EMERGENCY_DRILL_PLAN_NOT_FILED = new ErrorCode(1_040_844_003, "演练必须关联一份已发布/已备案的应急预案（演练是对预案的检验，不能演练一份还没发布的预案）");
    ErrorCode SET_EMERGENCY_DRILL_STATUS_INVALID = new ErrorCode(1_040_844_004, "演练状态流转不合法：只能 已计划→已演练→已闭环，且不得回退");
    ErrorCode SET_EMERGENCY_DRILL_RECTIFY_NOT_DONE = new ErrorCode(1_040_844_005, "演练有整改要求但未整改完成，不能闭环（整改闭环是演练闭环的前置）");
    ErrorCode SET_EMERGENCY_DRILL_DATE_BEFORE_PUBLISH = new ErrorCode(1_040_844_006, "演练日期不能早于关联预案的发布日期");
    ErrorCode SET_EMERGENCY_DRILL_RECORD_INCOMPLETE = new ErrorCode(1_040_844_007, "演练记录不完整：签到表/照片/视频至少留一项，且评估结论不能为空（计划→记录→评估缺一步都不算演练完成）");
    ErrorCode SET_EMERGENCY_EVENT_NOT_EXISTS = new ErrorCode(1_040_845_000, "应急事件不存在");
    ErrorCode SET_EMERGENCY_EVENT_NO_DUPLICATE = new ErrorCode(1_040_845_001, "应急事件编号已存在");
    ErrorCode SET_EMERGENCY_EVENT_TYPE_INVALID = new ErrorCode(1_040_845_002, "事件类型不合法（支持：LEAK 泄漏 / EXCEED 超标 / FACILITY_FAULT 设施故障 / OTHER 其他）");
    ErrorCode SET_EMERGENCY_EVENT_STATUS_INVALID = new ErrorCode(1_040_845_003, "事件状态流转不合法：已上报→处置中→待报告→已闭环，不得回退");
    ErrorCode SET_EMERGENCY_EVENT_WASTE_REQUIRED = new ErrorCode(1_040_845_004, "应急废物必须逐桶登记：每桶给桶码、危废类别与过秤净重，本服务据此写称重记录并落危废台账（应急不绕过管控）");
    ErrorCode SET_EMERGENCY_EVENT_WASTE_WEIGHT_INVALID = new ErrorCode(1_040_845_005, "应急废物净重必须大于 0：放行 0 等于给危废台账留一条空行，“记了”和“没记”就分不清了");
    ErrorCode SET_EMERGENCY_EVENT_WASTE_ALREADY_BUILT = new ErrorCode(1_040_845_006, "本事件的应急废物已建档，不能重复处置（如需补录请先撤销处置）");
    ErrorCode SET_EMERGENCY_EVENT_REPORT_CONTENT_REQUIRED = new ErrorCode(1_040_845_007, "事件报告不能为空：原因/数量/处置/整改四项是闭环的签字依据");
    ErrorCode SET_EMERGENCY_EVENT_CHEMICAL_NOT_EXISTS = new ErrorCode(1_040_845_008, "化学品档案不存在，无法关联 MSDS 与禁配信息");
    ErrorCode SET_EMERGENCY_SOP_SCENARIO_INVALID = new ErrorCode(1_040_846_000, "泄漏处置场景不合法（支持：MDI_LEAK 黑料泄漏 / THINNER_LEAK 稀释剂泄漏 / WASTE_OIL_LEAK 废机油泄漏）；也可以只给 chemicalCode，按危化品档案取处置卡");
    ErrorCode SET_EMERGENCY_SOP_PARAM_REQUIRED = new ErrorCode(1_040_846_001, "取应急处置卡至少要给泄漏场景（scenario）或化学品代码（chemicalCode）之一：两者都没有就不知道该处置什么");
    ErrorCode SET_EMERGENCY_SOP_CHEMICAL_NOT_EXISTS = new ErrorCode(1_040_846_002, "化学品档案不存在，取不到该化学品的 MSDS 与应急处置措施");

    // ========== 追溯反向查来源 1_040_847_xxx ==========

    ErrorCode SET_TRACE_REVERSE_PARAM_REQUIRED = new ErrorCode(1_040_847_000, "反向查来源至少要给危废桶码（containerCode）或联单号（manifestNo）之一：两者都没有就不知道该查哪只桶");

    // ========== 通用业务附件 1_040_848_xxx ==========

    ErrorCode SET_ATTACHMENT_NOT_EXISTS = new ErrorCode(1_040_848_000, "附件不存在（可能已被删除）");
    ErrorCode SET_ATTACHMENT_BIZ_SIGNED = new ErrorCode(1_040_848_001, "所属业务单已签字，附件不可删除（附件是签字结论的证据，删了结论就悬空了）");

    // ========== 在线监控看板 1_040_849_xxx ==========

    ErrorCode SET_MONITOR_EXCEED_NOT_EXISTS = new ErrorCode(1_040_849_000, "超标事件不存在（可能已被删除）");
    ErrorCode SET_MONITOR_EXCEED_STATUS_INVALID = new ErrorCode(1_040_849_001, "超标事件已闭环，不能重复处置：超标记录是审计证据，闭环后不再改状态");
    ErrorCode SET_MONITOR_EXCEED_ALREADY_DISPATCHED = new ErrorCode(1_040_849_002, "该超标事件已派单，不能重复派单（改派请先在追溯链留下原处置人）");
    ErrorCode SET_MONITOR_EXCEED_ALREADY_CLOSED = new ErrorCode(1_040_849_003, "该超标事件已闭环，不能重复闭环");

}
