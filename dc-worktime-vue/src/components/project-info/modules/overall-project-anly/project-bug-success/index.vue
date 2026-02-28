<template>
	<div class="flex-1">
		<Title title="BUG修复完成度仪表盘"></Title>
		<div class="m-t-10px flex">
			<div class="bug-success_list">
				<div class="bug-success_item" @click="handleActive(0)">
					<ProjectInfoIcon name="icon-github.png" :width="24" :height="24" />
					<div class="bug-success_item_progress">
						<span class="bug-success_item_progress_left">全部</span>
						<span class="bug-success_item_progress_right"
							>{{ data.front_back_over }}/{{ data.front_back }}</span
						>
					</div>
					<div class="bug-success_item_status" v-if="activeIndex === 0">
						<ElIcon color="#fff" size="12"> <Select /></ElIcon>
					</div>
				</div>
				<div class="bug-success_item" @click="handleActive(1)">
					<ProjectInfoIcon name="icon-javascript-fill.png" :width="24" :height="24" />
					<div class="bug-success_item_progress">
						<span class="bug-success_item_progress_left">前端</span>
						<span class="bug-success_item_progress_right">{{ data.front_over }}/{{ data.front }}</span>
					</div>
					<div class="bug-success_item_status" v-if="activeIndex === 1">
						<ElIcon color="#fff" size="12"> <Select /></ElIcon>
					</div>
				</div>
				<div class="bug-success_item" @click="handleActive(2)">
					<ProjectInfoIcon name="icon-code.png" :width="24" :height="24" />
					<div class="bug-success_item_progress">
						<span class="bug-success_item_progress_left">后端</span>
						<span class="bug-success_item_progress_right">{{ data.back_over }}/{{ data.back }}</span>
					</div>
					<div class="bug-success_item_status" v-if="activeIndex === 2">
						<ElIcon color="#fff" size="12"> <Select /></ElIcon>
					</div>
				</div>
			</div>
			<GaugeBugSuccess v-if="showRef" :data="percent" :type="activeIndex"></GaugeBugSuccess>
		</div>
	</div>
</template>

<script setup lang="ts">
	import { ProjectInfoIcon } from "@/components/icon/icon";
	import { ElIcon } from "element-plus";
	import { Select } from "@element-plus/icons-vue";
	import { repairComplete } from "@/api/project/info";
	import { appInstance } from "@/main";
	import { computed, reactive, ref } from "vue";

	const activeIndex = ref(0);

	const data = reactive({
		front_back_over_percent: 82.05, //全部完成率
		front_over_percent: 92, //前端完成率
		front_over: 23, //前端完成数
		back_over_percent: 64.29, //后端完成率
		back: 14, //后端数
		front_back: 39, //全部数
		front: 25, //前端数
		back_over: 9, //后端完成数
		front_back_over: 32 //全部完成数
	});

	const handleActive = (index: number) => {
		activeIndex.value = index;
		// computedPieDataList();
	};

	const showRef = ref(false);

	const percent = computed(() => {
		if (activeIndex.value === 0) {
			return data.front_back_over_percent;
		} else if (activeIndex.value === 1) {
			return data.front_over_percent;
		} else {
			return data.back_over_percent;
		}
	});

	repairComplete({ pro_project_id: appInstance.projectItem.id }).then((res) => {
		Object.keys(data).forEach((key) => {
			Reflect.set(data, key, res[key]);
		});
		showRef.value = true;
	});
</script>

<style lang="scss" scoped>
	.bug-success_list {
		display: flex;
		justify-content: space-between;
		align-items: center;
		flex-direction: column;
		height: 100%;
		width: 118px;
		gap: 8px;
		.bug-success_item {
			width: 118px;
			cursor: pointer;
			height: 58px;
			background: linear-gradient(180deg, #ffffff 0%, #f9fcff 100%);
			border-radius: 8px 8px 8px 8px;
			border: 1px solid rgba(0, 0, 0, 0.06);
			display: flex;
			align-items: center;
			gap: 10px;
			padding: 0 10px;
			position: relative;
			.bug-success_item_progress {
				display: flex;
				justify-content: flex-start;
				align-items: flex-start;

				flex-direction: column;

				.bug-success_item_progress_left {
					font-size: 12px;
					font-family:
						PingFangSC-Regular,
						PingFang SC;
					font-weight: 400;
					color: #000000;
				}
				.bug-success_item_progress_right {
					font-size: 12px;
					font-family:
						PingFangSC-Regular,
						PingFang SC;
					font-weight: 400;
					color: #000000;
				}
			}
			.bug-success_item_status {
				position: absolute;
				top: 0;
				right: 0;
				width: 25px;
				border-top-right-radius: 8px;
				border-bottom-left-radius: 8px;
				background: linear-gradient(180deg, #50a0ff 0%, #547ff7 100%);
			}
		}
	}
</style>
