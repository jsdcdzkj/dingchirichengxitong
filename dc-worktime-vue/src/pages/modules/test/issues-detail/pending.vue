<template>
	<ElTable border>
		<!-- <ElTable border :data="pageData.dataSource"> -->
		<ElTableColumn type="index" width="50" align="center"> </ElTableColumn>
		<ElTableColumn label="挂起时间" align="center"> </ElTableColumn>
		<ElTableColumn label="挂起来源任务" align="center"> </ElTableColumn>
		<ElTableColumn label="恢复时间" align="center"> </ElTableColumn>
		<ElTableColumn label="挂起时长" align="center"> </ElTableColumn>
	</ElTable>
</template>

<script lang="tsx" setup>
	import { createModelAsync } from "@/core/dialog";
	import { Setting } from "@element-plus/icons-vue";
	import { reactive } from "vue";
	import AddMilestone from "../milestone/dialog/add-milestone.vue";

	const pageData = reactive({
		dataSource: [{}, {}, {}, {}, {}],
		activeName: "first"
	});
</script>
