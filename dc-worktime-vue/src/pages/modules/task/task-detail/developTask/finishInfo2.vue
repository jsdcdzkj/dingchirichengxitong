<template>
	<ElDescriptions :column="1">
		<ElDescriptionsItem label="任务开始时间：">{{ detail.start_time || '--'}}</ElDescriptionsItem>
		<ElDescriptionsItem label="任务完成时间：">{{ detail.completion_time || '--'}}</ElDescriptionsItem>
		<ElDescriptionsItem label="任务延期情况：">{{ detail.delayStatusName || '--'}}</ElDescriptionsItem>
	</ElDescriptions>
	<ElDivider class="m-b-16px m-t-1px" v-if="detail.taskInterfaceList && detail.taskInterfaceList.length" />
	<div class="flex finish-wrap" v-if="detail.taskInterfaceList && detail.taskInterfaceList.length">
			<div class="left">
				<div class="item" :class="{active: activeIndex == index}" v-for="(item,index) in detail.taskInterfaceList" :key="index" @click="handleClick(index, item)">
					<ElTooltip
						class="box-item"
						effect="dark"
						:content="item.featureName"
					>
						<div class="m-l-12px textOverflow">{{item.featureName}}</div>
					</ElTooltip>
				</div>
			</div>
			<div class="right flex-1">
				<div class="m-b-12px flex flex-items-center"><div class="name">功能名称：</div> <span>{{ info.featureName || '--' }}</span></div>
				<div class="m-b-12px flex flex-items-center"><div class="name">接口名称：</div> <span>{{ info.name || '--' }}</span></div>
				<div class="m-b-12px flex flex-items-center"><div class="name">接口类型：</div> <span>{{ info.method_type || '--' }}</span></div>
				<div class="m-b-12px flex flex-items-center"><div class="name">接口路径：</div> <span>{{ info.url || '--' }}</span></div>
				<div class="m-b-12px flex flex-items-center"><div class="name">Content-Type：</div> <span>{{ info.header_content_type == 1 ? 'application/json' : 'x-www-form-urlencoded' }}</span></div>
				<div class="m-b-12px flex flex-items-center"><div class="name">Params：</div> <span>{{ info.params || '--' }}</span></div>
				<div class="m-b-12px flex flex-items-center"><div class="name">Response：</div> <span>{{ info.result || '--' }}</span></div>

			</div>
		</div>
</template>
<script setup lang="ts">
import { ref } from "vue"
import {ElDescriptions, ElDescriptionsItem, ElDivider, ElTooltip } from "element-plus"
const props = defineProps({
		detail: {
			type:Object,
			default: {}
		}
	});

const info = ref(props.detail.taskInterfaceList?.[0])
const activeIndex = ref(0)
console.log('3333333333333333detail', props.detail)
const handleClick = (index, item) => {
	info.value = item || {}
	activeIndex.value = index
}
</script>
<style lang="scss" scoped>
.left {
	border-right: 1px solid rgba(0,0,0,0.06);
	margin-right: 24px;
	font-size: 14px;
	.item {
		width: 200px;
		height: 44px;
		line-height: 44px;
		color: rgba(0,0,0,0.65);
		background: rgba(255,255,255,0);
		border-bottom: 1px solid rgba(0,0,0,0.06);
	}
	.active {
		background: #E8F3FF;
		color: #1B86FF;
	}
}
.right {
	text-align: left;
	font-size: 14px;

	.name {
		
		height: 44px;
		line-height: 44px;
		width: 108px;
		color: rgba(0,0,0,0.85);
	}
}

.finish-wrap {
	min-height: 200px;
	margin-top:16px;
	border-radius: 8px 8px 8px 8px;
	border: 1px solid rgba(0,0,0,0.15);
	overflow:hidden;
}
.textOverflow {
	white-space: nowrap;
	overflow:hidden;
	text-overflow: ellipsis;
}
</style>
