<template>
	<div class="flex" style="min-height: 200px" >
		<template v-if="detail.taskLeadersCallBackList && detail.taskLeadersCallBackList.length">
			<div class="left">
				<div class="item" :class="{active: activeIndex == index}" v-for="(item,index) in detail.taskLeadersCallBackList" :key="index" @click="handleClick(index, item)">
					<span class="m-l-12px">打回人：{{item.call_back_user_name}}</span>
				</div>
			</div>
			<div class="right flex-1">
				<div v-if="backInfo.call_back_opinion">打回意见：{{ backInfo.call_back_opinion }}</div>
				<div class="m-t-24px desc-img" v-html="backInfo.description"></div>
				<div class="m-t-24px">
					<div v-for="(itemFile,index) in backInfo.fileList" :key="index" class="file-item flex flex-justify-between m-b-12px">
						<div class="flex flex-items-center" @click="downFile(itemFile.fileUrl, itemFile.fileName)">
							<img src="@/assets/task/file-icon.png" style="width: 13px;height:13px;margin-left:17px;margin-right: 14px"/>
							<ElLink class="textOverflow">{{ itemFile.fileName }}</ElLink>
						</div>
					</div>
				</div>
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
import {ElLink } from "element-plus"
import { downFile } from "@/core/utils";

const props = defineProps({
	detail: {
		type: Object,
		default: {}
	}
});

const backInfo = ref({})
const activeIndex = ref(0)
const handleClick = (index:any, item:any) => {
	activeIndex.value = index
	backInfo.value = item || {}
}

handleClick(0, props.detail.taskLeadersCallBackList[0])

</script>
<style lang="scss" scoped>
.left {
	border-right: 1px solid rgba(0,0,0,0.06);
	margin-right: 24px;
	padding: 0 24px;
	.item {
		width: 182px;
		height: 44px;
		line-height: 44px;
		background: rgba(255,255,255,0);
		border-radius: 8px 8px 8px 8px;
	}
	.active {
		background: #E8F3FF;
		border-radius: 8px 8px 8px 8px;
	}
}
.right {
	text-align: left;
}
.file-item {
	width: 100%;
	height: 40px;
	background: #F8F8F8;
	border-radius: 4px 4px 4px 4px;
}
.textOverflow {
	white-space: nowrap;
	overflow:hidden;
	text-overflow: ellipsis;
}
</style>
