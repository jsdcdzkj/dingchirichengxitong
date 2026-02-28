<template>
	<ElTable :data="tableList" border stripe>
		<ElTableColumn label="序号" type="index" width="60" align="center"> </ElTableColumn>
		<ElTableColumn prop="source" label="BUG来源" align="center">
			<template #default="{ row }">
				<template v-if="row.source == 1">任务BUG</template>
				<template v-if="row.source == 2">评审BUG</template>
			</template>
		</ElTableColumn>
		<ElTableColumn prop="bug_title" label="BUG标题" align="center">
			<template #default="{ row }">
				{{ row.bug_title }}
			</template>
		</ElTableColumn>
		<ElTableColumn prop="assigned_to_user_type" label="指派至" align="center">
			<template #default="{ row }">
				<template v-if="row.assigned_to_user_type == 1">前端</template>
				<template v-if="row.assigned_to_user_type == 2">后端</template>
			</template>
		</ElTableColumn>
		<ElTableColumn prop="status" label="状态" align="center" width="180">
			<template #default="{ row }">
				{{ statusText[row.status] }}
			</template>
		</ElTableColumn>
	</ElTable>
</template>

<script setup lang="ts">
	import { ElTable, ElTableColumn } from "element-plus";
	import { array } from "vue-types";
	defineProps({
		tableList: array().def([])
	});
	const statusText = {
		1: "待指派",
		2: "待开始/未解决",
		3: "进行中",
		4: "待复测/已完成",
		5: "已解决",
		6: "已挂起",
		7: "已暂停"
	};
</script>
