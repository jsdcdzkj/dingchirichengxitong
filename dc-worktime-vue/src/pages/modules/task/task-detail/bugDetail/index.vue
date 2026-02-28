<template>
	<div>
		<div h="40px" style="position: relative; border-bottom: 1px solid rgba(0, 0, 0, 0.15)">
			<el-tabs v-model="activeName" class="demo-tabs">
				<el-tab-pane label="详情" name="first"></el-tab-pane>
				<!-- <el-tab-pane label="未解决信息" name="second"></el-tab-pane> -->
				<el-tab-pane label="解决记录" name="third"></el-tab-pane>
				<el-tab-pane label="转移记录" name="fourth"></el-tab-pane>
				<el-tab-pane label="工时" name="fifth"></el-tab-pane>
				<el-tab-pane label="挂起信息" name="sixth"></el-tab-pane>
				<el-tab-pane label="完成信息" name="seventh"></el-tab-pane>
			</el-tabs>
			<div class="float-right" v-show="detailInfo.status == '4' && showBtn">
				<ElButton type="success" @click="handleResolved(detailInfo.id)"> 已解决 </ElButton>
				<ElButton type="danger" @click="handleUnsolved(detailInfo.id)"> 未解决 </ElButton>
			</div>
		</div>
		<div class="m-t-32px">
			<!-- bug详情页类型 -->
			<DetailBug :detail="detailInfo" v-if="activeName == 'first'" :testType="detailInfo.assigned_to_user_type"> </DetailBug>
			<NoResolveQuestion
				:detail="detailInfo"
				v-else-if="activeName == 'second'"
				:testType="detailInfo.assigned_to_user_type"
			></NoResolveQuestion>
			<ResolveList
				:detail="detailInfo"
				v-else-if="activeName == 'third'"
				:testType="detailInfo.assigned_to_user_type"
			></ResolveList>
			<MoveList :detail="detailInfo" v-else-if="activeName == 'fourth'"></MoveList>
			<Working :detail="detailInfo" v-else-if="activeName == 'fifth'"> </Working>
			<Pending :detail="detailInfo" v-else-if="activeName == 'sixth'"> </Pending>
			<FinishInfo
				:detail="detailInfo"
				v-else-if="activeName == 'seventh'"
				:testType="detailInfo.assigned_to_user_type"
			></FinishInfo>
		</div>
	</div>
</template>
<script lang="tsx" setup>
	import { onMounted, reactive, ref } from "vue";
	import DetailBug from "./deatilBug.vue";
	import NoResolveQuestion from "./no-resolve-question.vue";
	import ResolveList from "./resolve-list.vue";
	import Working from "./working.vue";
	import MoveList from "./move-list.vue";
	import Pending from "./pending.vue";
	import FinishInfo from "./finishInfoBug.vue";
	import { ElTabs, ElTabPane, ElButton } from "element-plus";
	import { createModelAsync, createDrawerAsync } from "@/core/dialog";
	import Resolved from "./dialog/resolved.vue";
	import Unsolved from "./dialog/unsolved.vue";
	import { detailBug } from "@/api/task/kanban";

	const props = defineProps({
		detailInfo: {
			type: Object,
			default: {}
		},
		showBtn: {
			type: Boolean,
			default: false
		},
		refreshParentParentPage: {
			type: Function,
			required: true
		},
		testType: {
			type: String,
			default: ""
		}
	});

	const activeName = ref("first");
	const handleResolved = (detailInfo: any) => {
		createModelAsync(
			{ title: "已解决", showNext: false, width: "1056px" },
			{ refreshList },
			<Resolved projectId={props.detailInfo.id} />
		);
	};
	const handleUnsolved = (detailInfo: any) => {
		createModelAsync(
			{ title: "未解决", showNext: false, width: "1056px" },
			{ refreshList },
			<Unsolved rowInfo={props.detailInfo} />
		);
	};
	const emit = defineEmits(["updateDetailInfo", "updateShowBtn"]);

	const refreshList = (detailInfo: any) => {
		detailBug(props.detailInfo.id)
			.then((res) => {
				// 触发自定义事件通知父组件更新 detailInfo
				emit("updateDetailInfo", res);
				// 触发自定义事件通知父组件更新 showBtn
				emit("updateShowBtn", false);
			})
			.catch((error) => {
				// 增加错误处理
				console.error("获取任务详情失败:", error);
				// 可以在这里添加更多的错误处理逻辑，比如提示用户
			});
		props.refreshParentParentPage();
	};
</script>
<style lang="scss" scoped>
	.demo-tabs {
		bottom: 0px;
	}
</style>
