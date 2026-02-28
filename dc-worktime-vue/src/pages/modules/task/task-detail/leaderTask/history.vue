<template>
	<div class="flex" style="min-height:200px">
		<template v-if="detail.taskLeadersResultsList && detail.taskLeadersResultsList.length">
			<div class="left">
				<div class="item" :class="{active: activeIndex == index}" v-for="(item,index) in detail.taskLeadersResultsList" :key="index" @click="handleClick(index, item)">
					<span class="m-l-12px">{{item.createUserName+'：'+item.createTime}}</span>
				</div>
			</div>
			<div class="right flex-1" v-if="resultInfo">
				<div class="m-b-16px desc-img" style="text-align: left" v-html="resultInfo.content"></div>
				<ElDescriptions :column="1">
				<ElDescriptionsItem label="任务附件：">
					<div style="display:inline-block; width: calc(100% - 100px);vertical-align: top;">
						<div v-for="(itemFile,index) in resultInfo.fileList" :key="index" class="file-item flex flex-justify-between m-b-12px">
							<div class="flex flex-items-center"  @click="downFile(itemFile.fileUrl, itemFile.fileName)">
								<img src="@/assets/task/file-icon.png" style="width: 13px;height:13px;margin-right: 14px"/>
								<ElLink class="textOverflow">{{ itemFile.fileName }}</ElLink>
							</div>
						</div>
					</div>

				</ElDescriptionsItem>
				</ElDescriptions>
			</div>
		</template>
			
			<div v-else class="flex flex-1 flex-items-center flex-col flex-justify-center">
				<img src="@/assets/no-Data.png" style="width: 120px;" />
				<span style="margin-top: 10px;color: #999">暂无数据</span>
			</div>
		</div>
</template>
<script setup lang="ts">
import { toRaw, ref, onMounted } from "vue";
import {ElDescriptions, ElDescriptionsItem, ElLink } from "element-plus"
import { downFile } from "@/core/utils";

const props = defineProps({
	detail: {
		type: Object,
		default: {}
	}
});

const resultInfo = ref(props.detail.taskLeadersResultsList?.[0])
const activeIndex = ref(0)
const handleClick = (index:any, item:any) => {
	activeIndex.value = index
	resultInfo.value = item
}

</script>
<style lang="scss" scoped>
.left {
	border-right: 1px solid rgba(0,0,0,0.06);
	margin-right: 24px;
	padding: 0 24px;
	.item {
		height: 44px;
		line-height: 44px;
		background: rgba(255,255,255,0);
		border-radius: 8px 8px 8px 8px;
		padding-right: 16px;
	}
	.active {
		background: #E8F3FF;
		border-radius: 8px 8px 8px 8px;
	}
}
.file-item {
	width: 100%;
	height: 40px;
	background: #F8F8F8;
	border-radius: 4px 4px 4px 4px;
	padding: 0 16px;
}
.textOverflow {
	white-space: nowrap;
	overflow:hidden;
	text-overflow: ellipsis;
}
</style>
