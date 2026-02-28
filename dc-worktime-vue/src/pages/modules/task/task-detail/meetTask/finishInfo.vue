<template>
	<ElDescriptions :column="1" label-width="120px">
		<ElDescriptionsItem label="开始时间：">{{ detail.meet_start_time || '--'}}</ElDescriptionsItem>
		<ElDescriptionsItem label="完成时间：">{{ detail.meet_completion_time || '--'}}</ElDescriptionsItem>
		<ElDescriptionsItem label="会议时长："> {{ (detail.planing_hours || detail.planing_hours === 0) ? detail.planing_hours + '分钟' : '--'}} </ElDescriptionsItem>
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

