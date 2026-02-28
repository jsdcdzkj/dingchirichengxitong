<template>
	<div class="m-t-20px">
			<ElDescriptions :column="3">
				<ElDescriptionsItem label="预估工时：">{{ detail.planHours ||  '--'}}</ElDescriptionsItem>
				<ElDescriptionsItem label="偏差："> {{ detail.estimated_deviation || '--' }} </ElDescriptionsItem>
				<ElDescriptionsItem label="已用工时：">{{ detail.workHours || '--' }}</ElDescriptionsItem>
				<ElDescriptionsItem label="剩余工时："> {{ detail.remaining_work || '--' }} </ElDescriptionsItem>
				<!-- <ElDescriptionsItem label="工时进度："> -- </ElDescriptionsItem> -->
				<ElDescriptionsItem label="偏差比："> {{ detail.estimated_deviation_ratio || '--' }} </ElDescriptionsItem>
			</ElDescriptions>
			<ElTable
					border
					:data="detail.taskStopList"
				>
					<ElTableColumn
						label="序号"
						type="index"
						width="80px"
						align="center"
					></ElTableColumn>
					<ElTableColumn label="开始/恢复人" align="center" prop="createUserName"></ElTableColumn>
					<ElTableColumn label="完成人" align="center" prop="updateUserName">
						<template #default="scope"> {{ scope.row.updateUserName || '--' }} </template>
					</ElTableColumn>
					<ElTableColumn label="开始时间" width="180px" align="center">
						<template #default="scope"> {{ scope.row.start_time }} </template>
					</ElTableColumn>
					<ElTableColumn label="暂停/结束时间" width="180px" align="center">
						<template #default="scope"> {{ scope.row.stop_time }} </template>
					</ElTableColumn>
					<ElTableColumn label="期间工时" align="center"
						><template #default="scope">
						{{ scope.row.workHours }}
						</template></ElTableColumn
					>
					<ElTableColumn label="累计已用工时" align="center"
						><template #default="scope">
						{{ scope.row.totalWorkHours }}
						</template></ElTableColumn
					>
				</ElTable>
		</div>
</template>
<script lang="tsx" setup>
import { ElTable, ElTableColumn, ElDescriptions, ElDescriptionsItem } from "element-plus";
	const props = defineProps({
		detail: {
			type: Object,
			default: {}
		}
	});
</script>
