<template>
	<div class="flex-1">
		<Title title="BUG严重程度占比分析" />
		<div class="m-t-10px">
			<div class="bug-anly-list flex gap-6px">
				<div
					:class="{ active: activeIndex === 0 }"
					class="bug-anly-item flex-1 flex gap-10px items-center"
					@click="handleActive(0)"
				>
					<ProjectInfoIcon name="icon-man-diannao.png" :width="56" :height="56" />
					<div class="bug-anly-item-content flex flex-col">
						<span class="bug-anly-item-value">{{ frontBugBum }}</span>
						<span class="bug-anly-item-title">前端</span>
					</div>
				</div>
				<div
					:class="{ active: activeIndex === 1 }"
					class="bug-anly-item flex-1 flex gap-10px items-center"
					@click="handleActive(1)"
				>
					<ProjectInfoIcon name="icon-p-diannao.png" :width="56" :height="56" />
					<div class="bug-anly-item-content flex flex-col">
						<span class="bug-anly-item-value">{{ backBugBum }}</span>
						<span class="bug-anly-item-title">后端</span>
					</div>
				</div>
			</div>
			<Pie3dBug :data="pieDataList" v-if="showRef" />
		</div>
	</div>
</template>

<script setup lang="ts">
	import { severityPercentage } from "@/api/project/info";
	import { ProjectInfoIcon } from "@/components/icon/icon";
	import { appInstance } from "@/main";
	import { computed, reactive, ref } from "vue";
	const data = reactive({
		user_type_2_severity_2: 0,
		user_type_2_severity_3: 0,
		user_type_1_severity_1: 0,
		user_type_1_severity_2: 0,
		user_type_2_severity_1: 0,
		user_type_1_severity_3: 0
	});
	const activeIndex = ref(0);

	const showRef = ref(false);

	const pieDataList = ref<any>([]);

	const computedPieDataList = () => {
		if (activeIndex.value == 0) {
			pieDataList.value = [
				{
					name: "轻微",
					value: data.user_type_1_severity_1,
					itemStyle: {
						color: "#00d8ff"
					}
				},
				{
					name: "一般",
					value: data.user_type_1_severity_2,
					itemStyle: {
						color: "#018ef1"
					}
				},
				{
					name: "严重",
					value: data.user_type_1_severity_3,
					itemStyle: {
						color: "#f8bc87"
					}
				}
			];
		} else {
			pieDataList.value = [
				{
					name: "轻微",
					value: data.user_type_2_severity_1,
					itemStyle: {
						color: "#00d8ff"
					}
				},
				{
					name: "一般",
					value: data.user_type_2_severity_2,
					itemStyle: {
						color: "#018ef1"
					}
				},
				{
					name: "严重",
					value: data.user_type_2_severity_3,
					itemStyle: {
						color: "#f8bc87"
					}
				}
			];
		}
	};

	const handleActive = (index: number) => {
		activeIndex.value = index;
		computedPieDataList();
	};

	const frontBugBum = computed(() => {
		return data.user_type_1_severity_1 + data.user_type_1_severity_2 + data.user_type_1_severity_3;
	});
	const backBugBum = computed(() => {
		return data.user_type_2_severity_1 + data.user_type_2_severity_2 + data.user_type_2_severity_3;
	});
	severityPercentage({ pro_project_id: appInstance.projectItem.id }).then((res) => {
		Object.keys(data).forEach((key) => {
			Reflect.set(data, key, res[key]);
		});
		showRef.value = true;
		computedPieDataList();
	});
</script>

<style lang="scss" scoped>
	.bug-anly-list {
		.bug-anly-item {
			background: linear-gradient(180deg, #ffffff 0%, #f9fcff 100%);
			border-radius: 8px 8px 8px 8px;
			border: 1px solid rgba(0, 0, 0, 0.06);
			height: 70px;
			padding: 0 10px;
			cursor: pointer;
			.bug-anly-item-content {
				.bug-anly-item-value {
					font-size: 24px;
					font-family:
						PingFangSC-Semibold,
						PingFang SC;
					font-weight: 600;
					color: #1a2029;
				}
				.bug-anly-item-title {
					font-size: 14px;
					font-family:
						PingFangSC-Regular,
						PingFang SC;
					font-weight: 400;
					color: #6e7984;
				}
			}
			&.active {
				background-image: url(/icon/project-info/icon-item-select.png);
				background-size: 100% 100%;
				.bug-anly-item-value {
					color: #fff;
				}
				.bug-anly-item-title {
					color: #fff;
				}
			}
		}
	}
</style>
