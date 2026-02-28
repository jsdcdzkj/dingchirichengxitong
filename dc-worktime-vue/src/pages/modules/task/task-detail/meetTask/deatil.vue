<template>
	<ElDescriptions :column="3">
		<ElDescriptionsItem label="类型：">会议任务</ElDescriptionsItem>
		<ElDescriptionsItem label="关联项目：">{{ detail.project_name }}</ElDescriptionsItem>
		<ElDescriptionsItem label="关联计划或者里程碑：">{{
			detail.plan_name || detail.milestoneName
		}}</ElDescriptionsItem>
		<ElDescriptionsItem label="会议时间：">{{ detail.meet_time }}</ElDescriptionsItem>
		<ElDescriptionsItem label="会议时长：">{{ detail.planing_hours ? detail.planing_hours + "分钟" : "--" }}</ElDescriptionsItem>
		<ElDescriptionsItem label="会议地点：">{{ detail.meet_address }}</ElDescriptionsItem>
		<ElDescriptionsItem label="创建时间：">{{ detail.createTime }}</ElDescriptionsItem>
		<ElDescriptionsItem label="创建人：">{{ detail.createUserName }}</ElDescriptionsItem>
		<ElDescriptionsItem label="更新时间：">{{ detail.updateTime }}</ElDescriptionsItem>
	</ElDescriptions>
</template>
<script setup lang="ts">
import { ElDescriptions, ElDescriptionsItem } from "element-plus";
import { ref, watch, nextTick, onMounted } from "vue";
import "viewerjs/dist/viewer.css";
import { api as viewerApi } from "v-viewer";
const props = defineProps({
		detail: {
			type:Object,
			default: {}
		}
	});
</script>
<style scope lang="scss">
.file-item {
	width: 100%;
	height: 40px;
	background: #F8F8F8;
	border-radius: 4px 4px 4px 4px;
}
</style>
