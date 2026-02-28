<template>
	<div class="flex" style="min-height: 200px;width: 100%">
		
		<div class="flex" v-if="detail.taskTransferList && detail.taskTransferList.length > 0">
			<div class="left">
				<div class="item" :class="{active: activeIndex == index}" v-for="(item,index) in detail.taskTransferList" :key="index" @click="handleClick(index, item)">
					<span class="m-l-12px">{{item.transfer_time}}</span>
				</div>
			</div>
			<div class="right flex-1" v-if="noRsolveInfo">
				<ElDescriptions :column="1">
					<ElDescriptionsItem label="转移人：">{{ noRsolveInfo.transfer_user_name}}</ElDescriptionsItem>
					<ElDescriptionsItem label="转移至：">{{ noRsolveInfo.transfer_to_user_name}}</ElDescriptionsItem>
					<ElDescriptionsItem label="转移说明：">
						<div style="display:flex" class="desc-img" v-html="noRsolveInfo.description"></div>
					</ElDescriptionsItem>
					<!--<ElDescriptionsItem label="变更人：">{{ item.delay_status}}</ElDescriptionsItem>-->
				</ElDescriptions>
			</div>
		</div>
		<div v-else class="flex flex-1 flex-col flex-items-center flex-justify-center">
			<img src="@/assets/no-Data.png" style="width: 120px;" />
			<span style="margin-top: 10px;color: #999">暂无数据</span>
		</div>
	</div>
</template>
<script setup lang="ts">
import { toRaw, ref, onMounted } from "vue";
import { ElDescriptions, ElDescriptionsItem } from "element-plus";


const props = defineProps({
	detail: {
		type: Object,
		default: {}
	}
});

const noRsolveInfo = ref(props.detail.taskTransferList?.[0])
const activeIndex = ref(0)
const handleClick = (index:any, item:any) => {
	activeIndex.value = index
	noRsolveInfo.value = item
}


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
</style>
