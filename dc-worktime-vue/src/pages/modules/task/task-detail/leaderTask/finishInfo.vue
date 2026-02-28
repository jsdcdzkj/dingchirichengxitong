<template>
	<ElDescriptions :column="2" label-width="120px">
		<ElDescriptionsItem label="任务开始时间：">{{ detail.start_time || '--'}}</ElDescriptionsItem>
		<ElDescriptionsItem label="任务完成时间：">{{ detail.completion_time || '--'}}</ElDescriptionsItem>
		<ElDescriptionsItem label="任务终止时间：">{{ detail.final_time || '--'}}</ElDescriptionsItem>
		<ElDescriptionsItem label="任务延期情况：">{{ detail.delayStatusName || '--'}}</ElDescriptionsItem>
		<ElDescriptionsItem label="延期得分：">{{ detail.delay_score && detail.delay_score + '分'  || '--'}}</ElDescriptionsItem>
		<ElDescriptionsItem label="任务质量评价：">{{ detail.qualityName || '--'}}</ElDescriptionsItem>
		<ElDescriptionsItem :span="2" label="任务总评分：">{{ detail.task_rating && detail.task_rating + '分' || '--'}}</ElDescriptionsItem>
		<ElDescriptionsItem :span="2" label="评价意见：">{{ detail.evaluation_opinion || '--'}}</ElDescriptionsItem>
	</ElDescriptions>
</template>
<script setup lang="ts">
import {ElDescriptions, ElDescriptionsItem } from "element-plus"

const props = defineProps({
		detail: {
			type:Object,
			default: {}
		}
	});
</script>

