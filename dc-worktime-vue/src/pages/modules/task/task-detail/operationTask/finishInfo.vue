<template>
	<ElDescriptions :column="1" label-width="120px">
		<ElDescriptionsItem label="任务开始时间：">{{ detail.start_time || '--'}}</ElDescriptionsItem>
		<ElDescriptionsItem label="任务完成时间：">{{ detail.completion_time || '--'}}</ElDescriptionsItem>
		<ElDescriptionsItem label="任务延期情况："> {{ detail.delayStatusName || '--'}} </ElDescriptionsItem>
	</ElDescriptions>
</template>
<script setup lang="ts">
import { ElDescriptions, ElDescriptionsItem } from "element-plus";
const props = defineProps({
		detail: {
			type:Object,
			default: {}
		}
	});
</script>

