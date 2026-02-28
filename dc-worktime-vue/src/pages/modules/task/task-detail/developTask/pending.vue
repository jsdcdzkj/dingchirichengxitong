<template>
	<ElTable border :data="detail.taskPendingList">
		<ElTableColumn label="序号" type="index" width="80px" align="center"></ElTableColumn>
		<ElTableColumn label="挂起人" align="center" prop="suspend_user_name"></ElTableColumn>
		<ElTableColumn label="恢复人" align="center" prop="recovery_user_name"></ElTableColumn>
		<ElTableColumn label="挂起时间" width="180px" align="center" prop="suspend_time"></ElTableColumn>
		<ElTableColumn label="挂起来源任务" align="center">
			<template #default="scope">
				<span v-if="scope.row.pending_to_type == 1">计划任务</span>
				<span v-if="scope.row.pending_to_type == 2">bug任务</span>
				<span v-if="scope.row.pending_to_type == 3">运维任务</span>
				<span v-if="scope.row.pending_to_type == 4">领导交办任务</span>
				<span v-if="scope.row.pending_to_type == 5">会议任务</span>
			</template>
		</ElTableColumn>
		<ElTableColumn label="恢复时间" align="center" prop="recovery_time"></ElTableColumn>
		<ElTableColumn label="挂起时长" align="center" prop="suspendHours"></ElTableColumn>
	</ElTable>
</template>
<script lang="tsx" setup>
import { ElTable, ElTableColumn } from "element-plus";
const props = defineProps({
	detail: {
		type: Object,
		default: {}
	}
});
</script>
