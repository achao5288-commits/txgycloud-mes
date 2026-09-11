<script lang="ts" setup>
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import type { MesPollutionDischargeApi } from '#/api/mes/safetyEnv/pollutionDischarge';

import { Page } from '@vben/common-ui';

import { useVbenVxeGrid } from '#/adapter/vxe-table';
import { getPollutionDischargePage } from '#/api/mes/safetyEnv/pollutionDischarge';

import { useGridColumns, useGridFormSchema } from './data';

const [Grid] = useVbenVxeGrid({
  formOptions: {
    schema: useGridFormSchema(),
  },
  gridOptions: {
    columns: useGridColumns(),
    height: 'auto',
    keepSource: true,
    proxyConfig: {
      ajax: {
        query: async ({ page }, formValues) =>
          await getPollutionDischargePage({
            pageNo: page.currentPage,
            pageSize: page.pageSize,
            ...formValues,
          }),
      },
    },
    rowConfig: {
      keyField: 'id',
      isHover: true,
    },
    toolbarConfig: {
      refresh: true,
      search: true,
    },
  } as VxeTableGridOptions<MesPollutionDischargeApi.DischargeRecord>,
});
</script>

<template>
  <Page auto-content-height>
    <Grid table-title="排放合规流水（台账流转到「已排放」自动登记：去向 / 执行标准 / 登记人）" />
  </Page>
</template>
