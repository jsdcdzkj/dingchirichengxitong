<template>
	<ElDescriptions :column="1">
		<ElDescriptionsItem label="已用工时:">里程碑</ElDescriptionsItem>
	</ElDescriptions>
	<ElTable border>
		<!-- <ElTable border :data="pageData.dataSource"> -->
		<ElTableColumn type="index" width="50" align="center"> </ElTableColumn>
		<ElTableColumn label="开始时间" align="center"> </ElTableColumn>
		<ElTableColumn label="暂停/结束时间" align="center"> </ElTableColumn>
		<ElTableColumn label="期间工时" align="center"> </ElTableColumn>
		<ElTableColumn label="累计已用工时" align="center"> </ElTableColumn>
	</ElTable>
</template>

<script lang="tsx" setup>
	import { reactive } from "vue";
</script>
