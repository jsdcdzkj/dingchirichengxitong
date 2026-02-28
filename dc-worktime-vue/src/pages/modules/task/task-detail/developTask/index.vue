<template>
	<div>
		<div h="40px" style="position: relative; border-bottom: 1px solid rgba(0, 0, 0, 0.15)">
			<el-tabs v-model="activeName">
				<el-tab-pane label="详情" name="first"></el-tab-pane>
				<el-tab-pane label="关联内容" name="second"></el-tab-pane>
                <el-tab-pane label="转移记录" name="third"></el-tab-pane>
				<el-tab-pane label="工时" name="fourth"></el-tab-pane>
				<el-tab-pane label="挂起信息" name="fifth"></el-tab-pane>
				<el-tab-pane label="完成信息" name="sixth"></el-tab-pane>
			</el-tabs>
		</div>
		<div class="m-t-32px">
			<Detail :detail="detailInfo" v-if="activeName == 'first'"> </Detail>
			<LinkContent :detail="detailInfo" :refreshSearch="refreshSearch" v-else-if="activeName == 'second'"></LinkContent>
			<MoveTask :detail="detailInfo" v-else-if="activeName == 'third'"></MoveTask>
			<Working :detail="detailInfo" v-else-if="activeName == 'fourth'"></Working>
			<Pending :detail="detailInfo" v-else-if="activeName == 'fifth'"> </Pending>
			<FinishInfo2 :detail="detailInfo" v-else-if="activeName == 'sixth' && detailInfo.task_attribute == 2">
			</FinishInfo2>
			<FinishInfo :detail="detailInfo" v-else-if="activeName == 'sixth' && detailInfo.task_attribute != 2">
			</FinishInfo>
		</div>
	</div>
</template>
<script lang="tsx" setup>
	import { onMounted, reactive, ref } from "vue";
	import { ElTabs, ElTabPane } from "element-plus";
	import Detail from "./deatil.vue";
	import Working from "./working.vue";
    import MoveTask from './move-list.vue'
	import LinkContent from "./link-content.vue";
	import Pending from "./pending.vue";
	import FinishInfo from "./finishInfo.vue";
	import FinishInfo2 from "./finishInfo2.vue";

	const props = defineProps({
		detailInfo: {
			type: Object,
			default: {}
		},
		refreshSearch: {
			type: Function,
			default: () => () => {}
		},
	});
	const activeName = ref("first");
</script>
<style lang="scss" scoped>
.demo-tabs {
	bottom: 0px;
}</style>
